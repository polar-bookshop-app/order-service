package com.github.polar.orderservice.web;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.github.polar.orderservice.domain.Order;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
public class OrderJsonTest {

    @Autowired private JacksonTester<Order> orderJackson;

    @Test
    void serializeOrder() throws IOException {
        var order =
                Order.accepted(
                        "00000000001", "Build a Large Language Model", new BigDecimal("51.67"), 3);

        var orderJson = orderJackson.write(order);

        assertThat(orderJson).extractingJsonPathStringValue("@.bookIsbn").isEqualTo("00000000001");

        assertThat(orderJson)
                .extractingJsonPathStringValue("@.bookName")
                .isEqualTo("Build a Large Language Model");

        assertThat(orderJson).extractingJsonPathNumberValue("@.bookPrice").isEqualTo(51.67);

        assertThat(orderJson).extractingJsonPathNumberValue("@.quantity").isEqualTo(3);
    }

    @Autowired private JacksonTester<CreateOrderRequest> createOrderRequestJackson;

    @Test
    void deserializeCreateOrderRequest() throws IOException {
        var createOrderJson =
                """
                {
                  "bookIsbn": "0000000001",
                  "quantity": 3
                }
                """;

        assertThat(createOrderRequestJackson.parse(createOrderJson))
                .isNotNull()
                .isEqualTo(new CreateOrderRequest("0000000001", 3));
    }
}
