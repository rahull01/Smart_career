package com.smartcareer.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.smartcareer.security.JwtAuthenticationFilter;

@Configuration
@Component
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // ✅ ✅ ADD THIS LINE FOR CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/register.html",
                                "/dashboard.html",
                                "/Talk_with_us.html",
                                "/dsa-practice.html",
                                "/career-paths.html",
                                "/guidance.html",
                                "/style.css",
                                "/css/**",
                                "/js/**",
                                "/api/chat",
                                "/api/auth/**",
                                "/quiz.html",
                                "/api/auth/register",
                                "/api/chatgpt/**",
                                "/api/chatgpt/generate-questions",
                                "/favicon.ico",
                                "/software-development.html",
                                "/Mechanical_Engineering.html",
                                "/Digital_Marketing.html",
                                "/Data_Science.html",
                                "/solve.html",
                                "/UI_UX_Design.html",
                                "/Healthcare.html",
                                "/error",
                                "api/quiz/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var cors = new org.springframework.web.cors.CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:8080", "*"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
