package com.ampuero.msvc.clientes.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("APIRESTFULL - MSVC - Clientes")
                        .description("Esta es la seccion donde se encuentran todos " +
                                "los endpoints de MSVC clientes")
                        .version("1.0.0")
                );
    }
}
