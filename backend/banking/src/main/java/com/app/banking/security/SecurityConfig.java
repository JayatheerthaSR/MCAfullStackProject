package com.app.banking.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.banking.security.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Allow CORS preflight requests for all paths
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public API endpoints (authentication, registration, password reset etc.)
                .requestMatchers("/api/auth/**").permitAll()

                // Permit access to specific API paths for specific roles
                .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Allow access to static resources for your frontend (React build)
                // Ensure these paths match where your static files are served from.
                // Examples: /static/, /css/, /js/, /images/, /favicon.ico, /index.html (if served directly)
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/").permitAll()

                // For all other requests that are NOT APIs or static resources,
                // rely on the WebConfig to forward them to index.html.
                // These paths are considered client-side routes and Spring Security
                // should not try to authenticate them directly.
                // This covers paths like /customerDashboard, /adminDashboard etc.
                // The `WebConfig` will forward them to `/` where the React app loads.
                .requestMatchers("/**").permitAll() // THIS LINE IS NOW THE FALLBACK FOR UI ROUTES

                // Any request that reaches here and hasn't been permitted must be authenticated.
                // THIS RULE IS NO LONGER NEEDED IF "/**".permitAll() IS USED FOR UI FALLBACK,
                // AS ALL API PATHS ARE NOW EXPLICITLY DEFINED BEFORE IT.
                // .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                .invalidateHttpSession(false)
                .deleteCookies("JSESSIONID")
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
