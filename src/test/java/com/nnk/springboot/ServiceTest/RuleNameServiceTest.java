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

class RuleNameServiceTest {

    @InjectMocks
    private RuleNameService ruleNameService;

    @Mock
    private RuleNameRepository ruleNameRepository;

    private RuleName ruleName;

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

    @Test
    void getAllRuleName_shouldReturnListOfRuleNames() {
        List<RuleName> ruleNames = Arrays.asList(ruleName);
        when(ruleNameRepository.findAll()).thenReturn(ruleNames);

        List<RuleName> result = ruleNameService.getAllRuleName();

        assertEquals(1, result.size());
        verify(ruleNameRepository, times(1)).findAll();
    }

    @Test
    void getRuleNameById_shouldReturnRuleName_whenFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(ruleName));

        RuleName result = ruleNameService.getRuleNameById(1);

        assertNotNull(result);
        assertEquals("Rule 1", result.getName());
        verify(ruleNameRepository).findById(1);
    }

    @Test
    void getRuleNameById_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.getRuleNameById(1));
        verify(ruleNameRepository).findById(1);
    }

    @Test
    void addRuleName_shouldSaveAndReturnRuleName() {
        when(ruleNameRepository.save(ruleName)).thenReturn(ruleName);

        RuleName result = ruleNameService.addRuleName(ruleName);

        assertNotNull(result);
        assertEquals("Rule 1", result.getName());
        verify(ruleNameRepository).save(ruleName);
    }

    @Test
    void deleteRuleName_shouldDeleteRuleName_whenFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(ruleName));
        doNothing().when(ruleNameRepository).delete(ruleName);

        ruleNameService.deleteRuleName(1);

        verify(ruleNameRepository).delete(ruleName);
    }

    @Test
    void deleteRuleName_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.deleteRuleName(1));
        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository, never()).delete(any());
    }

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

    @Test
    void updateRuleName_shouldThrowException_whenNotFound() {
        when(ruleNameRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ruleNameService.updateRuleName(1, ruleName));
        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository, never()).save(any());
    }
}
