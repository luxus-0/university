package com.company.university.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfigurationProperties {
    private String secret;
    private String issuer;
    private String milliseconds;
    private long expirationMs;
}
