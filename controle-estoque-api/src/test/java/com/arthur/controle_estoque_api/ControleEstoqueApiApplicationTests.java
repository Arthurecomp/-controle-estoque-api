package com.arthur.controle_estoque_api;

import com.arthur.controle_estoque_api.config.TestMockConfig;

import org.junit.jupiter.api.Test;

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
