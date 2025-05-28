package com.nnk.springboot.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

/**
 * Handler exécuté après une authentification réussie.
 * <p>
 * Ce handler récupère les rôles de l'utilisateur authentifié
 * et redirige systématiquement vers la page "/home".
 * </p>
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Méthode appelée après une authentification réussie.
     *
     * @param request        la requête HTTP
     * @param response       la réponse HTTP
     * @param authentication l'objet contenant les informations d'authentification
     * @throws IOException      en cas d'erreur d'entrée/sortie
     * @throws ServletException en cas d'erreur de servlet
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        response.sendRedirect("/home");
    }
}
