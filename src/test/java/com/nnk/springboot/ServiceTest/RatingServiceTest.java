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

/**
 * Tests unitaires pour la classe {@link RatingService}.
 * <p>
 * Cette classe teste les différentes méthodes CRUD du service {@code RatingService}
 * en utilisant Mockito pour simuler les interactions avec le {@link RatingRepository}.
 * </p>
 * <ul>
 *   <li>Test de la récupération de tous les ratings avec succès et gestion d'exception.</li>
 *   <li>Test de l'ajout d'un rating avec succès, gestion d'un paramètre null et gestion d'exception lors de la sauvegarde.</li>
 *   <li>Test de la récupération d'un rating par son identifiant.</li>
 *   <li>Test de la mise à jour d'un rating existant.</li>
 *   <li>Test de la suppression d'un rating.</li>
 * </ul>
 */
class RatingServiceTest {

    @InjectMocks
    private RatingService ratingService;

    @Mock
    private RatingRepository ratingRepository;

    private Rating sampleRating;

    /**
     * Initialise les mocks et crée un objet {@link Rating} exemple avant chaque test.
     */
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

    /**
     * Teste la récupération de tous les ratings avec succès.
     * Vérifie que la liste retournée contient bien le rating simulé.
     */
    @Test
    void testGetAllRatingSuccess() {
        when(ratingRepository.findAll()).thenReturn(List.of(sampleRating));

        List<Rating> ratings = ratingService.getAllRating();

        assertEquals(1, ratings.size());
        assertEquals(sampleRating, ratings.get(0));
        verify(ratingRepository, times(1)).findAll();
    }

    /**
     * Teste le comportement lorsque la récupération de tous les ratings
     * provoque une exception (ex : erreur base de données).
     * Vérifie que l'exception est bien propagée avec un message approprié.
     */
    @Test
    void testGetAllRatingThrowsException() {
        when(ratingRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ratingService.getAllRating());
        assertTrue(ex.getMessage().contains("récupération des Ratings"));
        verify(ratingRepository).findAll();
    }

    /**
     * Teste l'ajout d'un rating avec succès.
     * Vérifie que l'objet retourné est identique à celui sauvegardé.
     */
    @Test
    void testAddRatingSuccess() {
        when(ratingRepository.save(sampleRating)).thenReturn(sampleRating);

        Rating saved = ratingService.addRating(sampleRating);

        assertEquals(sampleRating, saved);
        verify(ratingRepository).save(sampleRating);
    }

    /**
     * Teste que l'ajout d'un rating null lève une {@link IllegalArgumentException}
     * avec un message explicite.
     */
    @Test
    void testAddRatingThrowsIllegalArgumentExceptionIfNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ratingService.addRating(null));
        assertEquals("Le rating ne peut pas être null.", ex.getMessage());
        verify(ratingRepository, never()).save(any());
    }

    /**
     * Teste la gestion d'une exception lors de la sauvegarde d'un rating.
     * Vérifie que l'exception est propagée avec un message approprié.
     */
    @Test
    void testAddRatingThrowsRuntimeExceptionOnSave() {
        when(ratingRepository.save(sampleRating)).thenThrow(new RuntimeException("Erreur BDD"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> ratingService.addRating(sampleRating));
        assertTrue(ex.getMessage().contains("ajout du Rating"));
        verify(ratingRepository).save(sampleRating);
    }

    /**
     * Teste la récupération d'un rating par son identifiant avec succès.
     * Vérifie que le rating retourné correspond bien au rating simulé.
     */
    @Test
    void testGetRatingByIdSuccess() {
        when(ratingRepository.findById(1)).thenReturn(Optional.of(sampleRating));

        Rating result = ratingService.getRatingById(1);

        assertEquals(sampleRating, result);
        verify(ratingRepository).findById(1);
    }

    /**
     * Teste la mise à jour d'un rating existant.
     * Vérifie que les propriétés du rating sont bien modifiées et sauvegardées.
     */
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

    /**
     * Teste la suppression d'un rating existant.
     * Vérifie que la méthode delete du repository est appelée avec le bon rating.
     */
    @Test
    void testDeleteRatingSuccess() {
        when(ratingRepository.findById(1)).thenReturn(Optional.of(sampleRating));

        ratingService.deleteRating(1);

        verify(ratingRepository).delete(sampleRating);
    }
}
