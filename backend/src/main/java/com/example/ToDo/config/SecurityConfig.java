package com.example.ToDo.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // making only h2 console public
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .anyRequest().authenticated()
                )
                // disabling X-Fram-Options to get access to h2-console
                .headers( headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }
}
