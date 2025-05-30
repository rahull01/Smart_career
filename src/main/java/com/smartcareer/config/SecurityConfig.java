package com.smartcareer.config;

import com.smartcareer.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

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
                                "/api/auth/**",
                                "/quiz.html",
                                "/api/auth/register",
                                "/api/chatgpt/**",  // <-- yahan /api/chatgpt/** poore chatgpt ke liye allow karo
                                "/favicon.ico",
                                "/software-development.html",
                                "/Mechanical_Engineering.html",
                                "/Digital_Marketing.html",
                                "/Data_Science.html",
                                "/solve.html",
                                "/UI_UX_Design.html",
                                "/Healthcare.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
