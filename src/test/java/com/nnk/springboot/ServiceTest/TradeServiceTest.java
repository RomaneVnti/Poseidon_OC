package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private TradeRepository tradeRepository;

    private Trade trade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trade = new Trade();
        trade.setTradeId(1);
        trade.setAccount("TestAccount");
        trade.setType("Buy");
        trade.setBuyQuantity(100.0);
    }

    @Test
    void getAllTrade_shouldReturnTradeList() {
        List<Trade> trades = Arrays.asList(trade);
        when(tradeRepository.findAll()).thenReturn(trades);

        List<Trade> result = tradeService.getAllTrade();

        assertEquals(1, result.size());
        verify(tradeRepository).findAll();
    }

    @Test
    void getTradeById_shouldReturnTrade_whenFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.of(trade));

        Trade result = tradeService.getTradeById(1);

        assertNotNull(result);
        assertEquals("TestAccount", result.getAccount());
        verify(tradeRepository).findById(1);
    }

    @Test
    void getTradeById_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.getTradeById(1));
        verify(tradeRepository).findById(1);
    }

    @Test
    void addTrade_shouldReturnSavedTrade() {
        when(tradeRepository.save(trade)).thenReturn(trade);

        Trade result = tradeService.addTrade(trade);

        assertNotNull(result);
        assertEquals("TestAccount", result.getAccount());
        verify(tradeRepository).save(trade);
    }

    @Test
    void updateTrade_shouldReturnUpdatedTrade_whenFound() {
        Trade updated = new Trade();
        updated.setAccount("UpdatedAccount");
        updated.setType("Sell");
        updated.setBuyQuantity(200.0);

        when(tradeRepository.findById(1)).thenReturn(Optional.of(trade));
        when(tradeRepository.save(any())).thenReturn(trade);

        Trade result = tradeService.updateTrade(1, updated);

        assertNotNull(result);
        assertEquals("UpdatedAccount", trade.getAccount());
        assertEquals("Sell", trade.getType());
        assertEquals(200.0, trade.getBuyQuantity());
        verify(tradeRepository).findById(1);
        verify(tradeRepository).save(trade);
    }

    @Test
    void updateTrade_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.updateTrade(1, trade));
        verify(tradeRepository).findById(1);
        verify(tradeRepository, never()).save(any());
    }

    @Test
    void deleteTrade_shouldDeleteTrade_whenFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.of(trade));
        doNothing().when(tradeRepository).delete(trade);

        tradeService.deleteTrade(1);

        verify(tradeRepository).delete(trade);
    }

    @Test
    void deleteTrade_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.deleteTrade(1));
        verify(tradeRepository).findById(1);
        verify(tradeRepository, never()).delete(any());
    }
}
