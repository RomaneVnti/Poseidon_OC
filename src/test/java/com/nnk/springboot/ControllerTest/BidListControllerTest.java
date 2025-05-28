package com.nnk.springboot.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test d'intégration du contrôleur BidListController via MockMvc.
 * Les filtres de sécurité Spring Security sont désactivés ici.
 */
@WebMvcTest(BidListController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidListService bidListService;

    /**
     * Vérifie que l'affichage de la liste des BidList fonctionne correctement.
     */
    @Test
    public void testHome_shouldReturnBidListViewWithModel() throws Exception {
        BidList bid = new BidList();
        bid.setBidListId(1);
        bid.setAccount("Account1");

        when(bidListService.getAllBid()).thenReturn(List.of(bid));

        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attributeExists("bidLists"));

        verify(bidListService, times(1)).getAllBid();
    }

    /**
     * Vérifie que l'accès au formulaire d'ajout retourne bien la vue correspondante.
     */
    @Test
    public void testAddBidForm_shouldReturnAddView() throws Exception {
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"));
    }

    /**
     * Vérifie qu'une soumission valide redirige vers la liste des bids avec un flash info.
     */
    @Test
    public void testValidate_withValidBid_shouldRedirectToList() throws Exception {
        BidList bid = new BidList();
        bid.setBidListId(1);

        when(bidListService.addBid(any(BidList.class))).thenReturn(bid);

        mockMvc.perform(post("/bidList/validate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "account1")
                        .param("type", "type1")
                        .param("bidQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"))
                .andExpect(flash().attributeExists("info"));

        verify(bidListService, times(1)).addBid(any(BidList.class));
    }

    /**
     * Vérifie que des erreurs de validation retournent le formulaire d'ajout avec erreurs.
     */
    @Test
    public void testValidate_withValidationErrors_shouldReturnAddView() throws Exception {
        mockMvc.perform(post("/bidList/validate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "")
                        .param("type", "type1")
                        .param("bidQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        verify(bidListService, never()).addBid(any(BidList.class));
    }

    /**
     * Vérifie que l'affichage du formulaire de mise à jour fonctionne pour un ID existant.
     */
    @Test
    public void testShowUpdateForm_withExistingId_shouldReturnUpdateView() throws Exception {
        BidList bid = new BidList();
        bid.setBidListId(1);
        bid.setAccount("account1");

        when(bidListService.getBidById(1)).thenReturn(bid);

        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("bidList"));

        verify(bidListService, times(1)).getBidById(1);
    }

    /**
     * Vérifie que si l'ID est inexistant, l'utilisateur est redirigé vers la liste.
     */
    @Test
    public void testShowUpdateForm_withNonExistingId_shouldRedirectToList() throws Exception {
        when(bidListService.getBidById(1)).thenReturn(null);

        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListService, times(1)).getBidById(1);
    }

    /**
     * Vérifie que la mise à jour avec des données valides redirige vers la liste.
     */
    @Test
    public void testUpdateBid_withValidBid_shouldRedirectToList() throws Exception {
        when(bidListService.updateBidList(eq(1), any(BidList.class))).thenReturn(new BidList());

        mockMvc.perform(post("/bidList/update/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "account1")
                        .param("type", "type1")
                        .param("bidQuantity", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListService, times(1)).updateBidList(eq(1), any(BidList.class));
    }

    /**
     * Vérifie qu'une mise à jour avec erreur de validation renvoie le formulaire de mise à jour.
     */
    @Test
    public void testUpdateBid_withValidationErrors_shouldReturnUpdateView() throws Exception {
        mockMvc.perform(post("/bidList/update/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "")
                        .param("type", "type1")
                        .param("bidQuantity", "15"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        verify(bidListService, never()).updateBidList(anyInt(), any(BidList.class));
    }

    /**
     * Vérifie que la suppression d'un Bid redirige correctement vers la liste.
     */
    @Test
    public void testDeleteBid_shouldRedirectToList() throws Exception {
        doNothing().when(bidListService).deleteBidList(1);

        mockMvc.perform(get("/bidList/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListService, times(1)).deleteBidList(1);
    }
}
