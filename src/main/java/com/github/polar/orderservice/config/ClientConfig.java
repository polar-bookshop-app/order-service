package com.github.polar.orderservice.config;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    WebClient webClient(PolarConfig polarConfig, WebClient.Builder builder) {
        LOGGER.info(
                "Configuring WebClient with URL '{}'", polarConfig.catalogServiceUrl().toString());
        return builder.baseUrl(polarConfig.catalogServiceUrl().toString()).build();
    }
}
