package com.nnk.springboot.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

/**
 * Service personnalisé pour charger les détails d'un utilisateur à partir de la base de données.
 * Utilisé par Spring Security pour l'authentification.
 */
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailService.class);
    private final UserRepository userRepository;

    /**
     * Constructeur injectant le {@link UserRepository}.
     *
     * @param userRepository le repository des utilisateurs
     */
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge l'utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return les détails de l'utilisateur pour Spring Security
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Tentative de chargement de l'utilisateur : {}", username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("Utilisateur non trouvé : {}", username);
            throw new UsernameNotFoundException("Utilisateur non trouvé : " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getGrantedAuthorities(user.getRole())
        );
    }

    /**
     * Retourne la liste des autorités (rôles) associées à l'utilisateur.
     *
     * @param role le rôle de l'utilisateur
     * @return la liste des autorités pour Spring Security
     */
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        logger.info("Assignation du rôle : {}", role);
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        } catch (Exception e) {
            logger.error("Erreur lors de l'attribution du rôle : {}", role, e);
            throw new RuntimeException("Erreur lors de l'attribution des rôles", e);
        }
        return authorities;
    }
}
