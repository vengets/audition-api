package com.audition;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@OpenAPIDefinition(
    info = @Info(
        title = "Audition API",
        version = "1.0",
        description = "Swagger documentation for the Audition API"
    )
)

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.audition")
public class AuditionApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AuditionApplication.class, args);
    }

}
