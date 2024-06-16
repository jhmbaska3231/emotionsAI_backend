package com.example.fyp;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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

    @Value("${lambda.public.key}")
    private String publicKeyContent;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("Admin")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "https://cognito-idp." + cognitoRegion + ".amazonaws.com/" + userPoolId + "/.well-known/jwks.json";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtDecoder lambdaJwtDecoder() throws IOException, InvalidKeySpecException {
        RSAPublicKey publicKey = buildRSAPublicKey(publicKeyContent);
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private RSAPublicKey buildRSAPublicKey(String publicKeyContent) throws InvalidKeySpecException {
        // Remove the "BEGIN" and "END" markers
        publicKeyContent = publicKeyContent.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                                           .replaceAll("-----END PUBLIC KEY-----", "")
                                           .replaceAll("\\s+", ""); // Remove any whitespace characters
    
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
    
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidKeySpecException("Error generating RSA public key", e);
        }
    }
    
    // working code to create user from lambda trigger using symmetric key
    // @Bean
    // public JwtDecoder jwtDecoder() {
    //     return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256")).build();
    // }

}
