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

public class RuleNameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RuleNameService ruleNameService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RuleNameController ruleNameController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ruleNameController).build();
    }

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

    @Test
    public void testAddRuleForm_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/ruleName/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"));
    }

    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ruleNameController.validate(new RuleName(), bindingResult, model);

        assert(view.equals("ruleName/add"));
        verify(model, times(1)).addAttribute(eq("ruleName"), any(RuleName.class));
        verify(ruleNameService, never()).addRuleName(any(RuleName.class));
    }

    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ruleNameController.validate(new RuleName(), bindingResult, model);

        assert(view.equals("redirect:/ruleName/list"));
        verify(ruleNameService, times(1)).addRuleName(any(RuleName.class));
    }

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

    @Test
    public void testUpdateRuleName_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ruleNameController.updateRuleName(1, new RuleName(), bindingResult, model);

        assert(view.equals("ruleName/update"));
        verify(model, times(1)).addAttribute(eq("ruleName"), any(RuleName.class));
        verify(ruleNameService, never()).updateRuleName(anyInt(), any(RuleName.class));
    }

    @Test
    public void testUpdateRuleName_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ruleNameController.updateRuleName(1, new RuleName(), bindingResult, model);

        assert(view.equals("redirect:/ruleName/list"));
        verify(ruleNameService, times(1)).updateRuleName(eq(1), any(RuleName.class));
    }

    @Test
    public void testDeleteRuleName_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/ruleName/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

        verify(ruleNameService, times(1)).deleteRuleName(1);
    }
}
