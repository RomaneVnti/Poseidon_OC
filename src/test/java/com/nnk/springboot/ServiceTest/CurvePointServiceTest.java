package com.nnk.springboot.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;

import com.nnk.springboot.services.CurvePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataAccessException;

class CurvePointServiceTest {

    @InjectMocks
    private CurvePointService curvePointService;

    @Mock
    private CurvePointRepository curvePointRepository;

    private CurvePoint sampleCurvePoint;

    /**
     * Initialisation avant chaque test : création d'un objet CurvePoint de test
     * avec un id, un terme et une valeur.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleCurvePoint = new CurvePoint();
        sampleCurvePoint.setId(1);
        sampleCurvePoint.setTerm(BigDecimal.valueOf(10.0));
        sampleCurvePoint.setValue(BigDecimal.valueOf(20.0));
    }

    /**
     * Test de la méthode getCurvePointById() pour un cas réussi.
     * Vérifie que l'objet retourné n'est pas nul et correspond à l'id demandé.
     */
    @Test
    void testGetCurvePointByIdSuccess() {
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(sampleCurvePoint));

        CurvePoint result = curvePointService.getCurvePointById(1);

        assertNotNull(result);
        assertEquals(sampleCurvePoint.getId(), result.getId());
        verify(curvePointRepository, times(1)).findById(1);
    }

    /**
     * Test de la méthode getCurvePointById() lorsqu'une exception est levée par le repository.
     * Vérifie que l'exception RuntimeException est bien propagée avec un message approprié.
     */
    @Test
    void testGetCurvePointByIdThrowsException() {
        when(curvePointRepository.findById(anyInt())).thenThrow(new DataAccessException("DB error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> curvePointService.getCurvePointById(1));

        assertTrue(ex.getMessage().contains("Erreur lors de la récupération"));
        verify(curvePointRepository, times(1)).findById(1);
    }

    /**
     * Test de la méthode getAllCurvePoint() pour un cas réussi.
     * Vérifie que la liste retournée contient bien les CurvePoints attendus.
     */
    @Test
    void testGetAllCurvePointSuccess() {
        List<CurvePoint> list = Arrays.asList(sampleCurvePoint);
        when(curvePointRepository.findAll()).thenReturn(list);

        List<CurvePoint> result = curvePointService.getAllCurvePoint();

        assertEquals(1, result.size());
        verify(curvePointRepository, times(1)).findAll();
    }

    /**
     * Test de la méthode getAllCurvePoint() lorsqu'une exception est levée par le repository.
     * Vérifie que l'exception RuntimeException est bien propagée avec un message approprié.
     */
    @Test
    void testGetAllCurvePointThrowsException() {
        when(curvePointRepository.findAll()).thenThrow(new DataAccessException("DB error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> curvePointService.getAllCurvePoint());

        assertTrue(ex.getMessage().contains("Erreur lors de la récupération"));
        verify(curvePointRepository, times(1)).findAll();
    }

    /**
     * Test de la méthode addCurvePoint() pour un cas réussi.
     * Vérifie que l'objet CurvePoint ajouté est bien retourné.
     */
    @Test
    void testAddCurvePointSuccess() {
        when(curvePointRepository.save(sampleCurvePoint)).thenReturn(sampleCurvePoint);

        CurvePoint result = curvePointService.addCurvePoint(sampleCurvePoint);

        assertNotNull(result);
        assertEquals(sampleCurvePoint.getTerm(), result.getTerm());
        verify(curvePointRepository, times(1)).save(sampleCurvePoint);
    }

    /**
     * Test de la méthode addCurvePoint() lorsqu'une exception est levée par le repository.
     * Vérifie que l'exception RuntimeException est bien propagée avec un message approprié.
     */
    @Test
    void testAddCurvePointThrowsException() {
        when(curvePointRepository.save(any(CurvePoint.class))).thenThrow(new DataAccessException("DB error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> curvePointService.addCurvePoint(sampleCurvePoint));

        assertTrue(ex.getMessage().contains("Erreur lors de l'ajout"));
        verify(curvePointRepository, times(1)).save(sampleCurvePoint);
    }

    /**
     * Test de la méthode updateCurvePoint() pour un cas réussi.
     * Vérifie que l'objet CurvePoint est bien mis à jour et retourné.
     */
    @Test
    void testUpdateCurvePointSuccess() {
        CurvePoint updateData = new CurvePoint();
        updateData.setTerm(BigDecimal.valueOf(50.0));
        updateData.setValue(BigDecimal.valueOf(60.0));

        when(curvePointRepository.findById(1)).thenReturn(Optional.of(sampleCurvePoint));
        when(curvePointRepository.save(any(CurvePoint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CurvePoint updated = curvePointService.updateCurvePoint(1, updateData);

        assertEquals(BigDecimal.valueOf(50.0), updated.getTerm());
        assertEquals(BigDecimal.valueOf(60.0), updated.getValue());

        verify(curvePointRepository, times(1)).findById(1);
        verify(curvePointRepository, times(1)).save(sampleCurvePoint);
    }

    /**
     * Test de la méthode updateCurvePoint() lorsqu'une exception est levée lors de la sauvegarde.
     * Vérifie que l'exception RuntimeException est bien propagée avec un message approprié.
     */
    @Test
    void testUpdateCurvePointSaveThrowsException() {
        CurvePoint updateData = new CurvePoint();
        updateData.setTerm(BigDecimal.valueOf(50.0));
        updateData.setValue(BigDecimal.valueOf(60.0));

        when(curvePointRepository.findById(1)).thenReturn(Optional.of(sampleCurvePoint));
        when(curvePointRepository.save(any(CurvePoint.class))).thenThrow(new DataAccessException("DB error") {});

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> curvePointService.updateCurvePoint(1, updateData));

        assertTrue(ex.getMessage().contains("Erreur lors de la modification"));
        verify(curvePointRepository, times(1)).findById(1);
        verify(curvePointRepository, times(1)).save(sampleCurvePoint);
    }

    /**
     * Test de la méthode deleteCurvePoint() pour un cas réussi.
     * Vérifie que la suppression ne génère pas d'exception.
     */
    @Test
    void testDeleteCurvePointSuccess() {
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(sampleCurvePoint));
        doNothing().when(curvePointRepository).delete(sampleCurvePoint);

        assertDoesNotThrow(() -> curvePointService.deleteCurvePoint(1));

        verify(curvePointRepository, times(1)).findById(1);
        verify(curvePointRepository, times(1)).delete(sampleCurvePoint);
    }

    /**
     * Test de la méthode deleteCurvePoint() lorsqu'une exception est levée lors de la suppression.
     * Vérifie que l'exception RuntimeException est bien propagée avec un message approprié.
     */
    @Test
    void testDeleteCurvePointDeleteThrowsException() {
        when(curvePointRepository.findById(1)).thenReturn(Optional.of(sampleCurvePoint));
        doThrow(new DataAccessException("DB error") {}).when(curvePointRepository).delete(sampleCurvePoint);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> curvePointService.deleteCurvePoint(1));

        assertTrue(ex.getMessage().contains("Erreur lors de la suppression"));
        verify(curvePointRepository, times(1)).findById(1);
        verify(curvePointRepository, times(1)).delete(sampleCurvePoint);
    }
}
