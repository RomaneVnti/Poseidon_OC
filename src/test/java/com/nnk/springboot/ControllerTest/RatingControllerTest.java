package com.nnk.springboot.ControllerTest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;

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

public class RatingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RatingService ratingService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
    }

    @Test
    public void testHome_ShouldReturnRatingListView_WithRatings() throws Exception {
        List<Rating> ratings = Arrays.asList(new Rating(), new Rating());
        when(ratingService.getAllRating()).thenReturn(ratings);

        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("ratings"));

        verify(ratingService, times(1)).getAllRating();
    }

    @Test
    public void testAddRatingForm_ShouldReturnAddView() throws Exception {
        mockMvc.perform(get("/rating/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"));
    }

    @Test
    public void testValidate_WithErrors_ShouldReturnAddView() throws Exception {
        // Simuler validation errors via BindingResult
        when(bindingResult.hasErrors()).thenReturn(true);

        // Appeler méthode directement (MockMvc ne gère pas BindingResult)
        String view = ratingController.validate(new Rating(), bindingResult, model);

        assert(view.equals("rating/add"));
        verify(model, times(1)).addAttribute(eq("rating"), any(Rating.class));
        verify(ratingService, never()).addRating(any(Rating.class));
    }

    @Test
    public void testValidate_WithoutErrors_ShouldRedirect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ratingController.validate(new Rating(), bindingResult, model);

        assert(view.equals("redirect:/rating/list"));
        verify(ratingService, times(1)).addRating(any(Rating.class));
    }

    @Test
    public void testShowUpdateForm_ShouldReturnUpdateView_WithRating() throws Exception {
        Rating rating = new Rating();
        rating.setId(1);
        when(ratingService.getRatingById(1)).thenReturn(rating);

        mockMvc.perform(get("/rating/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("rating"));

        verify(ratingService, times(1)).getRatingById(1);
    }

    @Test
    public void testUpdateRating_WithErrors_ShouldReturnUpdateView() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = ratingController.updateRating(1, new Rating(), bindingResult, model);

        assert(view.equals("rating/update"));
        verify(model, times(1)).addAttribute(eq("rating"), any(Rating.class));
        verify(ratingService, never()).updateRating(anyInt(), any(Rating.class));
    }

    @Test
    public void testUpdateRating_WithoutErrors_ShouldRedirect() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = ratingController.updateRating(1, new Rating(), bindingResult, model);

        assert(view.equals("redirect:/rating/list"));
        verify(ratingService, times(1)).updateRating(eq(1), any(Rating.class));
    }

    @Test
    public void testDeleteRating_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/rating/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

        verify(ratingService, times(1)).deleteRating(1);
    }
}
