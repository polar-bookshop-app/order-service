package com.github.polar.orderservice.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Mono<Order> submitOrder(String bookIsbn, Integer quantity) {
        // TODO: query catalog-service here
        var rejectedOrder = Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
        return orderRepository.save(rejectedOrder);
    }
}
