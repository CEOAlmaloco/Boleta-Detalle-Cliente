package com.ampuero.msvc.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class MsvcClientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcClientesApplication.class, args);
	}

}
