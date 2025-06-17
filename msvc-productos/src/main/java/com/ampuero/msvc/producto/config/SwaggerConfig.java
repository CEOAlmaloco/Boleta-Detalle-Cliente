package com.ampuero.msvc.producto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        Contact contact = new Contact();
        contact.setName("alex ampuero");
        contact.setEmail("al.ampueroa@duocuc.cl");
        return new OpenAPI()
                .info(new Info()
                        .title("API - MSVC - Productos")
                        .version("1.0.0")
                        .description("Este es el microservicio de productos, con el puedes realizar todas las consultas" +
                                " CRUD que necesites")
                        .contact(contact)
                        .summary("esto es una api dentro de un proyecti de MSVC")
                );
    }
}
