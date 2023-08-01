package ru.emelkrist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.emelkrist.utils.CryptoTool;

@Configuration
public class RestServiceConfiguration {

    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool getCryptoTool(){
        return new CryptoTool(salt);
    }
}