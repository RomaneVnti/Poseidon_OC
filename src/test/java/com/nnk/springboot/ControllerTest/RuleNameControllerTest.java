package com.nnk.springboot.ControllerTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;

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
 * Classe de test unitaire pour {@link RuleNameController}.
 *
 * <p>Cette classe teste les différentes actions du contrôleur RuleNameController,
 * en simulant les appels au service {@link RuleNameService}, au modèle Spring MVC {@link Model}
 * et à la validation via {@link BindingResult}.</p>
 *
 * <p>Les tests utilisent {@link MockMvc} en standalone pour simuler les requêtes HTTP
 * et vérifier les vues retournées ainsi que la présence des attributs nécessaires dans le modèle.</p>
 */
public class RuleNameControllerTest {

    /**
     * Simulateur de requêtes HTTP pour tester le contrôleur.
     */
    private MockMvc mockMvc;

    /**
     * Mock du service RuleNameService.
     */
    @Mock
    private RuleNameService ruleNameService;

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
    private RuleNameController ruleNameController;

    /**
     * Initialisation avant chaque test.
     *
     * <p>Ouvre les mocks et configure MockMvc avec le contrôleur à tester.</p>
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ruleNameController).build();
    }

    /**
     * Teste que la page de liste des RuleNames est accessible
     * et que la vue "ruleName/list" est retournée avec la liste des RuleNames.
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testHome_ShouldReturnListView_WithRuleNames() throws Exception {
        List<RuleName> ruleNames = Arrays.asList(new RuleName(), new RuleName());
        when(ruleNameService.getAllRuleName()).thenReturn(ruleNames);

        mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(model().attributeExists("ruleNames"));

        verify(ruleNameService, times(1)).getAllRuleName();
    }

    /**
     * Teste que la page d'ajout d'un RuleName est accessible
     * et retourne la vue "ruleName/add".
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testAddRuleForm_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/ruleName/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"));
    }

    /**
     * Teste la validation de l'ajout d'un RuleName avec erreurs de validation.
     *
     * <p>Vérifie que la méthode retourne la vue "ruleName/add" et que le service
     * d'ajout n'est pas appelé.</p>
     */
    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ruleNameController.validate(new RuleName(), bindingResult, model);

        assert(view.equals("ruleName/add"));
        verify(model, times(1)).addAttribute(eq("ruleName"), any(RuleName.class));
        verify(ruleNameService, never()).addRuleName(any(RuleName.class));
    }

    /**
     * Teste la validation de l'ajout d'un RuleName sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des RuleNames
     * et que le service d'ajout est appelé une fois.</p>
     */
    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ruleNameController.validate(new RuleName(), bindingResult, model);

        assert(view.equals("redirect:/ruleName/list"));
        verify(ruleNameService, times(1)).addRuleName(any(RuleName.class));
    }

    /**
     * Teste l'affichage du formulaire de mise à jour d'un RuleName existant.
     *
     * <p>Vérifie que la vue "ruleName/update" est retournée avec l'objet RuleName dans le modèle.</p>
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testShowUpdateForm_ShouldReturnUpdateView_WithRuleName() throws Exception {
        RuleName ruleName = new RuleName();
        ruleName.setId(1);
        when(ruleNameService.getRuleNameById(1)).thenReturn(ruleName);

        mockMvc.perform(get("/ruleName/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(model().attributeExists("ruleName"));

        verify(ruleNameService, times(1)).getRuleNameById(1);
    }

    /**
     * Teste la mise à jour d'un RuleName avec erreurs de validation.
     *
     * <p>Vérifie que la méthode retourne la vue "ruleName/update" et que
     * le service de mise à jour n'est pas appelé.</p>
     */
    @Test
    public void testUpdateRuleName_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ruleNameController.updateRuleName(1, new RuleName(), bindingResult, model);

        assert(view.equals("ruleName/update"));
        verify(model, times(1)).addAttribute(eq("ruleName"), any(RuleName.class));
        verify(ruleNameService, never()).updateRuleName(anyInt(), any(RuleName.class));
    }

    /**
     * Teste la mise à jour d'un RuleName sans erreur.
     *
     * <p>Vérifie que la méthode redirige vers la liste des RuleNames
     * et que le service de mise à jour est appelé une fois.</p>
     */
    @Test
    public void testUpdateRuleName_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ruleNameController.updateRuleName(1, new RuleName(), bindingResult, model);

        assert(view.equals("redirect:/ruleName/list"));
        verify(ruleNameService, times(1)).updateRuleName(eq(1), any(RuleName.class));
    }

    /**
     * Teste la suppression d'un RuleName.
     *
     * <p>Vérifie que la suppression redirige vers la liste des RuleNames
     * et que le service de suppression est appelé une fois.</p>
     *
     * @throws Exception en cas d'erreur lors de la requête simulée
     */
    @Test
    public void testDeleteRuleName_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/ruleName/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

        verify(ruleNameService, times(1)).deleteRuleName(1);
    }
}
