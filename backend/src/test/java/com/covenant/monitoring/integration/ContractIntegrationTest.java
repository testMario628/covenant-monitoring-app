package com.covenant.monitoring.integration;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.repository.ContractRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractRepository contractRepository;

    private Contract testContract;

    @BeforeEach
    void setUp() {
        // Pulisci il repository prima di ogni test
        contractRepository.deleteAll();

        // Crea un contratto di test
        testContract = new Contract();
        testContract.setTitle("Finanziamento Progetto Eolico");
        testContract.setLegalEntity("Enel Green Power");
        testContract.setCountry("Italia");
        testContract = contractRepository.save(testContract);
    }

    @AfterEach
    void tearDown() {
        // Pulisci il repository dopo ogni test
        contractRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllContracts_shouldReturnAllContracts() throws Exception {
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testContract.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is("Finanziamento Progetto Eolico")))
                .andExpect(jsonPath("$[0].legalEntity", is("Enel Green Power")))
                .andExpect(jsonPath("$[0].country", is("Italia")));

        List<Contract> contracts = contractRepository.findAll();
        assertEquals(1, contracts.size());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContractById_withValidId_shouldReturnContract() throws Exception {
        mockMvc.perform(get("/contracts/{id}", testContract.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testContract.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Finanziamento Progetto Eolico")))
                .andExpect(jsonPath("$.legalEntity", is("Enel Green Power")))
                .andExpect(jsonPath("$.country", is("Italia")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContractById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/contracts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createContract_shouldCreateAndReturnContract() throws Exception {
        String newContractJson = "{"
                + "\"title\": \"Nuovo Finanziamento\","
                + "\"legalEntity\": \"Enel S.p.A.\","
                + "\"country\": \"Italia\""
                + "}";

        mockMvc.perform(post("/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContractJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Nuovo Finanziamento")))
                .andExpect(jsonPath("$.legalEntity", is("Enel S.p.A.")))
                .andExpect(jsonPath("$.country", is("Italia")));

        List<Contract> contracts = contractRepository.findAll();
        assertEquals(2, contracts.size());
        assertTrue(contracts.stream().anyMatch(c -> c.getTitle().equals("Nuovo Finanziamento")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateContract_withValidId_shouldUpdateAndReturnContract() throws Exception {
        String updatedContractJson = "{"
                + "\"id\": " + testContract.getId() + ","
                + "\"title\": \"Finanziamento Progetto Eolico Aggiornato\","
                + "\"legalEntity\": \"Enel Green Power\","
                + "\"country\": \"Italia\""
                + "}";

        mockMvc.perform(put("/contracts/{id}", testContract.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedContractJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testContract.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Finanziamento Progetto Eolico Aggiornato")));

        Contract updatedContract = contractRepository.findById(testContract.getId()).orElseThrow();
        assertEquals("Finanziamento Progetto Eolico Aggiornato", updatedContract.getTitle());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteContract_withValidId_shouldDeleteContract() throws Exception {
        mockMvc.perform(delete("/contracts/{id}", testContract.getId()))
                .andExpect(status().isNoContent());

        List<Contract> contracts = contractRepository.findAll();
        assertEquals(0, contracts.size());
    }

    @Test
    void getAllContracts_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createContract_withUserRole_shouldReturnForbidden() throws Exception {
        String newContractJson = "{"
                + "\"title\": \"Nuovo Finanziamento\","
                + "\"legalEntity\": \"Enel S.p.A.\","
                + "\"country\": \"Italia\""
                + "}";

        mockMvc.perform(post("/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContractJson))
                .andExpect(status().isForbidden());

        List<Contract> contracts = contractRepository.findAll();
        assertEquals(1, contracts.size()); // Solo il contratto di test dovrebbe esistere
    }
}
