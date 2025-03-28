package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.service.CovenantService;
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

@WebMvcTest(CovenantController.class)
public class CovenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CovenantService covenantService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "USER")
    void getAllCovenants_shouldReturnAllCovenants() throws Exception {
        when(covenantService.getAllCovenants()).thenReturn(covenantList);

        mockMvc.perform(get("/covenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Leverage Ratio")));

        verify(covenantService, times(1)).getAllCovenants();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantById_withValidId_shouldReturnCovenant() throws Exception {
        when(covenantService.getCovenantById(1L)).thenReturn(Optional.of(covenant1));

        mockMvc.perform(get("/covenants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$.threshold", is(1.2)))
                .andExpect(jsonPath("$.frequency", is("Trimestrale")));

        verify(covenantService, times(1)).getCovenantById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(covenantService.getCovenantById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/covenants/99"))
                .andExpect(status().isNotFound());

        verify(covenantService, times(1)).getCovenantById(99L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantsByContractId_shouldReturnCovenants() throws Exception {
        when(covenantService.getCovenantsByContractId(1L)).thenReturn(covenantList);

        mockMvc.perform(get("/covenants/contract/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Leverage Ratio")));

        verify(covenantService, times(1)).getCovenantsByContractId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCovenant_shouldReturnCreatedCovenant() throws Exception {
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

        when(covenantService.saveCovenant(any(Covenant.class))).thenReturn(savedCovenant);

        mockMvc.perform(post("/covenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCovenant)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Interest Coverage Ratio")))
                .andExpect(jsonPath("$.threshold", is(2.5)))
                .andExpect(jsonPath("$.frequency", is("Annuale")));

        verify(covenantService, times(1)).saveCovenant(any(Covenant.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCovenant_withValidId_shouldReturnUpdatedCovenant() throws Exception {
        Covenant updatedCovenant = new Covenant();
        updatedCovenant.setId(1L);
        updatedCovenant.setName("Debt Service Coverage Ratio");
        updatedCovenant.setDescription("Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.3");
        updatedCovenant.setThreshold(1.3);
        updatedCovenant.setFrequency("Trimestrale");
        updatedCovenant.setNextReviewDate(LocalDate.now().plusMonths(3));
        updatedCovenant.setContractId(1L);

        when(covenantService.getCovenantById(1L)).thenReturn(Optional.of(covenant1));
        when(covenantService.saveCovenant(any(Covenant.class))).thenReturn(updatedCovenant);

        mockMvc.perform(put("/covenants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCovenant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.threshold", is(1.3)));

        verify(covenantService, times(1)).getCovenantById(1L);
        verify(covenantService, times(1)).saveCovenant(any(Covenant.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCovenant_withInvalidId_shouldReturnNotFound() throws Exception {
        Covenant updatedCovenant = new Covenant();
        updatedCovenant.setId(99L);
        updatedCovenant.setName("Covenant Inesistente");

        when(covenantService.getCovenantById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/covenants/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCovenant)))
                .andExpect(status().isNotFound());

        verify(covenantService, times(1)).getCovenantById(99L);
        verify(covenantService, never()).saveCovenant(any(Covenant.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCovenant_withValidId_shouldReturnNoContent() throws Exception {
        when(covenantService.getCovenantById(1L)).thenReturn(Optional.of(covenant1));
        doNothing().when(covenantService).deleteCovenant(anyLong());

        mockMvc.perform(delete("/covenants/1"))
                .andExpect(status().isNoContent());

        verify(covenantService, times(1)).getCovenantById(1L);
        verify(covenantService, times(1)).deleteCovenant(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCovenant_withInvalidId_shouldReturnNotFound() throws Exception {
        when(covenantService.getCovenantById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/covenants/99"))
                .andExpect(status().isNotFound());

        verify(covenantService, times(1)).getCovenantById(99L);
        verify(covenantService, never()).deleteCovenant(anyLong());
    }

    @Test
    void getAllCovenants_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/covenants"))
                .andExpect(status().isUnauthorized());

        verify(covenantService, never()).getAllCovenants();
    }
}
