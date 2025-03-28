package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.service.ContractService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContractController.class)
public class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    @Autowired
    private ObjectMapper objectMapper;

    private Contract contract1;
    private Contract contract2;
    private List<Contract> contractList;

    @BeforeEach
    void setUp() {
        contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Finanziamento Progetto Eolico");
        contract1.setLegalEntity("Enel Green Power");
        contract1.setCountry("Italia");

        contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Finanziamento Impianto Solare");
        contract2.setLegalEntity("Enel Green Power");
        contract2.setCountry("Spagna");

        contractList = Arrays.asList(contract1, contract2);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllContracts_shouldReturnAllContracts() throws Exception {
        when(contractService.getAllContracts()).thenReturn(contractList);

        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Finanziamento Progetto Eolico")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Finanziamento Impianto Solare")));

        verify(contractService, times(1)).getAllContracts();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContractById_withValidId_shouldReturnContract() throws Exception {
        when(contractService.getContractById(1L)).thenReturn(Optional.of(contract1));

        mockMvc.perform(get("/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Finanziamento Progetto Eolico")))
                .andExpect(jsonPath("$.legalEntity", is("Enel Green Power")))
                .andExpect(jsonPath("$.country", is("Italia")));

        verify(contractService, times(1)).getContractById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContractById_withInvalidId_shouldReturnNotFound() throws Exception {
        when(contractService.getContractById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/contracts/99"))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).getContractById(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createContract_shouldReturnCreatedContract() throws Exception {
        Contract newContract = new Contract();
        newContract.setTitle("Nuovo Finanziamento");
        newContract.setLegalEntity("Enel S.p.A.");
        newContract.setCountry("Italia");

        Contract savedContract = new Contract();
        savedContract.setId(3L);
        savedContract.setTitle("Nuovo Finanziamento");
        savedContract.setLegalEntity("Enel S.p.A.");
        savedContract.setCountry("Italia");

        when(contractService.saveContract(any(Contract.class))).thenReturn(savedContract);

        mockMvc.perform(post("/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newContract)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Nuovo Finanziamento")))
                .andExpect(jsonPath("$.legalEntity", is("Enel S.p.A.")))
                .andExpect(jsonPath("$.country", is("Italia")));

        verify(contractService, times(1)).saveContract(any(Contract.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateContract_withValidId_shouldReturnUpdatedContract() throws Exception {
        Contract updatedContract = new Contract();
        updatedContract.setId(1L);
        updatedContract.setTitle("Finanziamento Progetto Eolico Aggiornato");
        updatedContract.setLegalEntity("Enel Green Power");
        updatedContract.setCountry("Italia");

        when(contractService.getContractById(1L)).thenReturn(Optional.of(contract1));
        when(contractService.saveContract(any(Contract.class))).thenReturn(updatedContract);

        mockMvc.perform(put("/contracts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedContract)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Finanziamento Progetto Eolico Aggiornato")));

        verify(contractService, times(1)).getContractById(1L);
        verify(contractService, times(1)).saveContract(any(Contract.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateContract_withInvalidId_shouldReturnNotFound() throws Exception {
        Contract updatedContract = new Contract();
        updatedContract.setId(99L);
        updatedContract.setTitle("Finanziamento Inesistente");

        when(contractService.getContractById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/contracts/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedContract)))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).getContractById(99L);
        verify(contractService, never()).saveContract(any(Contract.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteContract_withValidId_shouldReturnNoContent() throws Exception {
        when(contractService.getContractById(1L)).thenReturn(Optional.of(contract1));
        doNothing().when(contractService).deleteContract(anyLong());

        mockMvc.perform(delete("/contracts/1"))
                .andExpect(status().isNoContent());

        verify(contractService, times(1)).getContractById(1L);
        verify(contractService, times(1)).deleteContract(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteContract_withInvalidId_shouldReturnNotFound() throws Exception {
        when(contractService.getContractById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/contracts/99"))
                .andExpect(status().isNotFound());

        verify(contractService, times(1)).getContractById(99L);
        verify(contractService, never()).deleteContract(anyLong());
    }

    @Test
    void getAllContracts_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isUnauthorized());

        verify(contractService, never()).getAllContracts();
    }
}
