package com.ampuero.msvc.boleta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("APIRESTFULL - MSVC - Boletas")
                        .description("Esta es la seccion donde se encuentran todos " +
                                "los endpoints de MSVC Boletas")
                        .version("1.0.0")
                );
    }
}
