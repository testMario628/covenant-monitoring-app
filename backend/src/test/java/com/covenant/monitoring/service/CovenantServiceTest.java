package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.repository.CovenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CovenantServiceTest {

    @Mock
    private CovenantRepository covenantRepository;

    @InjectMocks
    private CovenantService covenantService;

    private Covenant covenant1;
    private Covenant covenant2;
    private List<Covenant> covenantList;

    @BeforeEach
    void setUp() {
        covenant1 = new Covenant();
        covenant1.setId(1L);
        covenant1.setName("Debt Service Coverage Ratio");
        covenant1.setDescription("Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.2");
        covenant1.setThreshold(1.2);
        covenant1.setFrequency("Trimestrale");
        covenant1.setNextReviewDate(LocalDate.now().plusMonths(3));
        covenant1.setContractId(1L);

        covenant2 = new Covenant();
        covenant2.setId(2L);
        covenant2.setName("Leverage Ratio");
        covenant2.setDescription("Il rapporto tra debito e patrimonio netto deve essere inferiore a 3.0");
        covenant2.setThreshold(3.0);
        covenant2.setFrequency("Semestrale");
        covenant2.setNextReviewDate(LocalDate.now().plusMonths(6));
        covenant2.setContractId(1L);

        covenantList = Arrays.asList(covenant1, covenant2);
    }

    @Test
    void getAllCovenants_shouldReturnAllCovenants() {
        when(covenantRepository.findAll()).thenReturn(covenantList);

        List<Covenant> result = covenantService.getAllCovenants();

        assertEquals(2, result.size());
        assertEquals("Debt Service Coverage Ratio", result.get(0).getName());
        assertEquals("Leverage Ratio", result.get(1).getName());
        verify(covenantRepository, times(1)).findAll();
    }

    @Test
    void getCovenantById_withValidId_shouldReturnCovenant() {
        when(covenantRepository.findById(1L)).thenReturn(Optional.of(covenant1));

        Optional<Covenant> result = covenantService.getCovenantById(1L);

        assertTrue(result.isPresent());
        assertEquals("Debt Service Coverage Ratio", result.get().getName());
        verify(covenantRepository, times(1)).findById(1L);
    }

    @Test
    void getCovenantById_withInvalidId_shouldReturnEmpty() {
        when(covenantRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Covenant> result = covenantService.getCovenantById(99L);

        assertFalse(result.isPresent());
        verify(covenantRepository, times(1)).findById(99L);
    }

    @Test
    void saveCovenant_shouldReturnSavedCovenant() {
        Covenant newCovenant = new Covenant();
        newCovenant.setName("Interest Coverage Ratio");
        newCovenant.setDescription("Il rapporto tra EBITDA e interessi deve essere superiore a 2.5");
        newCovenant.setThreshold(2.5);
        newCovenant.setFrequency("Annuale");
        newCovenant.setNextReviewDate(LocalDate.now().plusMonths(12));
        newCovenant.setContractId(2L);

        Covenant savedCovenant = new Covenant();
        savedCovenant.setId(3L);
        savedCovenant.setName("Interest Coverage Ratio");
        savedCovenant.setDescription("Il rapporto tra EBITDA e interessi deve essere superiore a 2.5");
        savedCovenant.setThreshold(2.5);
        savedCovenant.setFrequency("Annuale");
        savedCovenant.setNextReviewDate(LocalDate.now().plusMonths(12));
        savedCovenant.setContractId(2L);

        when(covenantRepository.save(any(Covenant.class))).thenReturn(savedCovenant);

        Covenant result = covenantService.saveCovenant(newCovenant);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Interest Coverage Ratio", result.getName());
        verify(covenantRepository, times(1)).save(any(Covenant.class));
    }

    @Test
    void updateCovenant_shouldReturnUpdatedCovenant() {
        Covenant updatedCovenant = new Covenant();
        updatedCovenant.setId(1L);
        updatedCovenant.setName("Debt Service Coverage Ratio");
        updatedCovenant.setDescription("Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.3");
        updatedCovenant.setThreshold(1.3);
        updatedCovenant.setFrequency("Trimestrale");
        updatedCovenant.setNextReviewDate(LocalDate.now().plusMonths(3));
        updatedCovenant.setContractId(1L);

        when(covenantRepository.save(any(Covenant.class))).thenReturn(updatedCovenant);

        Covenant result = covenantService.saveCovenant(updatedCovenant);

        assertNotNull(result);
        assertEquals(1.3, result.getThreshold());
        verify(covenantRepository, times(1)).save(any(Covenant.class));
    }

    @Test
    void deleteCovenant_shouldCallRepositoryDelete() {
        doNothing().when(covenantRepository).deleteById(anyLong());

        covenantService.deleteCovenant(1L);

        verify(covenantRepository, times(1)).deleteById(1L);
    }

    @Test
    void getCovenantsByContractId_shouldReturnFilteredCovenants() {
        when(covenantRepository.findByContractId(1L)).thenReturn(covenantList);

        List<Covenant> result = covenantService.getCovenantsByContractId(1L);

        assertEquals(2, result.size());
        assertEquals("Debt Service Coverage Ratio", result.get(0).getName());
        assertEquals("Leverage Ratio", result.get(1).getName());
        verify(covenantRepository, times(1)).findByContractId(1L);
    }

    @Test
    void getCovenantsDueForReview_shouldReturnCovenantsDueForReview() {
        LocalDate reviewDate = LocalDate.now().plusDays(30);
        when(covenantRepository.findByNextReviewDateBefore(reviewDate)).thenReturn(List.of(covenant1));

        List<Covenant> result = covenantService.getCovenantsDueForReview(30);

        assertEquals(1, result.size());
        assertEquals("Debt Service Coverage Ratio", result.get(0).getName());
        verify(covenantRepository, times(1)).findByNextReviewDateBefore(reviewDate);
    }
}
