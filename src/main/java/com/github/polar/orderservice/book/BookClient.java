package com.github.polar.orderservice.book;

import com.github.polar.orderservice.domain.BookInfo;
import com.github.polar.orderservice.domain.BookInfoError;
import com.github.polar.orderservice.util.Result;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class BookClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String GET_BOOK_BY_ISBN = "/books/%s";

    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Result<BookInfo, BookInfoError>> getBookInfo(String isbn) {

        /*
         * If initial call failed, make 3 more attempts with exponential backoff delay: 100ms, 200ms, 400ms each
         * 100 + 200 + 400 = 700 ms
         */
        final long retryCount = 3L;
        final long retryExponentialBackoffDelay = 100L;

        Mono<BookInfo> bookInfoMono =
                webClient
                        .get()
                        .uri(String.format(GET_BOOK_BY_ISBN, isbn))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(BookInfo.class);

        Mono<Result<BookInfo, BookInfoError>> bookInfoRes =
                bookInfoMono.flatMap(bookInfo -> Mono.just(Result.ok(bookInfo)));

        // Handle 'NotFound', no retry
        bookInfoRes =
                bookInfoRes.onErrorResume(
                        WebClientResponseException.NotFound.class,
                        ex -> Mono.just(Result.error(new BookInfoError("Book Not Found"))));

        // Timeout for a SINGLE retry
        bookInfoRes =
                bookInfoRes.timeout(
                        Duration.ofMillis(
                                calculateMaxPossibleDelay(
                                        retryCount, retryExponentialBackoffDelay)),
                        Mono.just(Result.error(new BookInfoError("Retry Timed Out"))));

        // Retry, exponential backoff
        bookInfoRes =
                bookInfoRes.retryWhen(
                        Retry.backoff(retryCount, Duration.ofMillis(retryExponentialBackoffDelay)));

        // Handle generic Exception
        bookInfoRes =
                bookInfoRes.onErrorResume(
                        Exception.class,
                        notUsed -> Mono.just(Result.error(new BookInfoError("Generic Error"))));

        return bookInfoRes;
    }

    /**
     * Calculates the maximum possible delay based on the given number of attempts and an initial
     * duration.
     *
     * <p>The delay is computed by doubling the {@code duration} for each attempt after the first
     * one, up to the specified {@code maxAttempts}. This effectively simulates an exponential
     * backoff strategy.
     *
     * <p>For example:
     *
     * <ul>
     *   <li>If {@code maxAttempts = 1}, the result is {@code duration}.
     *   <li>If {@code maxAttempts = 3} and {@code duration = 100}, the result is {@code 400} (100 →
     *       200 → 400).
     * </ul>
     *
     * @param maxAttempts the maximum number of attempts; must be greater than or equal to 1
     * @param duration the initial delay duration
     * @return the maximum possible delay after applying exponential backoff
     */
    private static long calculateMaxPossibleDelay(long maxAttempts, long duration) {
        assert maxAttempts >= 1;
        assert duration > 0L;

        long curDuration = duration;

        for (int i = 1; i < maxAttempts; i++) {
            curDuration *= 2;
        }

        LOGGER.info("calculateMaxPossibleDelay: {}", curDuration);
        return curDuration;
    }
}
