package com.github.polar.orderservice.book;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

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

        final String isbn = "0321349601";

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
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(body);

        mockWebServer.enqueue(mockResponse);

        var maybeBookInfo = bookClient.getBookInfo(isbn);

        StepVerifier.create(maybeBookInfo)
                .expectNextMatches(
                        b -> {
                            return b.isOk() && b.ok().isbn().equals("0321349601");
                        })
                .verifyComplete();
    }
}
