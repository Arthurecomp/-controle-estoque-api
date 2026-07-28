package com.arthur.controle_estoque_api.config;

import com.arthur.controle_estoque_api.rabbitmq.EstoqueBaixoProducer;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMockConfig {

    @Bean
    public EstoqueBaixoProducer estoqueBaixoProducer() {
        return org.mockito.Mockito.mock(EstoqueBaixoProducer.class);
    }
}