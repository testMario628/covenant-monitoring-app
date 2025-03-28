package com.covenant.monitoring.integration;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.repository.CovenantRepository;
import com.covenant.monitoring.repository.MonitoringResultRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MonitoringResultIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MonitoringResultRepository monitoringResultRepository;

    @Autowired
    private CovenantRepository covenantRepository;

    private MonitoringResult testResult;
    private Covenant testCovenant;

    @BeforeEach
    void setUp() {
        // Pulisci i repository prima di ogni test
        monitoringResultRepository.deleteAll();
        covenantRepository.deleteAll();

        // Crea un covenant di test
        testCovenant = new Covenant();
        testCovenant.setName("Debt Service Coverage Ratio");
        testCovenant.setDescription("Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.2");
        testCovenant.setThreshold(1.2);
        testCovenant.setFrequency("Trimestrale");
        testCovenant.setNextReviewDate(LocalDate.now().plusMonths(3));
        testCovenant.setContractId(1L);
        testCovenant = covenantRepository.save(testCovenant);

        // Crea un risultato di monitoraggio di test
        testResult = new MonitoringResult();
        testResult.setCovenantId(testCovenant.getId());
        testResult.setActualValue(1.5);
        testResult.setStatus("Compliant");
        testResult.setReviewDate(LocalDate.now().minusDays(15));
        testResult.setNotes("Il covenant è rispettato con un margine del 25%");
        testResult.setReviewedBy("Mario Rossi");
        testResult = monitoringResultRepository.save(testResult);
    }

    @AfterEach
    void tearDown() {
        // Pulisci i repository dopo ogni test
        monitoringResultRepository.deleteAll();
        covenantRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllMonitoringResults_shouldReturnAllResults() throws Exception {
        mockMvc.perform(get("/monitoring-results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testResult.getId().intValue())))
                .andExpect(jsonPath("$[0].covenantId", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$[0].actualValue", is(1.5)))
                .andExpect(jsonPath("$[0].status", is("Compliant")));

        List<MonitoringResult> results = monitoringResultRepository.findAll();
        assertEquals(1, results.size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultById_withValidId_shouldReturnResult() throws Exception {
        mockMvc.perform(get("/monitoring-results/{id}", testResult.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testResult.getId().intValue())))
                .andExpect(jsonPath("$.covenantId", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$.actualValue", is(1.5)))
                .andExpect(jsonPath("$.status", is("Compliant")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/monitoring-results/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMonitoringResultsByCovenantId_shouldReturnResults() throws Exception {
        mockMvc.perform(get("/monitoring-results/covenant/{id}", testCovenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testResult.getId().intValue())))
                .andExpect(jsonPath("$[0].covenantId", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$[0].status", is("Compliant")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMonitoringResult_shouldCreateAndReturnResult() throws Exception {
        String newResultJson = "{"
                + "\"covenantId\": " + testCovenant.getId() + ","
                + "\"actualValue\": 2.7,"
                + "\"status\": \"Compliant\","
                + "\"reviewDate\": \"" + LocalDate.now() + "\","
                + "\"notes\": \"Nuovo monitoraggio\","
                + "\"reviewedBy\": \"Luigi Bianchi\""
                + "}";

        mockMvc.perform(post("/monitoring-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newResultJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.covenantId", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$.actualValue", is(2.7)))
                .andExpect(jsonPath("$.status", is("Compliant")));

        List<MonitoringResult> results = monitoringResultRepository.findAll();
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(r -> r.getActualValue() == 2.7));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMonitoringResult_withValidId_shouldUpdateAndReturnResult() throws Exception {
        String updatedResultJson = "{"
                + "\"id\": " + testResult.getId() + ","
                + "\"covenantId\": " + testCovenant.getId() + ","
                + "\"actualValue\": 1.6,"
                + "\"status\": \"Compliant\","
                + "\"reviewDate\": \"" + testResult.getReviewDate() + "\","
                + "\"notes\": \"Il covenant è rispettato con un margine del 33%\","
                + "\"reviewedBy\": \"Mario Rossi\""
                + "}";

        mockMvc.perform(put("/monitoring-results/{id}", testResult.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedResultJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testResult.getId().intValue())))
                .andExpect(jsonPath("$.actualValue", is(1.6)))
                .andExpect(jsonPath("$.notes", is("Il covenant è rispettato con un margine del 33%")));

        MonitoringResult updatedResult = monitoringResultRepository.findById(testResult.getId()).orElseThrow();
        assertEquals(1.6, updatedResult.getActualValue());
        assertEquals("Il covenant è rispettato con un margine del 33%", updatedResult.getNotes());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMonitoringResult_withValidId_shouldDeleteResult() throws Exception {
        mockMvc.perform(delete("/monitoring-results/{id}", testResult.getId()))
                .andExpect(status().isNoContent());

        List<MonitoringResult> results = monitoringResultRepository.findAll();
        assertEquals(0, results.size());
    }

    @Test
    void getAllMonitoringResults_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/monitoring-results"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createMonitoringResult_withUserRole_shouldReturnForbidden() throws Exception {
        String newResultJson = "{"
                + "\"covenantId\": " + testCovenant.getId() + ","
                + "\"actualValue\": 2.7,"
                + "\"status\": \"Compliant\","
                + "\"reviewDate\": \"" + LocalDate.now() + "\","
                + "\"notes\": \"Nuovo monitoraggio\","
                + "\"reviewedBy\": \"Luigi Bianchi\""
                + "}";

        mockMvc.perform(post("/monitoring-results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newResultJson))
                .andExpect(status().isForbidden());

        List<MonitoringResult> results = monitoringResultRepository.findAll();
        assertEquals(1, results.size()); // Solo il risultato di test dovrebbe esistere
    }
}
