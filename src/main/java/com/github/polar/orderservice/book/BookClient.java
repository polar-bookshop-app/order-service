package com.github.polar.orderservice.book;

import com.github.polar.orderservice.domain.BookInfo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BookClient {

    private static final String GET_BOOK_BY_ISBN = "/books/%s";

    private final WebClient webClient;

    public BookClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BookInfo> getBookInfo(String isbn) {
        return webClient
                .get()
                .uri(String.format(GET_BOOK_BY_ISBN, isbn))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(BookInfo.class);
    }
}
