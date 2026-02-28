package com.yusuf.route.transportation.configuration;

import com.yusuf.route.transportation.security.service.JwtService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    @Bean
    public JwtService jwtService(JwtProperties props) {
        return new JwtService(props.issuer(), props.secret(), props.accessTokenTtlMinutes());
    }
}