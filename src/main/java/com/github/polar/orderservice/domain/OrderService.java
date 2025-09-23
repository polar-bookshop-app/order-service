package com.github.polar.orderservice.domain;

import com.github.polar.orderservice.book.BookClient;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    public OrderService(OrderRepository orderRepository, BookClient bookClient) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }

    @Transactional
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Mono<Order> submitOrder(String bookIsbn, Integer quantity) {
        var resultMono = bookClient.getBookInfo(bookIsbn);

        var orderMono =
                resultMono.map(
                        result -> {
                            if (result.isOk()) {
                                BookInfo bookInfo = result.ok();
                                return Order.accepted(
                                        bookIsbn, bookInfo.name(), bookInfo.price(), quantity);
                            } else {
                                LOGGER.warn("Book info failed with {}", result.err().description());
                                return Order.rejected(bookIsbn, quantity);
                            }
                        });

        return orderMono.flatMap(orderRepository::save);
    }
}
