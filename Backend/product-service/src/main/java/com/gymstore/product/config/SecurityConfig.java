package com.gymstore.product.config;

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
 * Resource server (servlet) cho product-service.
 * - GET sản phẩm/danh mục/thương hiệu: public.
 * - Viết/xoá: yêu cầu JWT + @PreAuthorize('ADMIN') ở controller.
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
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories", "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/brands", "/api/brands/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakConverter())));
        return http.build();
    }

    /** Chuyển realm_access.roles trong JWT thành GrantedAuthority (ROLE_xxx). */
    @Bean
    Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {
        return KeycloakRealmRolesConverter.forServlet();
    }
}
