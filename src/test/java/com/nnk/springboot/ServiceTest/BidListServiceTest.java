package com.nnk.springboot.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;

import com.nnk.springboot.services.BidListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

/**
 * Tests unitaires pour la classe {@link BidListService}.
 * <p>
 * Cette classe utilise Mockito pour simuler le comportement du {@link BidListRepository}
 * et vérifie le fonctionnement des différentes méthodes du service :
 * récupération de toutes les offres, récupération par identifiant, ajout,
 * mise à jour et suppression d'une offre (BidList).
 * </p>
 * <p>
 * Chaque méthode de test valide le comportement attendu,
 * y compris la gestion des erreurs et des exceptions.
 * </p>
 */
@SpringBootTest
class BidListServiceTest {

    @InjectMocks
    private BidListService bidListService;

    @Mock
    private BidListRepository bidListRepository;

    private BidList bid;

    /**
     * Initialisation avant chaque test :
     * création d'un objet BidList d'exemple et ouverture des mocks Mockito.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bid = new BidList();
        bid.setBidListId(1);
        bid.setAccount("account");
        bid.setType("type");
        bid.setBidQuantity(BigDecimal.valueOf(10.0));
    }

    /**
     * Teste que la méthode {@link BidListService#getAllBid()} retourne une liste non nulle
     * contenant les éléments attendus.
     */
    @Test
    void getAllBid_ShouldReturnList() {
        List<BidList> bids = Arrays.asList(bid);
        when(bidListRepository.findAll()).thenReturn(bids);

        List<BidList> result = bidListService.getAllBid();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bidListRepository, times(1)).findAll();
    }

    /**
     * Teste que la méthode {@link BidListService#getAllBid()} retourne une liste vide
     * quand aucune donnée n'est présente.
     */
    @Test
    void getAllBid_ShouldReturnEmptyList() {
        when(bidListRepository.findAll()).thenReturn(Collections.emptyList());

        List<BidList> result = bidListService.getAllBid();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bidListRepository, times(1)).findAll();
    }

    /**
     * Teste que la méthode {@link BidListService#getAllBid()} lance une {@link RuntimeException}
     * en cas d'erreur de la couche repository.
     */
    @Test
    void getAllBid_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.getAllBid());
        assertEquals("Erreur lors de la récupération des BidList.", ex.getMessage());
        verify(bidListRepository, times(1)).findAll();
    }

    /**
     * Teste que la méthode {@link BidListService#getBidById(Integer)} retourne un objet BidList
     * correspondant à l'identifiant fourni.
     */
    @Test
    void getBidById_ShouldReturnBid() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));

        BidList result = bidListService.getBidById(1);

        assertNotNull(result);
        assertEquals(1, result.getBidListId());
        verify(bidListRepository, times(1)).findById(1);
    }

    /**
     * Teste que la méthode {@link BidListService#getBidById(Integer)} lance une
     * {@link IllegalArgumentException} si l'identifiant fourni est nul.
     */
    @Test
    void getBidById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bidListService.getBidById(null));
        assertEquals("L'ID ne peut pas être nul.", ex.getMessage());
    }

    /**
     * Teste que la méthode {@link BidListService#addBid(BidList)} sauvegarde correctement
     * un objet BidList et retourne l'objet sauvegardé.
     */
    @Test
    void addBid_ShouldSaveAndReturnBid() {
        when(bidListRepository.save(bid)).thenReturn(bid);

        BidList result = bidListService.addBid(bid);

        assertNotNull(result);
        assertEquals(bid.getBidListId(), result.getBidListId());
        verify(bidListRepository, times(1)).save(bid);
    }

    /**
     * Teste que la méthode {@link BidListService#addBid(BidList)} lance une
     * {@link IllegalArgumentException} si l'objet BidList fourni est nul.
     */
    @Test
    void addBid_ShouldThrowIllegalArgumentException_WhenBidIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bidListService.addBid(null));
        assertEquals("Le bid à ajouter ne peut pas être nul.", ex.getMessage());
    }

    /**
     * Teste que la méthode {@link BidListService#addBid(BidList)} lance une {@link RuntimeException}
     * en cas d'erreur de la couche repository lors de la sauvegarde.
     */
    @Test
    void addBid_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.save(bid)).thenThrow(new RuntimeException("DB save error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.addBid(bid));
        assertEquals("Erreur lors de l'ajout du BidList.", ex.getMessage());
        verify(bidListRepository, times(1)).save(bid);
    }

    /**
     * Teste que la méthode {@link BidListService#updateBidList(Integer, BidList)} met à jour
     * un BidList existant et retourne l'objet mis à jour.
     */
    @Test
    void updateBidList_ShouldUpdateAndReturnBid() {
        BidList update = new BidList();
        update.setAccount("newAccount");
        update.setType("newType");
        update.setBidQuantity(BigDecimal.valueOf(20.0));

        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));
        when(bidListRepository.save(any(BidList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BidList result = bidListService.updateBidList(1, update);

        assertNotNull(result);
        assertEquals("newAccount", result.getAccount());
        assertEquals("newType", result.getType());
        assertEquals(BigDecimal.valueOf(20.0), result.getBidQuantity());
        verify(bidListRepository, times(1)).findById(1);
        verify(bidListRepository, times(1)).save(bid);
    }

    /**
     * Teste que la méthode {@link BidListService#updateBidList(Integer, BidList)} lance une
     * {@link RuntimeException} en cas d'erreur de la couche repository lors de la mise à jour.
     */
    @Test
    void updateBidList_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));
        when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException("DB save error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bidListService.updateBidList(1, bid));
        assertEquals("Erreur lors de la mise à jour du Bid.", ex.getMessage());
    }

    /**
     * Teste que la méthode {@link BidListService#deleteBidList(Integer)} supprime un BidList existant
     * sans lancer d'exception.
     */
    @Test
    void deleteBidList_ShouldDeleteBid() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));

        doNothing().when(bidListRepository).delete(bid);

        assertDoesNotThrow(() -> bidListService.deleteBidList(1));

        verify(bidListRepository, times(1)).findById(1);
        verify(bidListRepository, times(1)).delete(bid);
    }

    /**
     * Teste que la méthode {@link BidListService#deleteBidList(Integer)} lance une
     * {@link RuntimeException} en cas d'erreur de la couche repository lors de la suppression.
     */
    @Test
    void deleteBidList_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));
        doThrow(new RuntimeException("DB delete error")).when(bidListRepository).delete(bid);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.deleteBidList(1));
        assertTrue(ex.getMessage().contains("Erreur lors de la suppression du Bid"));
    }
}
