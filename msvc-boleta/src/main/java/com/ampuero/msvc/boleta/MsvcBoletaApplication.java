package com.ampuero.msvc.boleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcBoletaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsvcBoletaApplication.class, args);
    }
}
