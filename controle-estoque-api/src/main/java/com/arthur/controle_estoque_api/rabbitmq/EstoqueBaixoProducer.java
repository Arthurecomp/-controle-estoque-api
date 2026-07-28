package com.arthur.controle_estoque_api.rabbitmq;

import com.arthur.controle_estoque_api.event.EstoqueBaixoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class EstoqueBaixoProducer {

    private final RabbitTemplate rabbitTemplate;


    public EstoqueBaixoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void enviarEstoqueBaixo(
            EstoqueBaixoEvent evento
    ) {

        rabbitTemplate.convertAndSend(
                "estoque.exchange",
                "estoque.baixo",
                evento
        );

    }
}