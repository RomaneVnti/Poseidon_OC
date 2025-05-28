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

/**
 * Classe de configuration de la sécurité Spring Security.
 * Elle définit les règles d'accès, la gestion de l'authentification,
 * le chiffrement des mots de passe, et la redirection des utilisateurs.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailService customUserDetailService;

    /**
     * Définit la chaîne de filtres de sécurité HTTP.
     *
     * @param http l'objet HttpSecurity à configurer
     * @return la chaîne de filtres de sécurité
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuration des règles de sécurité");

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/app/login",
                                "/user/add",
                                "/user/validate",   // <-- Ajouté ici
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers("/user/list").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/app/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
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

    /**
     * Fournit un encodeur de mot de passe basé sur BCrypt.
     *
     * @return une instance de BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        logger.debug("Initialisation du BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le gestionnaire d'authentification avec le service de détails utilisateur personnalisé.
     *
     * @param http l'objet HttpSecurity
     * @param bCryptPasswordEncoder l'encodeur de mot de passe à utiliser
     * @return l'AuthenticationManager configuré
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        logger.info("Configuration du gestionnaire d'authentification");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailService)
                .passwordEncoder(bCryptPasswordEncoder);

        logger.info("Gestionnaire d'authentification configuré avec succès");

        return authenticationManagerBuilder.build();
    }
}
