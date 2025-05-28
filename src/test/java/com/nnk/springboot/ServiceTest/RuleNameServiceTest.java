package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.RuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe {@link RuleNameService}.
 * <p>
 * Cette classe teste les opérations CRUD proposées par le service {@code RuleNameService}
 * en simulant les interactions avec le {@link RuleNameRepository} à l'aide de Mockito.
 * </p>
 * <ul>
 *   <li>Test de la récupération de toutes les règles.</li>
 *   <li>Test de la récupération d'une règle par identifiant, avec succès et en cas d'absence.</li>
 *   <li>Test de l'ajout d'une nouvelle règle.</li>
 *   <li>Test de la suppression d'une règle, avec gestion du cas où la règle n'existe pas.</li>
 *   <li>Test de la mise à jour d'une règle existante, avec gestion du cas où la règle n'existe pas.</li>
 * </ul>
 */
class RuleNameServiceTest {

    @InjectMocks
    private RuleNameService ruleNameService;

    @Mock
    private RuleNameRepository ruleNameRepository;

    private RuleName ruleName;

    /**
     * Initialise les mocks et crée un objet {@link RuleName} exemple avant chaque test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleName = new RuleName();
        ruleName.setId(1);
        ruleName.setName("Rule 1");
        ruleName.setDescription("Test description");
        ruleName.setJson("{\"field\":\"value\"}");
        ruleName.setSqlStr("SELECT * FROM test");
        ruleName.setSqlPart("WHERE condition");
        ruleName.setTemplate("Template");
    }

    /**
     * Teste la récupération de toutes les règles.
     * Vérifie que la liste retournée correspond à la liste simulée.
     */
    @Test
    void getAllRuleName_shouldReturnListOfRuleNames() {
        List<RuleName> ruleNames = Arrays.asList(ruleName);
        when(ruleNameRepository.findAll()).thenReturn(ruleNames);

        List<RuleName> result = ruleNameService.getAllRuleName();

        assertEquals(1, result.size());
        verify(ruleNameRepository, times(1)).findAll();
    }

    /**
     * Teste la récupération d'une règle par identifiant quand elle existe.
     * Vérifie que la règle retournée est non nulle et correcte.
     */
    @Test
    void getRuleNameById_shouldReturnRuleName_whenFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(ruleName));

        RuleName result = ruleNameService.getRuleNameById(1);

        assertNotNull(result);
        assertEquals("Rule 1", result.getName());
        verify(ruleNameRepository).findById(1);
    }

    /**
     * Teste le cas où la récupération d'une règle par identifiant ne trouve rien.
     * Vérifie qu'une {@link RuntimeException} est levée.
     */
    @Test
    void getRuleNameById_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.getRuleNameById(1));
        verify(ruleNameRepository).findById(1);
    }

    /**
     * Teste l'ajout d'une règle.
     * Vérifie que la règle ajoutée est non nulle et correctement sauvegardée.
     */
    @Test
    void addRuleName_shouldSaveAndReturnRuleName() {
        when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);

        RuleName result = ruleNameService.addRuleName(ruleName);

        assertNotNull(result);
        assertEquals("Rule 1", result.getName());
        verify(ruleNameRepository).save(ruleName);
    }

    /**
     * Teste la suppression d'une règle existante.
     * Vérifie que la méthode delete est bien appelée avec la règle correcte.
     */
    @Test
    void deleteRuleName_shouldDeleteRuleName_whenFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(ruleName));
        doNothing().when(ruleNameRepository).delete(ruleName);

        ruleNameService.deleteRuleName(1);

        verify(ruleNameRepository).delete(ruleName);
    }

    /**
     * Teste la suppression d'une règle qui n'existe pas.
     * Vérifie qu'une {@link RuntimeException} est levée et que la suppression n'est jamais appelée.
     */
    @Test
    void deleteRuleName_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.deleteRuleName(1));
        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository, never()).delete(any());
    }

    /**
     * Teste la mise à jour d'une règle existante.
     * Vérifie que les propriétés sont mises à jour et que la sauvegarde est effectuée.
     */
    @Test
    void updateRuleName_shouldUpdateAndReturnRuleName_whenFound() {
        RuleName updated = new RuleName();
        updated.setName("Updated Rule");
        updated.setDescription("Updated description");
        updated.setJson("{}");
        updated.setSqlStr("SQL");
        updated.setSqlPart("PART");
        updated.setTemplate("TEMPLATE");

        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(ruleName));
        when(ruleNameRepository.save(any())).thenReturn(ruleName);

        RuleName result = ruleNameService.updateRuleName(1, updated);

        assertNotNull(result);
        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository).save(ruleName);
        assertEquals("Updated Rule", ruleName.getName());
    }

    /**
     * Teste la mise à jour d'une règle non existante.
     * Vérifie qu'une {@link RuntimeException} est levée et qu'aucune sauvegarde n'est effectuée.
     */
    @Test
    void updateRuleName_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.updateRuleName(1, ruleName));
        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository, never()).save(any());
    }
}
