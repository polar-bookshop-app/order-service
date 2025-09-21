package com.github.polar.orderservice.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank(message = "Book 'ISBN' must be defined") String bookIsbn,
        @NotNull(message = "The book 'quantity' must be defined")
                @Min(value = 1, message = "You should order at least '1' book")
                @Max(value = 5, message = "You can't order more than '5 books'")
                Integer quantity) {}
