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

/**
 * Tests unitaires pour la classe {@link TradeService}.
 * <p>
 * Cette classe teste les opérations CRUD fournies par le service {@code TradeService}
 * en simulant les interactions avec le {@link TradeRepository} via Mockito.
 * </p>
 * <ul>
 *   <li>Test de la récupération de toutes les transactions (trades).</li>
 *   <li>Test de la récupération d'une transaction par identifiant, avec succès et cas d'absence.</li>
 *   <li>Test de l'ajout d'une nouvelle transaction.</li>
 *   <li>Test de la mise à jour d'une transaction existante, avec gestion du cas où elle n'existe pas.</li>
 *   <li>Test de la suppression d'une transaction, avec gestion du cas où elle n'existe pas.</li>
 * </ul>
 */
class TradeServiceTest {

    @InjectMocks
    private TradeService tradeService;

    @Mock
    private TradeRepository tradeRepository;

    private Trade trade;

    /**
     * Initialise les mocks et crée un objet {@link Trade} exemple avant chaque test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        trade = new Trade();
        trade.setTradeId(1);
        trade.setAccount("TestAccount");
        trade.setType("Buy");
        trade.setBuyQuantity(100.0);
    }

    /**
     * Teste la récupération de toutes les transactions.
     * Vérifie que la liste retournée correspond à la liste simulée.
     */
    @Test
    void getAllTrade_shouldReturnTradeList() {
        List<Trade> trades = Arrays.asList(trade);
        when(tradeRepository.findAll()).thenReturn(trades);

        List<Trade> result = tradeService.getAllTrade();

        assertEquals(1, result.size());
        verify(tradeRepository).findAll();
    }

    /**
     * Teste la récupération d'une transaction par identifiant lorsque trouvée.
     * Vérifie que l'objet retourné n'est pas nul et correspond à la transaction attendue.
     */
    @Test
    void getTradeById_shouldReturnTrade_whenFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.of(trade));

        Trade result = tradeService.getTradeById(1);

        assertNotNull(result);
        assertEquals("TestAccount", result.getAccount());
        verify(tradeRepository).findById(1);
    }

    /**
     * Teste la récupération d'une transaction par identifiant lorsque non trouvée.
     * Vérifie qu'une {@link RuntimeException} est levée.
     */
    @Test
    void getTradeById_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.getTradeById(1));
        verify(tradeRepository).findById(1);
    }

    /**
     * Teste l'ajout d'une nouvelle transaction.
     * Vérifie que la transaction sauvegardée n'est pas nulle et correspond à l'entrée.
     */
    @Test
    void addTrade_shouldReturnSavedTrade() {
        when(tradeRepository.save(trade)).thenReturn(trade);

        Trade result = tradeService.addTrade(trade);

        assertNotNull(result);
        assertEquals("TestAccount", result.getAccount());
        verify(tradeRepository).save(trade);
    }

    /**
     * Teste la mise à jour d'une transaction existante.
     * Vérifie que les champs sont bien mis à jour et que la sauvegarde est effectuée.
     */
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

    /**
     * Teste la mise à jour d'une transaction inexistante.
     * Vérifie qu'une {@link RuntimeException} est levée et qu'aucune sauvegarde n'est réalisée.
     */
    @Test
    void updateTrade_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.updateTrade(1, trade));
        verify(tradeRepository).findById(1);
        verify(tradeRepository, never()).save(any());
    }

    /**
     * Teste la suppression d'une transaction existante.
     * Vérifie que la méthode delete est appelée avec l'objet correct.
     */
    @Test
    void deleteTrade_shouldDeleteTrade_whenFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.of(trade));
        doNothing().when(tradeRepository).delete(trade);

        tradeService.deleteTrade(1);

        verify(tradeRepository).delete(trade);
    }

    /**
     * Teste la suppression d'une transaction inexistante.
     * Vérifie qu'une {@link RuntimeException} est levée et qu'aucune suppression n'est effectuée.
     */
    @Test
    void deleteTrade_shouldThrowException_whenNotFound() {
        when(tradeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tradeService.deleteTrade(1));
        verify(tradeRepository).findById(1);
        verify(tradeRepository, never()).delete(any());
    }
}
