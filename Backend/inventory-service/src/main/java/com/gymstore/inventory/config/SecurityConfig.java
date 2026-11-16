package com.gymstore.inventory.config;

import com.gymstore.common.security.KeycloakRealmRolesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Resource server cho inventory-service.
 * - /api/inventory/** (list, xem, cập nhật): STAFF/ADMIN (@PreAuthorize ở controller).
 * - /internal/inventory/** (reserve/commit/release): chỉ workflow-service gọi nội bộ,
 *   KHÔNG lộ qua gateway. Trong demo để permitAll và dựa vào cô lập mạng.
 *   (Sản phẩm: prod nên yêu cầu token service-account từ workflow-service.)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/inventory/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakConverter())));
        return http.build();
    }

    @Bean
    Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {
        return KeycloakRealmRolesConverter.forServlet();
    }
}
