package com.pk.vgsms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class JacksonMapper {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
