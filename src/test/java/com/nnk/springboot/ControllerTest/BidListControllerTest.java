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

@WebMvcTest(BidListController.class)
@AutoConfigureMockMvc(addFilters = false)  // désactive Spring Security pour tests
public class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidListService bidListService;

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

    @Test
    public void testAddBidForm_shouldReturnAddView() throws Exception {
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"));
    }

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

    @Test
    public void testValidate_withValidationErrors_shouldReturnAddView() throws Exception {
        mockMvc.perform(post("/bidList/validate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "")  // champ vide -> erreur validation
                        .param("type", "type1")
                        .param("bidQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        verify(bidListService, never()).addBid(any(BidList.class));
    }

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

    @Test
    public void testShowUpdateForm_withNonExistingId_shouldRedirectToList() throws Exception {
        when(bidListService.getBidById(1)).thenReturn(null);

        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListService, times(1)).getBidById(1);
    }

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


    @Test
    public void testUpdateBid_withValidationErrors_shouldReturnUpdateView() throws Exception {
        mockMvc.perform(post("/bidList/update/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("account", "")  // erreur validation (champ vide)
                        .param("type", "type1")
                        .param("bidQuantity", "15"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        verify(bidListService, never()).updateBidList(anyInt(), any(BidList.class));
    }

    @Test
    public void testDeleteBid_shouldRedirectToList() throws Exception {
        doNothing().when(bidListService).deleteBidList(1);

        mockMvc.perform(get("/bidList/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListService, times(1)).deleteBidList(1);
    }
}
