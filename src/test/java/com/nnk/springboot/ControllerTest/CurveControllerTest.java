package com.nnk.springboot.ControllerTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurvePointService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;

public class CurveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurvePointService curvePointService;

    @InjectMocks
    private CurveController curveController;

    @BeforeEach
    public void setup() {
        org.mockito.MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(curveController).build();
    }

    @Test
    public void testHome_shouldReturnListView() throws Exception {
        CurvePoint cp1 = new CurvePoint();
        CurvePoint cp2 = new CurvePoint();
        when(curvePointService.getAllCurvePoint()).thenReturn(Arrays.asList(cp1, cp2));

        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("curvePoints"))
                .andExpect(model().attribute("curvePoints", Arrays.asList(cp1, cp2)));

        verify(curvePointService, times(1)).getAllCurvePoint();
    }

    @Test
    public void testHome_shouldReturnErrorViewOnException() throws Exception {
        when(curvePointService.getAllCurvePoint()).thenThrow(new RuntimeException("Erreur BD"));

        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(curvePointService, times(1)).getAllCurvePoint();
    }

    @Test
    public void testAddBidForm_shouldReturnAddView() throws Exception {
        mockMvc.perform(get("/curvePoint/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"));
    }

    @Test
    public void testValidate_shouldRedirectToList_whenNoErrors() throws Exception {
        // Pour simuler un CurvePoint valide, on va juste passer les paramètres attendus,
        // ici on suppose au moins un champ "term" et "value" par exemple. Adapte selon ta classe.
        mockMvc.perform(post("/curvePoint/validate")
                        .param("term", "10")
                        .param("value", "20")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointService, times(1)).addCurvePoint(any(CurvePoint.class));
    }

    @Test
    public void testValidate_shouldReturnAddView_whenErrors() throws Exception {
        // Ici on simule une erreur de validation, on peut faire un post sans param ou avec un param invalide
        mockMvc.perform(post("/curvePoint/validate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"));

        verify(curvePointService, times(0)).addCurvePoint(any(CurvePoint.class));
    }

    @Test
    public void testValidate_shouldReturnErrorViewOnException() throws Exception {
        doThrow(new RuntimeException("Erreur insertion")).when(curvePointService).addCurvePoint(any(CurvePoint.class));

        mockMvc.perform(post("/curvePoint/validate")
                        .param("term", "10")
                        .param("value", "20")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(curvePointService, times(1)).addCurvePoint(any(CurvePoint.class));
    }

    @Test
    public void testShowUpdateForm_shouldReturnUpdateView() throws Exception {
        CurvePoint cp = new CurvePoint();
        when(curvePointService.getCurvePointById(1)).thenReturn(cp);

        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("curvePoint"));

        verify(curvePointService, times(1)).getCurvePointById(1);
    }

    @Test
    public void testShowUpdateForm_shouldReturnErrorViewOnException() throws Exception {
        when(curvePointService.getCurvePointById(1)).thenThrow(new RuntimeException("Non trouvé"));

        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(curvePointService, times(1)).getCurvePointById(1);
    }

    @Test
    public void testUpdateBid_shouldRedirectToList_whenNoErrors() throws Exception {
        mockMvc.perform(post("/curvePoint/update/1")
                        .param("term", "10")
                        .param("value", "20")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointService, times(1)).updateCurvePoint(eq(1), any(CurvePoint.class));
    }

    @Test
    public void testUpdateBid_shouldReturnUpdateView_whenErrors() throws Exception {
        // simulate validation error, param manquant
        mockMvc.perform(post("/curvePoint/update/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"));

        verify(curvePointService, times(0)).updateCurvePoint(anyInt(), any(CurvePoint.class));
    }

    @Test
    public void testUpdateBid_shouldReturnErrorViewOnException() throws Exception {
        doThrow(new RuntimeException("Erreur maj")).when(curvePointService).updateCurvePoint(eq(1), any(CurvePoint.class));

        mockMvc.perform(post("/curvePoint/update/1")
                        .param("term", "10")
                        .param("value", "20")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(curvePointService, times(1)).updateCurvePoint(eq(1), any(CurvePoint.class));
    }

    @Test
    public void testDeleteBid_shouldRedirectToList() throws Exception {
        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        verify(curvePointService, times(1)).deleteCurvePoint(1);
    }

    @Test
    public void testDeleteBid_shouldReturnErrorViewOnException() throws Exception {
        doThrow(new RuntimeException("Erreur suppression")).when(curvePointService).deleteCurvePoint(1);

        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(curvePointService, times(1)).deleteCurvePoint(1);
    }
}
