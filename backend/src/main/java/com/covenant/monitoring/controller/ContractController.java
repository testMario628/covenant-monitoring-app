package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Contract>> getAllContracts() {
        List<Contract> contracts = contractService.getAllContracts();
        return new ResponseEntity<>(contracts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        Optional<Contract> contract = contractService.getContractById(id);
        return contract.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/contract-id/{contractId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Contract> getContractByContractId(@PathVariable String contractId) {
        Optional<Contract> contract = contractService.getContractByContractId(contractId);
        return contract.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/country/{country}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Contract>> getContractsByCountry(@PathVariable String country) {
        List<Contract> contracts = contractService.getContractsByCountry(country);
        return new ResponseEntity<>(contracts, HttpStatus.OK);
    }

    @GetMapping("/legal-entity/{legalEntity}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Contract>> getContractsByLegalEntity(@PathVariable String legalEntity) {
        List<Contract> contracts = contractService.getContractsByLegalEntity(legalEntity);
        return new ResponseEntity<>(contracts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Contract>> getContractsByStatus(@PathVariable String status) {
        List<Contract> contracts = contractService.getContractsByStatus(status);
        return new ResponseEntity<>(contracts, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY')")
    public ResponseEntity<Contract> createContract(@RequestBody Contract contract) {
        Contract savedContract = contractService.saveContract(contract);
        return new ResponseEntity<>(savedContract, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY')")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody Contract contract) {
        Optional<Contract> existingContract = contractService.getContractById(id);
        if (existingContract.isPresent()) {
            contract.setId(id);
            Contract updatedContract = contractService.saveContract(contract);
            return new ResponseEntity<>(updatedContract, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        Optional<Contract> existingContract = contractService.getContractById(id);
        if (existingContract.isPresent()) {
            contractService.deleteContract(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
