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

public class TradeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TradeService tradeService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
    }

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

    @Test
    public void testAddUser_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/trade/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"));
    }

    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = tradeController.validate(new Trade(), bindingResult, model);

        assert(view.equals("trade/add"));
        verify(model, times(1)).addAttribute(eq("trade"), any(Trade.class));
        verify(tradeService, never()).addTrade(any(Trade.class));
    }

    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = tradeController.validate(new Trade(), bindingResult, model);

        assert(view.equals("redirect:/trade/list"));
        verify(tradeService, times(1)).addTrade(any(Trade.class));
    }

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

    @Test
    public void testUpdateTrade_WithErrors_ShouldReturnUpdateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = tradeController.updateTrade(1, new Trade(), bindingResult, model);

        assert(view.equals("trade/update"));
        verify(model, times(1)).addAttribute(eq("trade"), any(Trade.class));
        verify(tradeService, never()).updateTrade(anyInt(), any(Trade.class));
    }

    @Test
    public void testUpdateTrade_WithoutErrors_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = tradeController.updateTrade(1, new Trade(), bindingResult, model);

        assert(view.equals("redirect:/trade/list"));
        verify(tradeService, times(1)).updateTrade(eq(1), any(Trade.class));
    }

    @Test
    public void testDeleteTrade_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/trade/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

        verify(tradeService, times(1)).deleteTrade(1);
    }
}
