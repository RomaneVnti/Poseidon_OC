package com.nnk.springboot.ControllerTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;

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
 * Classe de test unitaire pour {@link TradeController}.
 *
 * <p>Cette classe teste les différentes fonctionnalités du contrôleur TradeController
 * en simulant les appels au service {@link TradeService}, au modèle Spring MVC {@link Model}
 * ainsi qu'à la validation via {@link BindingResult}.</p>
 *
 * <p>Les tests utilisent {@link MockMvc} en standalone pour simuler des requêtes HTTP
 * et vérifier les vues retournées ainsi que la présence des attributs dans le modèle.</p>
 */
public class TradeControllerTest {

    /**
     * Simulateur de requêtes HTTP pour tester le contrôleur.
     */
    private MockMvc mockMvc;

    /**
     * Mock du service TradeService.
     */
    @Mock
    private TradeService tradeService;

    /**
     * Mock du modèle Spring MVC.
     */
    @Mock
    private Model model;

    /**
     * Mock de l'objet BindingResult pour la validation.
     */
    @Mock
    private BindingResult bindingResult;

    /**
     * Instance du contrôleur injectée avec les mocks.
     */
    @InjectMocks
    private TradeController tradeController;

    /**
     * Initialisation avant chaque test.
     *
     * <p>Ouvre les mocks et configure MockMvc avec le contrôleur à tester.</p>
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
    }

    /**
     * Teste que la page listant les Trades est accessible,
     * retourne la vue "trade/list" et contient la liste des trades dans le modèle.
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testHome_ShouldReturnListView_WithTrades() throws Exception {
        List<Trade> trades = Arrays.asList(new Trade(), new Trade());
        when(tradeService.getAllTrade()).thenReturn(trades);

        mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/list"))
                .andExpect(model().attributeExists("trades"));

        verify(tradeService, times(1)).getAllTrade();
    }

    /**
     * Teste l'accès à la page d'ajout d'un nouveau Trade,
     * qui doit retourner la vue "trade/add".
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testAddUser_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/trade/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"));
    }

    /**
     * Teste la validation d'un ajout de Trade avec erreurs de validation.
     *
     * <p>Vérifie que la vue "trade/add" est retournée,
     * que l'attribut "trade" est ajouté au modèle,
     * et que le service d'ajout n'est pas appelé.</p>
     */
    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = tradeController.validate(new Trade(), bindingResult, model);

        assert(view.equals("trade/add"));
        verify(model, times(1)).addAttribute(eq("trade"), any(Trade.class));
        verify(tradeService, never()).addTrade(any(Trade.class));
    }

    /**
     * Teste la validation d'un ajout de Trade sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des trades,
     * et que le service d'ajout est appelé une fois.</p>
     */
    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = tradeController.validate(new Trade(), bindingResult, model);

        assert(view.equals("redirect:/trade/list"));
        verify(tradeService, times(1)).addTrade(any(Trade.class));
    }

    /**
     * Teste l'affichage du formulaire de mise à jour d'un Trade existant.
     *
     * <p>Vérifie que la vue "trade/update" est retournée
     * et que l'objet Trade est présent dans le modèle.</p>
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testShowUpdateForm_ShouldReturnUpdateView_WithTrade() throws Exception {
        Trade trade = new Trade();
        trade.setTradeId(1);
        when(tradeService.getTradeById(1)).thenReturn(trade);

        mockMvc.perform(get("/trade/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(model().attributeExists("trade"));

        verify(tradeService, times(1)).getTradeById(1);
    }

    /**
     * Teste la mise à jour d'un Trade avec erreurs de validation.
     *
     * <p>Vérifie que la méthode retourne la vue "trade/update",
     * que l'attribut "trade" est ajouté au modèle,
     * et que le service de mise à jour n'est pas appelé.</p>
     */
    @Test
    public void testUpdateTrade_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = tradeController.updateTrade(1, new Trade(), bindingResult, model);

        assert(view.equals("trade/update"));
        verify(model, times(1)).addAttribute(eq("trade"), any(Trade.class));
        verify(tradeService, never()).updateTrade(anyInt(), any(Trade.class));
    }

    /**
     * Teste la mise à jour d'un Trade sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des trades,
     * et que le service de mise à jour est appelé une fois.</p>
     */
    @Test
    public void testUpdateTrade_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = tradeController.updateTrade(1, new Trade(), bindingResult, model);

        assert(view.equals("redirect:/trade/list"));
        verify(tradeService, times(1)).updateTrade(eq(1), any(Trade.class));
    }

    /**
     * Teste la suppression d'un Trade.
     *
     * <p>Vérifie que la suppression redirige vers la liste des trades
     * et que le service de suppression est appelé une fois.</p>
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testDeleteTrade_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/trade/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

        verify(tradeService, times(1)).deleteTrade(1);
    }
}
