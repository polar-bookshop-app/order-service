package com.github.polar.orderservice.book;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@TestMethodOrder(MethodOrderer.Random.class)
public class BookClientTest {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient =
                WebClient.builder().baseUrl(mockWebServer.url("/").uri().toString()).build();
        bookClient = new BookClient(webClient);
    }

    @AfterEach
    void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getBookInfoNormalCase() {
        final String isbn = "0000000001";

        var body =
                """
        {
          "id": 20,
          "isbn": "%s",
          "title": "Effective Java 3rd Edition",
          "author": "Joshua Bloch",
          "price": 43.86,
          "publisher": "Manning",
          "createdDate": "2025-09-26T08:21:32.363263Z",
          "lastModifiedDate": "2025-09-26T08:21:32.363263Z",
          "version": 1
        }
        """
                        .formatted(isbn);

        var mockResponse =
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(body);

        mockWebServer.enqueue(mockResponse);

        var maybeBookInfo = bookClient.getBookInfo(isbn);

        StepVerifier.create(maybeBookInfo)
                .expectNextMatches(
                        b -> {
                            assertThat(b.isOk()).isTrue();

                            var bookInfo = b.ok();

                            assertThat(bookInfo)
                                    .isNotNull()
                                    .hasFieldOrPropertyWithValue("isbn", "0000000001")
                                    .hasFieldOrPropertyWithValue(
                                            "name", "Effective Java 3rd Edition")
                                    .hasFieldOrPropertyWithValue("author", "Joshua Bloch")
                                    .hasFieldOrPropertyWithValue("price", new BigDecimal("43.86"));
                            return true;
                        })
                .verifyComplete();
    }

    @Test
    void getBookInfoNotFoundCase() {
        final String isbn = "0000000000";

        var body =
                """
        {
          "type": "https://example.com/probs/not-found",
          "title": "Book not found",
          "status": "NOT_FOUND",
          "detail": "Can't find book with isbn '0000000000'",
          "instance": "/books/%s",
          "context": {}
        }
        """
                        .formatted(isbn);

        var mockResponse =
                new MockResponse()
                        .setResponseCode(404)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(body);

        mockWebServer.enqueue(mockResponse);

        var maybeBookInfo = bookClient.getBookInfo(isbn);

        StepVerifier.create(maybeBookInfo)
                .expectNextMatches(
                        result -> {
                            assertThat(result.isError()).isTrue();
                            var error = result.error();
                            assertThat(error.description())
                                    .isEqualTo(
                                            String.format("Can't find book with isbn '%s'", isbn));

                            return true;
                        })
                .verifyComplete();
    }

    @Test
    void getBookInfoTimeoutCase() {
        final String isbn = "0000000001";

        // Simulate a delayed response to trigger a timeout
        var mockResponse =
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{}")
                        .setBodyDelay(2500, TimeUnit.MILLISECONDS); // delay the response by 2500ms

        mockWebServer.enqueue(mockResponse);

        var maybeBookInfo = bookClient.getBookInfo(isbn);

        StepVerifier.create(maybeBookInfo)
                .expectNextMatches(
                        result -> {
                            assertThat(result.isError()).isTrue();
                            var error = result.error();
                            assertThat(error.description()).isEqualTo("Call Timed Out");
                            return true;
                        })
                .verifyComplete();
    }

    @Test
    void getBookInfoException() {
        final String isbn = "0000000001";

        // Simulate an HTTP 5xx response
        var mockResponse =
                new MockResponse()
                        .setResponseCode(500)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{}");

        // Enqueue the mock response RETRY_COUNT + 1 times to simulate the retry failures
        for (int i = 0; i < BookClient.RETRY_COUNT + 1; i++) {
            mockWebServer.enqueue(mockResponse);
        }

        var maybeBookInfo = bookClient.getBookInfo(isbn);

        StepVerifier.create(maybeBookInfo)
                .expectNextMatches(
                        result -> {
                            assertThat(result.isError()).isTrue();
                            var error = result.error();
                            assertThat(error.description()).isEqualTo("Generic Error");
                            return true;
                        })
                .verifyComplete();
    }
}
