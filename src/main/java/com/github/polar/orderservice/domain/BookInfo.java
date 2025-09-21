package com.github.polar.orderservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record BookInfo(
        @JsonProperty("isbn") String isbn,
        @JsonProperty("title") String name,
        @JsonProperty("author") String author,
        @JsonProperty("price") BigDecimal price) {}
