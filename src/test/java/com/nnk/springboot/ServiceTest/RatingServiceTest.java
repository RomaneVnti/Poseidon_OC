package com.nnk.springboot.ServiceTest;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    @InjectMocks
    private RatingService ratingService;

    @Mock
    private RatingRepository ratingRepository;

    private Rating sampleRating;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleRating = new Rating();
        sampleRating.setId(1);
        sampleRating.setMoodysRating("Moody");
        sampleRating.setFitchRating("Fitch");
        sampleRating.setSandRating("S&P");
        sampleRating.setOrderNumber(5);
    }

    @Test
    void testGetAllRatingSuccess() {
        when(ratingRepository.findAll()).thenReturn(List.of(sampleRating));

        List<Rating> ratings = ratingService.getAllRating();

        assertEquals(1, ratings.size());
        assertEquals(sampleRating, ratings.get(0));
        verify(ratingRepository, times(1)).findAll();
    }

    @Test
    void testGetAllRatingThrowsException() {
        when(ratingRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ratingService.getAllRating());
        assertTrue(ex.getMessage().contains("récupération des Ratings"));
        verify(ratingRepository).findAll();
    }

    @Test
    void testAddRatingSuccess() {
        when(ratingRepository.save(sampleRating)).thenReturn(sampleRating);

        Rating saved = ratingService.addRating(sampleRating);

        assertEquals(sampleRating, saved);
        verify(ratingRepository).save(sampleRating);
    }

    @Test
    void testAddRatingThrowsIllegalArgumentExceptionIfNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ratingService.addRating(null));
        assertEquals("Le rating ne peut pas être null.", ex.getMessage());
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void testAddRatingThrowsRuntimeExceptionOnSave() {
        when(ratingRepository.save(sampleRating)).thenThrow(new RuntimeException("Erreur BDD"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ratingService.addRating(sampleRating));
        assertTrue(ex.getMessage().contains("ajout du Rating"));
        verify(ratingRepository).save(sampleRating);
    }

    @Test
    void testGetRatingByIdSuccess() {
        when(ratingRepository.findById(1)).thenReturn(Optional.of(sampleRating));

        Rating result = ratingService.getRatingById(1);

        assertEquals(sampleRating, result);
        verify(ratingRepository).findById(1);
    }



    @Test
    void testUpdateRatingSuccess() {
        Rating updated = new Rating();
        updated.setMoodysRating("New Moody");
        updated.setFitchRating("New Fitch");
        updated.setSandRating("New S&P");
        updated.setOrderNumber(10);

        when(ratingRepository.findById(1)).thenReturn(Optional.of(sampleRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(sampleRating);

        Rating result = ratingService.updateRating(1, updated);

        assertEquals(sampleRating, result);
        assertEquals("New Moody", sampleRating.getMoodysRating());
        assertEquals("New Fitch", sampleRating.getFitchRating());
        assertEquals("New S&P", sampleRating.getSandRating());
        assertEquals(10, sampleRating.getOrderNumber());
        verify(ratingRepository).save(sampleRating);
    }



    @Test
    void testDeleteRatingSuccess() {
        when(ratingRepository.findById(1)).thenReturn(Optional.of(sampleRating));

        ratingService.deleteRating(1);

        verify(ratingRepository).delete(sampleRating);
    }


}
