package com.example.fyp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // working code to create user from lambda trigger using symmetric key
    // @Value("${jwt.secret}")
    // private String jwtSecret;

    @Value("${cognito.region}")
    private String cognitoRegion;

    @Value("${cognito.userPoolId}")
    private String userPoolId;

    // working code
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("Admin")
                .requestMatchers("/api/lambda/**").permitAll() // Ensure the endpoint for lambda is accessible
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        return http.build();
    }

    // working code to authorize retrieve daries
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "https://cognito-idp." + cognitoRegion + ".amazonaws.com/" + userPoolId + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
    
    // working code to create user from lambda trigger using symmetric key
    // @Bean
    // public JwtDecoder jwtDecoder() {
    //     return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")).build();
    // }

}
