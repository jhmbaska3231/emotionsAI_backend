package com.example.fyp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${cognito.region}")
    private String cognitoRegion;

    @Value("${cognito.userPoolId}")
    private String userPoolId;

    // working for but single tenant/issuer only
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(authorize -> authorize
    //             .requestMatchers("/api/auth/**").permitAll()
    //             .requestMatchers("/api/admin/**").hasRole("Admin")
    //             .requestMatchers("/api/lambda/**").permitAll()
    //             .anyRequest().authenticated()
    //         )
    //         .oauth2ResourceServer(oauth2 -> oauth2
    //             .jwt(jwt -> jwt.decoder(jwtDecoder()))
    //         );

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("Admin")
                .requestMatchers("/api/lambda/**").permitAll()
                .requestMatchers("/api/forms/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationManagerResolver(authenticationManagerResolver)
            );

        return http.build();
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(JwtDecoder jwtDecoder, JwtDecoder jwtDecoderLambda) {
        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
        authenticationManagers.put("default", jwtAuthenticationManager(jwtDecoder));
        authenticationManagers.put("lambda", jwtAuthenticationManager(jwtDecoderLambda));

        return request -> {
            String lambdaRequestHeader = request.getHeader("X-Lambda-Request");
            String key = (lambdaRequestHeader != null && lambdaRequestHeader.equals("true")) ? "lambda" : "default";
            return Optional.ofNullable(authenticationManagers.get(key))
                .orElseThrow(() -> new IllegalArgumentException("Unknown authentication manager"));
        };
    }

    private AuthenticationManager jwtAuthenticationManager(JwtDecoder jwtDecoder) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "https://cognito-idp." + cognitoRegion + ".amazonaws.com/" + userPoolId + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
    
    @Bean
    public JwtDecoder jwtDecoderLambda() {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")).build();
    }

}
