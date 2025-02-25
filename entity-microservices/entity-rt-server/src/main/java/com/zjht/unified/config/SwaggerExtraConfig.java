package com.zjht.unified.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.util.ReferenceSerializationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;

@Configuration
public class SwaggerExtraConfig {

    @Bean
    public SwaggerJacksonModuleRegistrar swaggerJacksonModuleRegistrar() {
        return new SwaggerJacksonModuleRegistrar();
    }

    public static class SwaggerJacksonModuleRegistrar implements JacksonModuleRegistrar {

        @Override
        public void maybeRegisterModule(ObjectMapper objectMapper) {
            ReferenceSerializationConfigurer.serializeAsComputedRef(objectMapper);
        }
    }
}
