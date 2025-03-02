package org.example.customrbacjavademo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI() {

    var info = new Info()
        .title("Custom RBAC API")
        .description("Custom RBAC API using Spring")
        .version("v0.0.1");

    return new OpenAPI().info(info);
  }
}
