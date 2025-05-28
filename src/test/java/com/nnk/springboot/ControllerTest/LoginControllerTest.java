package com.nnk.springboot.ControllerTest;

import com.nnk.springboot.controllers.LoginController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de test pour le {@link LoginController}.
 *
 * <p>Ces tests vérifient le comportement des endpoints liés à la connexion,
 * à la récupération de la liste des utilisateurs et à la gestion des erreurs.</p>
 *
 * <p>Le test utilise {@link WebMvcTest} pour charger uniquement le contrôleur concerné,
 * et {@link AutoConfigureMockMvc} pour configurer {@link MockMvc} sans les filtres de sécurité
 * afin de faciliter les tests sans authentification réelle.</p>
 */
@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoginControllerTest {

    /**
     * MockMvc pour simuler les requêtes HTTP vers le contrôleur.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Mock du repository utilisateur, utilisé pour simuler l'accès aux données.
     */
    @MockBean
    private UserRepository userRepository;

    /**
     * Teste que la page de login est accessible et retourne la vue "login".
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void loginPage_shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/app/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    /**
     * Teste la récupération de la liste des utilisateurs.
     * <p>
     * Simule l'appel à la méthode {@code findAll} du repository utilisateur,
     * puis vérifie que la vue "user/list" est retournée avec le modèle contenant la liste des utilisateurs.
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void getAllUserArticles_shouldReturnUserListViewWithUsers() throws Exception {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("admin");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/app/secure/article-details"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("users"));

        verify(userRepository, times(1)).findAll();
    }

    /**
     * Teste l'accès à la page d'erreur "/app/error".
     * <p>
     * Vérifie que la vue "403" est retournée avec un message d'erreur approprié.
     * </p>
     *
     * @throws Exception si une erreur survient lors de la simulation de la requête
     */
    @Test
    public void error_shouldReturn403ViewWithErrorMessage() throws Exception {
        mockMvc.perform(get("/app/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("403"))
                .andExpect(model().attribute("errorMsg", "You are not authorized for the requested data."));
    }
}
