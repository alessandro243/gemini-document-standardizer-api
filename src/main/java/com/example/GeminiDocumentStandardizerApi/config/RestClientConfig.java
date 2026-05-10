package com.example.GeminiDocumentStandardizerApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public static RestClient rest_Client(){
        return RestClient.builder().build();
    }
}
