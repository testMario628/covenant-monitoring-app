package com.covenant.monitoring.integration;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.repository.ContractRepository;
import com.covenant.monitoring.repository.CovenantRepository;
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
public class CovenantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CovenantRepository covenantRepository;

    @Autowired
    private ContractRepository contractRepository;

    private Covenant testCovenant;
    private Contract testContract;

    @BeforeEach
    void setUp() {
        // Pulisci i repository prima di ogni test
        covenantRepository.deleteAll();
        contractRepository.deleteAll();

        // Crea un contratto di test
        testContract = new Contract();
        testContract.setTitle("Finanziamento Progetto Eolico");
        testContract.setLegalEntity("Enel Green Power");
        testContract.setCountry("Italia");
        testContract = contractRepository.save(testContract);

        // Crea un covenant di test
        testCovenant = new Covenant();
        testCovenant.setName("Debt Service Coverage Ratio");
        testCovenant.setDescription("Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.2");
        testCovenant.setThreshold(1.2);
        testCovenant.setFrequency("Trimestrale");
        testCovenant.setNextReviewDate(LocalDate.now().plusMonths(3));
        testCovenant.setContractId(testContract.getId());
        testCovenant = covenantRepository.save(testCovenant);
    }

    @AfterEach
    void tearDown() {
        // Pulisci i repository dopo ogni test
        covenantRepository.deleteAll();
        contractRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCovenants_shouldReturnAllCovenants() throws Exception {
        mockMvc.perform(get("/covenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$[0].threshold", is(1.2)))
                .andExpect(jsonPath("$[0].frequency", is("Trimestrale")));

        List<Covenant> covenants = covenantRepository.findAll();
        assertEquals(1, covenants.size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantById_withValidId_shouldReturnCovenant() throws Exception {
        mockMvc.perform(get("/covenants/{id}", testCovenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$.threshold", is(1.2)))
                .andExpect(jsonPath("$.contractId", is(testContract.getId().intValue())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/covenants/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCovenantsByContractId_shouldReturnCovenants() throws Exception {
        mockMvc.perform(get("/covenants/contract/{id}", testContract.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is("Debt Service Coverage Ratio")))
                .andExpect(jsonPath("$[0].contractId", is(testContract.getId().intValue())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCovenant_shouldCreateAndReturnCovenant() throws Exception {
        String newCovenantJson = "{"
                + "\"name\": \"Interest Coverage Ratio\","
                + "\"description\": \"Il rapporto tra EBITDA e interessi deve essere superiore a 2.5\","
                + "\"threshold\": 2.5,"
                + "\"frequency\": \"Annuale\","
                + "\"nextReviewDate\": \"" + LocalDate.now().plusMonths(12) + "\","
                + "\"contractId\": " + testContract.getId()
                + "}";

        mockMvc.perform(post("/covenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCovenantJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Interest Coverage Ratio")))
                .andExpect(jsonPath("$.threshold", is(2.5)))
                .andExpect(jsonPath("$.frequency", is("Annuale")));

        List<Covenant> covenants = covenantRepository.findAll();
        assertEquals(2, covenants.size());
        assertTrue(covenants.stream().anyMatch(c -> c.getName().equals("Interest Coverage Ratio")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCovenant_withValidId_shouldUpdateAndReturnCovenant() throws Exception {
        String updatedCovenantJson = "{"
                + "\"id\": " + testCovenant.getId() + ","
                + "\"name\": \"Debt Service Coverage Ratio\","
                + "\"description\": \"Il rapporto tra flusso di cassa e servizio del debito deve essere superiore a 1.3\","
                + "\"threshold\": 1.3,"
                + "\"frequency\": \"Trimestrale\","
                + "\"nextReviewDate\": \"" + testCovenant.getNextReviewDate() + "\","
                + "\"contractId\": " + testContract.getId()
                + "}";

        mockMvc.perform(put("/covenants/{id}", testCovenant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedCovenantJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCovenant.getId().intValue())))
                .andExpect(jsonPath("$.threshold", is(1.3)));

        Covenant updatedCovenant = covenantRepository.findById(testCovenant.getId()).orElseThrow();
        assertEquals(1.3, updatedCovenant.getThreshold());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCovenant_withValidId_shouldDeleteCovenant() throws Exception {
        mockMvc.perform(delete("/covenants/{id}", testCovenant.getId()))
                .andExpect(status().isNoContent());

        List<Covenant> covenants = covenantRepository.findAll();
        assertEquals(0, covenants.size());
    }

    @Test
    void getAllCovenants_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/covenants"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCovenant_withUserRole_shouldReturnForbidden() throws Exception {
        String newCovenantJson = "{"
                + "\"name\": \"Interest Coverage Ratio\","
                + "\"description\": \"Il rapporto tra EBITDA e interessi deve essere superiore a 2.5\","
                + "\"threshold\": 2.5,"
                + "\"frequency\": \"Annuale\","
                + "\"nextReviewDate\": \"" + LocalDate.now().plusMonths(12) + "\","
                + "\"contractId\": " + testContract.getId()
                + "}";

        mockMvc.perform(post("/covenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCovenantJson))
                .andExpect(status().isForbidden());

        List<Covenant> covenants = covenantRepository.findAll();
        assertEquals(1, covenants.size()); // Solo il covenant di test dovrebbe esistere
    }
}
