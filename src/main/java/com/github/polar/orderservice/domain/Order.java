package com.github.polar.orderservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public record Order(
        @Id Long id,
        String bookIsbn,
        String bookName,
        BigDecimal bookPrice,
        Integer quantity,
        OrderStatus status,
        @CreatedDate Instant createdDate,
        @LastModifiedDate Instant lastModifiedDate,
        @Version int version) {

    public static Order accepted(
            String bookIsbn, String bookName, BigDecimal bookPrice, Integer quantity) {
        return new Order(
                null, bookIsbn, bookName, bookPrice, quantity, OrderStatus.ACCEPTED, null, null, 0);
    }

    public static Order rejected(String bookIsbn, Integer quantity) {
        return new Order(null, bookIsbn, null, null, quantity, OrderStatus.REJECTED, null, null, 0);
    }
}
