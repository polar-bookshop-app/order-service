package com.github.polar.orderservice.web;

import com.github.polar.orderservice.domain.Order;
import com.github.polar.orderservice.domain.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return orderService.submitOrder(
                createOrderRequest.bookIsbn(), createOrderRequest.quantity());
    }
}
