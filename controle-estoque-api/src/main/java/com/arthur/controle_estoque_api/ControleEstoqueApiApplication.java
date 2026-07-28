package com.arthur.controle_estoque_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class ControleEstoqueApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControleEstoqueApiApplication.class, args);

	}

}
