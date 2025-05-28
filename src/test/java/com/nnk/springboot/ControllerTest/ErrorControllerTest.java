package com.nnk.springboot.ControllerTest;

import com.nnk.springboot.controllers.ErrorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de test pour le {@link ErrorController}.
 *
 * <p>Ces tests vérifient le comportement de la page d'erreur 403 (accès refusé),
 * en s'assurant que le modèle et la vue sont correctement renseignés en fonction
 * de la présence ou non d'un utilisateur authentifié.</p>
 *
 * <p>Le test utilise {@link WebMvcTest} pour charger uniquement le contrôleur concerné
 * et {@link AutoConfigureMockMvc} pour configurer {@link MockMvc} sans filtre de sécurité
 * afin de faciliter les tests des routes sans authentification réelle.</p>
 */
@WebMvcTest(ErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ErrorControllerTest {

    /**
     * MockMvc pour simuler les requêtes HTTP vers le contrôleur.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Teste l'accès à la page 403 avec un utilisateur authentifié.
     * <p>
     * Vérifie que la vue "403" est retournée avec :
     * <ul>
     *   <li>le statut HTTP 200 (OK)</li>
     *   <li>le modèle contenant le nom d'utilisateur</li>
     *   <li>le message d'erreur spécifique indiquant un accès refusé</li>
     * </ul>
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void testAccessDenied_withAuthenticatedUser_shouldAddUsernameAndErrorMessage() throws Exception {
        Principal mockPrincipal = () -> "admin";

        mockMvc.perform(get("/403").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("403"))
                .andExpect(model().attribute("username", "admin"))
                .andExpect(model().attribute("errorMsg", "Vous n'êtes pas autorisé à accéder à cette page."));
    }

    /**
     * Teste l'accès à la page 403 sans utilisateur authentifié.
     * <p>
     * Vérifie que la vue "403" est retournée avec :
     * <ul>
     *   <li>le statut HTTP 200 (OK)</li>
     *   <li>le modèle contenant "Invité" comme nom d'utilisateur</li>
     *   <li>le message d'erreur spécifique indiquant un accès refusé</li>
     * </ul>
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void testAccessDenied_withoutAuthenticatedUser_shouldSetUsernameAsInviteAndAddErrorMessage() throws Exception {
        mockMvc.perform(get("/403"))
                .andExpect(status().isOk())
                .andExpect(view().name("403"))
                .andExpect(model().attribute("username", "Invité"))
                .andExpect(model().attribute("errorMsg", "Vous n'êtes pas autorisé à accéder à cette page."));
    }
}
