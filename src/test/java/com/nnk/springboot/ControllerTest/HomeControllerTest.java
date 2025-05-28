package com.nnk.springboot.ControllerTest;

import com.nnk.springboot.controllers.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de test pour le {@link HomeController}.
 *
 * <p>Ces tests vérifient le comportement des différentes routes du contrôleur
 * home, notamment l'affichage de la page d'accueil avec ou sans utilisateur
 * authentifié, ainsi que la redirection de la page d'accueil admin.</p>
 *
 * <p>Le test utilise {@link WebMvcTest} pour charger uniquement le contrôleur concerné,
 * et {@link AutoConfigureMockMvc} pour configurer {@link MockMvc} sans les filtres de sécurité
 * afin de faciliter les tests des routes sans authentification réelle.</p>
 */
@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HomeControllerTest {

    /**
     * MockMvc pour simuler les requêtes HTTP vers le contrôleur.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Teste l'accès à la page "/home" avec un utilisateur authentifié.
     * <p>
     * Vérifie que la vue "home" est retournée avec :
     * <ul>
     *   <li>le statut HTTP 200 (OK)</li>
     *   <li>le modèle contenant le nom d'utilisateur authentifié</li>
     * </ul>
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void testHome_withAuthenticatedUser_shouldAddUsernameToModelAndReturnHomeView() throws Exception {
        Principal mockPrincipal = () -> "john.doe";

        mockMvc.perform(get("/home").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "john.doe"));
    }

    /**
     * Teste l'accès à la page "/home" sans utilisateur authentifié.
     * <p>
     * Vérifie que la vue "home" est retournée avec :
     * <ul>
     *   <li>le statut HTTP 200 (OK)</li>
     *   <li>le modèle contenant "Guest" comme nom d'utilisateur</li>
     * </ul>
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void testHome_withoutAuthenticatedUser_shouldAddGuestUsernameAndReturnHomeView() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "Guest"));
    }

    /**
     * Teste la redirection de la page "/admin/home".
     * <p>
     * Vérifie que la requête aboutit à une redirection (statut 3xx)
     * vers la liste des bids à l'URL "/bidList/list".
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void testAdminHome_shouldRedirectToBidListList() throws Exception {
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));
    }
}
