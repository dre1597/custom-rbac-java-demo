package org.example.customrbacjavademo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.example.customrbacjavademo.configuration.swagger.SwaggerCustomCssInjector;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
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

  @Bean
  public SwaggerIndexTransformer swaggerIndexTransformer(
      final SwaggerUiConfigProperties swaggerUiConfig,
      final SwaggerUiOAuthProperties swaggerUiOAuthProperties,
      final SwaggerWelcomeCommon swaggerWelcomeCommon,
      final ObjectMapperProvider objectMapperProvider
  ) {
    return new SwaggerCustomCssInjector(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
  }
}
