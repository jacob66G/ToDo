package com.example.ToDo.security.config;

import com.example.ToDo.security.exception.CustomAuthenticationEntryPoint;
import com.example.ToDo.security.filter.JwtAuthenticationFilter;
import com.example.ToDo.security.provider.EmailPasswordAuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement( sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // making only h2 console public
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                // disabling X-Fram-Options to get access to h2-console
                .headers( headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling( ehc -> ehc.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)

                .cors( corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration corsConfig = new CorsConfiguration();
                        corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        corsConfig.setAllowedMethods(Collections.singletonList("*"));
                        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfig.setExposedHeaders(Collections.singletonList("Location"));
                        corsConfig.setMaxAge(300L);

                        return corsConfig;
                    }
                }))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        AuthenticationProvider authenticationProvider = new EmailPasswordAuthenticationProvider(
                                                                                        userDetailsService,
                                                                                        passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);
        return providerManager;
    }
}
