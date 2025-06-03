package com.ampuero.msvc.detalle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsvcDetalleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcDetalleApplication.class, args);
    }

}
