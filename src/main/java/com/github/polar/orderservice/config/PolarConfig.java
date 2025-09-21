package com.github.polar.orderservice.config;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "polar")
public record PolarConfig(@NotNull URI catalogServiceUrl) {}
