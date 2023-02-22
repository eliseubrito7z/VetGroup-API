package com.api.vetgroup.config;

import com.api.vetgroup.exceptions.handler.FilterChainExceptionHandler;
import com.api.vetgroup.security.jwt.JwtConfigurer;
import com.api.vetgroup.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private FilterChainExceptionHandler filterChainExceptionHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();

        Pbkdf2PasswordEncoder pbkdf2Encoder =
                new Pbkdf2PasswordEncoder("", 8, 185000,
                        Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        return passwordEncoder;
    }

    @Bean
    AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/auth/signin",
                                        "/auth/refresh",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                .requestMatchers(HttpMethod.GET,
                                        "/api/staff/v1/**"
                                ).permitAll()

                                .requestMatchers(HttpMethod.PUT, "/api/staff/v1/**"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER")

                                .requestMatchers(HttpMethod.POST,
                                        "/api/staff/v1/**",
                                                "/api/rooms/v1/**"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER")

                                .requestMatchers("/api/rooms/v1/create"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER", "MANAGER")

                                .requestMatchers("/api/reports/v1/create"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER", "MANAGER", "VETERINARY", "ASSISTANT")

                                .requestMatchers(HttpMethod.PATCH, "/api/reports/v1/**"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER")

                                .requestMatchers(HttpMethod.PATCH, "/api/rooms/v1/**"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER", "MANAGER", "VETERINARY")

                                .requestMatchers(HttpMethod.DELETE, "/api/**"
                                ).hasAnyAuthority("CEO", "GENERAL_MANAGER")

                                .requestMatchers("/api/**").authenticated()
                )
                .cors()
                .and()
                .apply(new JwtConfigurer(tokenProvider))
                .and()
                .build();
    }
}
