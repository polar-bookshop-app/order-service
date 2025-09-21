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
        return bookClient
                .getBookInfo(bookIsbn)
                .map(
                        bookInfo -> {
                            var order =
                                    Order.of(
                                            bookIsbn,
                                            bookInfo.name(),
                                            bookInfo.price(),
                                            quantity,
                                            OrderStatus.ACCEPTED);
                            return order;
                        })
                .flatMap(orderRepository::save);
    }
}
