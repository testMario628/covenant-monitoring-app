package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.service.MonitoringResultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MonitoringResultController.class)
public class MonitoringResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonitoringResultService monitoringResultService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "USER")
    void getAllMonitoringResults_shouldReturnAllResults() throws Exception {
        when(monitoringResultService.getAllMonitoringResults()).thenReturn(resultList);

        mockMvc.perform(get("/monitoring-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("Compliant")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("Non Compliant")));

        verify(monitoringResultService, times(1)).getAllMonitoringResults();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultById_withValidId_shouldReturnResult() throws Exception {
        when(monitoringResultService.getMonitoringResultById(1L)).thenReturn(Optional.of(result1));

        mockMvc.perform(get("/monitoring-results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.covenantId", is(1)))
                .andExpect(jsonPath("$.actualValue", is(1.5)))
                .andExpect(jsonPath("$.status", is("Compliant")));

        verify(monitoringResultService, times(1)).getMonitoringResultById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(monitoringResultService.getMonitoringResultById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/monitoring-results/99"))
                .andExpect(status().isNotFound());

        verify(monitoringResultService, times(1)).getMonitoringResultById(99L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultsByCovenantId_shouldReturnResults() throws Exception {
        when(monitoringResultService.getMonitoringResultsByCovenantId(1L)).thenReturn(List.of(result1));

        mockMvc.perform(get("/monitoring-results/covenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].covenantId", is(1)))
                .andExpect(jsonPath("$[0].status", is("Compliant")));

        verify(monitoringResultService, times(1)).getMonitoringResultsByCovenantId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMonitoringResult_shouldReturnCreatedResult() throws Exception {
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

        when(monitoringResultService.saveMonitoringResult(any(MonitoringResult.class))).thenReturn(savedResult);

        mockMvc.perform(post("/monitoring-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newResult)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.covenantId", is(3)))
                .andExpect(jsonPath("$.actualValue", is(2.7)))
                .andExpect(jsonPath("$.status", is("Compliant")));

        verify(monitoringResultService, times(1)).saveMonitoringResult(any(MonitoringResult.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMonitoringResult_withValidId_shouldReturnUpdatedResult() throws Exception {
        MonitoringResult updatedResult = new MonitoringResult();
        updatedResult.setId(1L);
        updatedResult.setCovenantId(1L);
        updatedResult.setActualValue(1.6);
        updatedResult.setStatus("Compliant");
        updatedResult.setReviewDate(LocalDate.now().minusDays(15));
        updatedResult.setNotes("Il covenant è rispettato con un margine del 33%");
        updatedResult.setReviewedBy("Mario Rossi");

        when(monitoringResultService.getMonitoringResultById(1L)).thenReturn(Optional.of(result1));
        when(monitoringResultService.saveMonitoringResult(any(MonitoringResult.class))).thenReturn(updatedResult);

        mockMvc.perform(put("/monitoring-results/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.actualValue", is(1.6)))
                .andExpect(jsonPath("$.notes", is("Il covenant è rispettato con un margine del 33%")));

        verify(monitoringResultService, times(1)).getMonitoringResultById(1L);
        verify(monitoringResultService, times(1)).saveMonitoringResult(any(MonitoringResult.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMonitoringResult_withInvalidId_shouldReturnNotFound() throws Exception {
        MonitoringResult updatedResult = new MonitoringResult();
        updatedResult.setId(99L);
        updatedResult.setCovenantId(1L);
        updatedResult.setActualValue(1.6);
        updatedResult.setStatus("Compliant");

        when(monitoringResultService.getMonitoringResultById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/monitoring-results/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedResult)))
                .andExpect(status().isNotFound());

        verify(monitoringResultService, times(1)).getMonitoringResultById(99L);
        verify(monitoringResultService, never()).saveMonitoringResult(any(MonitoringResult.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMonitoringResult_withValidId_shouldReturnNoContent() throws Exception {
        when(monitoringResultService.getMonitoringResultById(1L)).thenReturn(Optional.of(result1));
        doNothing().when(monitoringResultService).deleteMonitoringResult(anyLong());

        mockMvc.perform(delete("/monitoring-results/1"))
                .andExpect(status().isNoContent());

        verify(monitoringResultService, times(1)).getMonitoringResultById(1L);
        verify(monitoringResultService, times(1)).deleteMonitoringResult(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMonitoringResult_withInvalidId_shouldReturnNotFound() throws Exception {
        when(monitoringResultService.getMonitoringResultById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/monitoring-results/99"))
                .andExpect(status().isNotFound());

        verify(monitoringResultService, times(1)).getMonitoringResultById(99L);
        verify(monitoringResultService, never()).deleteMonitoringResult(anyLong());
    }

    @Test
    void getAllMonitoringResults_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/monitoring-results"))
                .andExpect(status().isUnauthorized());

        verify(monitoringResultService, never()).getAllMonitoringResults();
    }
}
