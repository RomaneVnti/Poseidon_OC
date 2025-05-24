package com.nnk.springboot.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nnk.springboot.services.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuration des règles de sécurité");

        http
                .csrf(csrf -> csrf.disable())  // désactivation CSRF si pas besoin (adapter selon besoin)
                .authorizeHttpRequests(auth -> auth
                        // Pages accessibles sans authentification
                        .requestMatchers("/app/login", "/css/**", "/js/**", "/images/**").permitAll()
                        // Seul ADMIN peut accéder à /user/list
                        .requestMatchers("/user/list").hasRole("ADMIN")
                        // Toutes les autres requêtes doivent être authentifiées
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/app/login")           // URL du formulaire de login
                        .loginProcessingUrl("/login")      // URL POST pour soumettre login/password
                        .defaultSuccessUrl("/home", true)  // TOUS les utilisateurs redirigés vers /home après login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/app/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        logger.info("Configuration de la sécurité appliquée avec succès");

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        logger.debug("Initialisation du BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        logger.info("Configuration du gestionnaire d'authentification");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailService).passwordEncoder(bCryptPasswordEncoder);

        logger.info("Gestionnaire d'authentification configuré avec succès");

        return authenticationManagerBuilder.build();
    }
}
