package com.nnk.springboot.ControllerTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/**
 * Classe de test unitaire pour {@link RatingController}.
 *
 * <p>Cette classe teste les différentes méthodes du contrôleur RatingController
 * en utilisant Mockito pour simuler le service {@link RatingService}, le modèle {@link Model}
 * et la gestion des erreurs via {@link BindingResult}.</p>
 *
 * <p>Les tests utilisent {@link MockMvc} configuré en standalone pour simuler
 * les requêtes HTTP et vérifier les vues retournées, les attributs du modèle,
 * ainsi que les appels aux services mockés.</p>
 */
public class RatingControllerTest {

    /**
     * Simulateur de requêtes HTTP pour tester le contrôleur.
     */
    private MockMvc mockMvc;

    /**
     * Mock du service RatingService.
     */
    @Mock
    private RatingService ratingService;

    /**
     * Mock du modèle Spring MVC.
     */
    @Mock
    private Model model;

    /**
     * Mock de l'objet de gestion des erreurs de validation.
     */
    @Mock
    private BindingResult bindingResult;

    /**
     * Instance injectée du contrôleur à tester.
     */
    @InjectMocks
    private RatingController ratingController;

    /**
     * Initialisation avant chaque test.
     *
     * <p>Ouvre les mocks et configure MockMvc pour le contrôleur.</p>
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
    }

    /**
     * Teste que la page liste des ratings est accessible
     * et que la vue "rating/list" est retournée avec la liste des ratings dans le modèle.
     *
     * @throws Exception en cas d'erreur lors de la simulation de la requête
     */
    @Test
    public void testHome_ShouldReturnRatingListView_WithRatings() throws Exception {
        List<Rating> ratings = Arrays.asList(new Rating(), new Rating());
        when(ratingService.getAllRating()).thenReturn(ratings);

        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("ratings"));

        verify(ratingService, times(1)).getAllRating();
    }

    /**
     * Teste que la page d'ajout d'un rating est accessible
     * et retourne la vue "rating/add".
     *
     * @throws Exception en cas d'erreur lors de la simulation de la requête
     */
    @Test
    public void testAddRatingForm_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/rating/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"));
    }

    /**
     * Teste la validation d'un nouveau rating avec erreurs de validation.
     *
     * <p>Vérifie que la méthode retourne la vue "rating/add" et que
     * le service d'ajout n'est pas appelé.</p>
     */
    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ratingController.validate(new Rating(), bindingResult, model);

        assert(view.equals("rating/add"));
        verify(model, times(1)).addAttribute(eq("rating"), any(Rating.class));
        verify(ratingService, never()).addRating(any(Rating.class));
    }

    /**
     * Teste la validation d'un nouveau rating sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des ratings
     * et que le service d'ajout est appelé une fois.</p>
     */
    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ratingController.validate(new Rating(), bindingResult, model);

        assert(view.equals("redirect:/rating/list"));
        verify(ratingService, times(1)).addRating(any(Rating.class));
    }

    /**
     * Teste l'affichage du formulaire de mise à jour d'un rating.
     *
     * <p>Vérifie que la vue "rating/update" est retournée avec le rating dans le modèle.</p>
     *
     * @throws Exception en cas d'erreur lors de la simulation de la requête
     */
    @Test
    public void testShowUpdateForm_ShouldReturnUpdateView_WithRating() throws Exception {
        Rating rating = new Rating();
        rating.setId(1);
        when(ratingService.getRatingById(1)).thenReturn(rating);

        mockMvc.perform(get("/rating/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("rating"));

        verify(ratingService, times(1)).getRatingById(1);
    }

    /**
     * Teste la mise à jour d'un rating avec erreurs de validation.
     *
     * <p>Vérifie que la méthode retourne la vue "rating/update" et que
     * le service de mise à jour n'est pas appelé.</p>
     */
    @Test
    public void testUpdateRating_WithErrors_ShouldReturnUpdateView() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ratingController.updateRating(1, new Rating(), bindingResult, model);

        assert(view.equals("rating/update"));
        verify(model, times(1)).addAttribute(eq("rating"), any(Rating.class));
        verify(ratingService, never()).updateRating(anyInt(), any(Rating.class));
    }

    /**
     * Teste la mise à jour d'un rating sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des ratings
     * et que le service de mise à jour est appelé une fois.</p>
     */
    @Test
    public void testUpdateRating_WithoutErrors_ShouldRedirect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ratingController.updateRating(1, new Rating(), bindingResult, model);

        assert(view.equals("redirect:/rating/list"));
        verify(ratingService, times(1)).updateRating(eq(1), any(Rating.class));
    }

    /**
     * Teste la suppression d'un rating.
     *
     * <p>Vérifie que la suppression redirige vers la liste des ratings
     * et que le service de suppression est appelé une fois.</p>
     *
     * @throws Exception en cas d'erreur lors de la simulation de la requête
     */
    @Test
    public void testDeleteRating_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/rating/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

        verify(ratingService, times(1)).deleteRating(1);
    }
}
