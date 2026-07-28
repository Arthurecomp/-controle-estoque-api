package com.arthur.controle_estoque_api.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRabbitConfig {

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate();
    }
}