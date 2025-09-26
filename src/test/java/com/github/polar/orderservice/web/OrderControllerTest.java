package com.github.polar.orderservice.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.polar.orderservice.domain.Order;
import com.github.polar.orderservice.domain.OrderService;
import com.github.polar.orderservice.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @Autowired private WebTestClient webClient;

    @Autowired private OrderService orderService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public OrderService mockOrderService() {
            return Mockito.mock(OrderService.class);
        }
    }

    @Test
    void submitOrderRejectedCase() {

        var isbn = "0000000001";
        var quantity = 3;

        when(orderService.submitOrder(isbn, quantity))
                .thenReturn(Mono.just(Order.rejected(isbn, 3)));

        webClient
                .post()
                .uri("/orders")
                .bodyValue(new CreateOrderRequest(isbn, quantity))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Order.class)
                .value(
                        order -> {
                            assertThat(order).isNotNull();
                            assertThat(order.status()).isEqualTo(OrderStatus.REJECTED);
                            assertThat(order.bookIsbn()).isEqualTo(isbn);
                            assertThat(order.quantity()).isEqualTo(quantity);
                        });

        verify(orderService, times(1)).submitOrder(isbn, quantity);
    }
}
