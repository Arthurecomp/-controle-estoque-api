package com.arthur.controle_estoque_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")//Use o appclication-test.yml
@ImportAutoConfiguration(exclude = {
		RabbitAutoConfiguration.class
})
class ControleEstoqueApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
