package com.arthur.controle_estoque_api.rabbitmq;

import com.arthur.controle_estoque_api.event.EstoqueBaixoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class EstoqueBaixoConsumer {


    @RabbitListener(
            queues = "estoque-baixo.queue"
    )
    public void consumir(EstoqueBaixoEvent evento){

        System.out.println(
                "Produto com estoque baixo: "
                        + evento.nomeProduto()
                        + " quantidade: "
                        + evento.quantidadeAtual()
        );

    }
}