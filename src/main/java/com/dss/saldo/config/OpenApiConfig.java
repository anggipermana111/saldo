package com.dss.saldo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DSS Investment Portfolio API")
                        .version("1.0")
                        .description("API untuk simulasi perhitungan portofolio investasi (Live Coding Interview)")
                        .contact(new Contact()
                                .name("Anggi Permana")
                                .email("anggipermana490@gmail.com")));
    }
}
