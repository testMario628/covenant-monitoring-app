package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContractServiceTest {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractService contractService;

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
    void getAllContracts_shouldReturnAllContracts() {
        when(contractRepository.findAll()).thenReturn(contractList);

        List<Contract> result = contractService.getAllContracts();

        assertEquals(2, result.size());
        assertEquals("Finanziamento Progetto Eolico", result.get(0).getTitle());
        assertEquals("Finanziamento Impianto Solare", result.get(1).getTitle());
        verify(contractRepository, times(1)).findAll();
    }

    @Test
    void getContractById_withValidId_shouldReturnContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract1));

        Optional<Contract> result = contractService.getContractById(1L);

        assertTrue(result.isPresent());
        assertEquals("Finanziamento Progetto Eolico", result.get().getTitle());
        verify(contractRepository, times(1)).findById(1L);
    }

    @Test
    void getContractById_withInvalidId_shouldReturnEmpty() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Contract> result = contractService.getContractById(99L);

        assertFalse(result.isPresent());
        verify(contractRepository, times(1)).findById(99L);
    }

    @Test
    void saveContract_shouldReturnSavedContract() {
        Contract newContract = new Contract();
        newContract.setTitle("Nuovo Finanziamento");
        newContract.setLegalEntity("Enel S.p.A.");
        newContract.setCountry("Italia");

        Contract savedContract = new Contract();
        savedContract.setId(3L);
        savedContract.setTitle("Nuovo Finanziamento");
        savedContract.setLegalEntity("Enel S.p.A.");
        savedContract.setCountry("Italia");

        when(contractRepository.save(any(Contract.class))).thenReturn(savedContract);

        Contract result = contractService.saveContract(newContract);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Nuovo Finanziamento", result.getTitle());
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void updateContract_shouldReturnUpdatedContract() {
        Contract updatedContract = new Contract();
        updatedContract.setId(1L);
        updatedContract.setTitle("Finanziamento Progetto Eolico Aggiornato");
        updatedContract.setLegalEntity("Enel Green Power");
        updatedContract.setCountry("Italia");

        when(contractRepository.save(any(Contract.class))).thenReturn(updatedContract);

        Contract result = contractService.saveContract(updatedContract);

        assertNotNull(result);
        assertEquals("Finanziamento Progetto Eolico Aggiornato", result.getTitle());
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    void deleteContract_shouldCallRepositoryDelete() {
        doNothing().when(contractRepository).deleteById(anyLong());

        contractService.deleteContract(1L);

        verify(contractRepository, times(1)).deleteById(1L);
    }

    @Test
    void getContractsByLegalEntity_shouldReturnFilteredContracts() {
        when(contractRepository.findByLegalEntityContainingIgnoreCase("Enel")).thenReturn(contractList);

        List<Contract> result = contractService.getContractsByLegalEntity("Enel");

        assertEquals(2, result.size());
        assertEquals("Finanziamento Progetto Eolico", result.get(0).getTitle());
        assertEquals("Finanziamento Impianto Solare", result.get(1).getTitle());
        verify(contractRepository, times(1)).findByLegalEntityContainingIgnoreCase("Enel");
    }

    @Test
    void getContractsByCountry_shouldReturnFilteredContracts() {
        when(contractRepository.findByCountryContainingIgnoreCase("Italia")).thenReturn(List.of(contract1));

        List<Contract> result = contractService.getContractsByCountry("Italia");

        assertEquals(1, result.size());
        assertEquals("Finanziamento Progetto Eolico", result.get(0).getTitle());
        verify(contractRepository, times(1)).findByCountryContainingIgnoreCase("Italia");
    }
}
