package com.gymstore.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .cors(Customizer.withDefaults())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/brands", "/api/brands/**").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> {}));
        return http.build();
    }
}
