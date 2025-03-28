package com.covenant.monitoring.service;

import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.repository.MonitoringResultRepository;
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
public class MonitoringResultServiceTest {

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    @InjectMocks
    private MonitoringResultService monitoringResultService;

    private MonitoringResult result1;
    private MonitoringResult result2;
    private List<MonitoringResult> resultList;

    @BeforeEach
    void setUp() {
        result1 = new MonitoringResult();
        result1.setId(1L);
        result1.setCovenantId(1L);
        result1.setActualValue(1.5);
        result1.setStatus("Compliant");
        result1.setReviewDate(LocalDate.now().minusDays(15));
        result1.setNotes("Il covenant è rispettato con un margine del 25%");
        result1.setReviewedBy("Mario Rossi");

        result2 = new MonitoringResult();
        result2.setId(2L);
        result2.setCovenantId(2L);
        result2.setActualValue(3.2);
        result2.setStatus("Non Compliant");
        result2.setReviewDate(LocalDate.now().minusDays(10));
        result2.setNotes("Il covenant non è rispettato, superato del 6.7%");
        result2.setReviewedBy("Mario Rossi");

        resultList = Arrays.asList(result1, result2);
    }

    @Test
    void getAllMonitoringResults_shouldReturnAllResults() {
        when(monitoringResultRepository.findAll()).thenReturn(resultList);

        List<MonitoringResult> result = monitoringResultService.getAllMonitoringResults();

        assertEquals(2, result.size());
        assertEquals("Compliant", result.get(0).getStatus());
        assertEquals("Non Compliant", result.get(1).getStatus());
        verify(monitoringResultRepository, times(1)).findAll();
    }

    @Test
    void getMonitoringResultById_withValidId_shouldReturnResult() {
        when(monitoringResultRepository.findById(1L)).thenReturn(Optional.of(result1));

        Optional<MonitoringResult> result = monitoringResultService.getMonitoringResultById(1L);

        assertTrue(result.isPresent());
        assertEquals(1.5, result.get().getActualValue());
        assertEquals("Compliant", result.get().getStatus());
        verify(monitoringResultRepository, times(1)).findById(1L);
    }

    @Test
    void getMonitoringResultById_withInvalidId_shouldReturnEmpty() {
        when(monitoringResultRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<MonitoringResult> result = monitoringResultService.getMonitoringResultById(99L);

        assertFalse(result.isPresent());
        verify(monitoringResultRepository, times(1)).findById(99L);
    }

    @Test
    void saveMonitoringResult_shouldReturnSavedResult() {
        MonitoringResult newResult = new MonitoringResult();
        newResult.setCovenantId(3L);
        newResult.setActualValue(2.7);
        newResult.setStatus("Compliant");
        newResult.setReviewDate(LocalDate.now());
        newResult.setNotes("Nuovo monitoraggio");
        newResult.setReviewedBy("Luigi Bianchi");

        MonitoringResult savedResult = new MonitoringResult();
        savedResult.setId(3L);
        savedResult.setCovenantId(3L);
        savedResult.setActualValue(2.7);
        savedResult.setStatus("Compliant");
        savedResult.setReviewDate(LocalDate.now());
        savedResult.setNotes("Nuovo monitoraggio");
        savedResult.setReviewedBy("Luigi Bianchi");

        when(monitoringResultRepository.save(any(MonitoringResult.class))).thenReturn(savedResult);

        MonitoringResult result = monitoringResultService.saveMonitoringResult(newResult);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(2.7, result.getActualValue());
        assertEquals("Compliant", result.getStatus());
        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
    }

    @Test
    void updateMonitoringResult_shouldReturnUpdatedResult() {
        MonitoringResult updatedResult = new MonitoringResult();
        updatedResult.setId(1L);
        updatedResult.setCovenantId(1L);
        updatedResult.setActualValue(1.6);
        updatedResult.setStatus("Compliant");
        updatedResult.setReviewDate(LocalDate.now().minusDays(15));
        updatedResult.setNotes("Il covenant è rispettato con un margine del 33%");
        updatedResult.setReviewedBy("Mario Rossi");

        when(monitoringResultRepository.save(any(MonitoringResult.class))).thenReturn(updatedResult);

        MonitoringResult result = monitoringResultService.saveMonitoringResult(updatedResult);

        assertNotNull(result);
        assertEquals(1.6, result.getActualValue());
        assertEquals("Il covenant è rispettato con un margine del 33%", result.getNotes());
        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
    }

    @Test
    void deleteMonitoringResult_shouldCallRepositoryDelete() {
        doNothing().when(monitoringResultRepository).deleteById(anyLong());

        monitoringResultService.deleteMonitoringResult(1L);

        verify(monitoringResultRepository, times(1)).deleteById(1L);
    }

    @Test
    void getMonitoringResultsByCovenantId_shouldReturnFilteredResults() {
        when(monitoringResultRepository.findByCovenantId(1L)).thenReturn(List.of(result1));

        List<MonitoringResult> result = monitoringResultService.getMonitoringResultsByCovenantId(1L);

        assertEquals(1, result.size());
        assertEquals(1.5, result.get(0).getActualValue());
        assertEquals("Compliant", result.get(0).getStatus());
        verify(monitoringResultRepository, times(1)).findByCovenantId(1L);
    }

    @Test
    void getLatestMonitoringResults_shouldReturnLatestResults() {
        when(monitoringResultRepository.findTop10ByOrderByReviewDateDesc()).thenReturn(resultList);

        List<MonitoringResult> result = monitoringResultService.getLatestMonitoringResults();

        assertEquals(2, result.size());
        assertEquals("Compliant", result.get(0).getStatus());
        assertEquals("Non Compliant", result.get(1).getStatus());
        verify(monitoringResultRepository, times(1)).findTop10ByOrderByReviewDateDesc();
    }

    @Test
    void getNonCompliantResults_shouldReturnNonCompliantResults() {
        when(monitoringResultRepository.findByStatus("Non Compliant")).thenReturn(List.of(result2));

        List<MonitoringResult> result = monitoringResultService.getNonCompliantResults();

        assertEquals(1, result.size());
        assertEquals(3.2, result.get(0).getActualValue());
        assertEquals("Non Compliant", result.get(0).getStatus());
        verify(monitoringResultRepository, times(1)).findByStatus("Non Compliant");
    }
}
