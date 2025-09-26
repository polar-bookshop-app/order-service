package com.github.polar.orderservice.domain;

import com.github.polar.orderservice.config.DataConfig;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
public class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgresql =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6"));

    @Autowired private OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryTest::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        return String.format(
                "r2dbc:postgresql://%s:%s/%s",
                postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
                postgresql.getDatabaseName());
    }

    @Test
    void saveAcceptedOrder() {
        var acceptedOrder =
                Order.accepted(
                        "0000000001", "Hacker's Delight 2nd Edition", new BigDecimal("35.99"), 3);
        StepVerifier.create(orderRepository.save(acceptedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.ACCEPTED))
                .verifyComplete();
    }

    @Test
    void saveRejectedOrder() {
        var rejectedOrder = Order.rejected("0000000001", 3);
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }
}
