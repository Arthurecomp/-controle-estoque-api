package com.arthur.controle_estoque_api;

import com.arthur.controle_estoque_api.config.TestMockConfig;
import com.arthur.controle_estoque_api.config.TestRabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")//Use o appclication-test.yml
@Import(TestMockConfig.class)
class ControleEstoqueApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
