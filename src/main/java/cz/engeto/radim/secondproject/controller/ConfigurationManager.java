package cz.engeto.radim.secondproject.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationManager {
    @Bean
    public static String fileWithPersonIds(){
        return "personIds.txt";
    }
}
