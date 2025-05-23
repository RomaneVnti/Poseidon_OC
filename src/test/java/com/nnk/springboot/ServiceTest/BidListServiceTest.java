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

@SpringBootTest
class BidListServiceTest {

    @InjectMocks
    private BidListService bidListService;

    @Mock
    private BidListRepository bidListRepository;

    private BidList bid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bid = new BidList();
        bid.setBidListId(1);
        bid.setAccount("account");
        bid.setType("type");
        bid.setBidQuantity(BigDecimal.valueOf(10.0));
    }

    @Test
    void getAllBid_ShouldReturnList() {
        List<BidList> bids = Arrays.asList(bid);
        when(bidListRepository.findAll()).thenReturn(bids);

        List<BidList> result = bidListService.getAllBid();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bidListRepository, times(1)).findAll();
    }

    @Test
    void getAllBid_ShouldReturnEmptyList() {
        when(bidListRepository.findAll()).thenReturn(Collections.emptyList());

        List<BidList> result = bidListService.getAllBid();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bidListRepository, times(1)).findAll();
    }

    @Test
    void getAllBid_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.getAllBid());
        assertEquals("Erreur lors de la récupération des BidList.", ex.getMessage());
        verify(bidListRepository, times(1)).findAll();
    }

    @Test
    void getBidById_ShouldReturnBid() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));

        BidList result = bidListService.getBidById(1);

        assertNotNull(result);
        assertEquals(1, result.getBidListId());
        verify(bidListRepository, times(1)).findById(1);
    }

    @Test
    void getBidById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bidListService.getBidById(null));
        assertEquals("L'ID ne peut pas être nul.", ex.getMessage());
    }



    @Test
    void addBid_ShouldSaveAndReturnBid() {
        when(bidListRepository.save(bid)).thenReturn(bid);

        BidList result = bidListService.addBid(bid);

        assertNotNull(result);
        assertEquals(bid.getBidListId(), result.getBidListId());
        verify(bidListRepository, times(1)).save(bid);
    }

    @Test
    void addBid_ShouldThrowIllegalArgumentException_WhenBidIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bidListService.addBid(null));
        assertEquals("Le bid à ajouter ne peut pas être nul.", ex.getMessage());
    }

    @Test
    void addBid_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.save(bid)).thenThrow(new RuntimeException("DB save error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.addBid(bid));
        assertEquals("Erreur lors de l'ajout du BidList.", ex.getMessage());
        verify(bidListRepository, times(1)).save(bid);
    }

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

    @Test
    void updateBidList_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));
        when(bidListRepository.save(any(BidList.class))).thenThrow(new RuntimeException("DB save error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bidListService.updateBidList(1, bid));
        assertEquals("Erreur lors de la mise à jour du Bid.", ex.getMessage());
    }

    @Test
    void deleteBidList_ShouldDeleteBid() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));

        doNothing().when(bidListRepository).delete(bid);

        assertDoesNotThrow(() -> bidListService.deleteBidList(1));

        verify(bidListRepository, times(1)).findById(1);
        verify(bidListRepository, times(1)).delete(bid);
    }

    @Test
    void deleteBidList_ShouldThrowRuntimeException_OnRepositoryError() {
        when(bidListRepository.findById(1)).thenReturn(Optional.of(bid));
        doThrow(new RuntimeException("DB delete error")).when(bidListRepository).delete(bid);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bidListService.deleteBidList(1));
        assertTrue(ex.getMessage().contains("Erreur lors de la suppression du Bid"));
    }
}
