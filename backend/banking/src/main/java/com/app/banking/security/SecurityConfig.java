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
import org.springframework.security.config.Customizer;


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
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Allow CORS preflight requests for all paths
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public API endpoints (authentication, registration, password reset etc.)
                .requestMatchers("/api/auth/**").permitAll()

                // Permit access to specific API paths for specific roles
                .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Allow access to specific static resources often found in the root or common paths
                // Add any other specific files your React app might request from the root here
                .requestMatchers("/favicon.ico", "/logo192.png", "/logo512.png", "/manifest.json").permitAll()

                // Allow access to standard static resource directories
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()

                // --- CRUCIAL CHANGE FOR SPA FALLBACK ---
                // Allow the root path and any other GET request that isn't an API endpoint.
                // This permits client-side routes (e.g., /dashboard) to reach the WebConfig's
                // resource handler, which will then serve index.html.
                // Allow SPA entry points (frontend only)
                .requestMatchers(HttpMethod.GET, "/", "/index.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/static/**", "/css/**", "/js/**", "/images/**").permitAll()


                // ALL OTHER REQUESTS MUST BE AUTHENTICATED
                // This now correctly applies to POST, PUT, DELETE, etc. requests
                // that are not explicitly permitted above, and authenticated GET API calls.
                .anyRequest().authenticated()
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
