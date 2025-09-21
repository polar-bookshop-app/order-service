package com.github.polar.orderservice.domain;

import com.github.polar.orderservice.config.PolarConfig;
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
    private final PolarConfig polarConfig;

    public OrderService(OrderRepository orderRepository, PolarConfig polarConfig) {
        this.orderRepository = orderRepository;
        this.polarConfig = polarConfig;
    }

    @Transactional
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Mono<Order> submitOrder(String bookIsbn, Integer quantity) {

        LOGGER.info("HTTP call to {}", polarConfig.catalogServiceUrl());

        // TODO: query catalog-service here
        var rejectedOrder = Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
        return orderRepository.save(rejectedOrder);
    }
}
