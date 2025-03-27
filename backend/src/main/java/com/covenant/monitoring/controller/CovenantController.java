package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.service.CovenantService;
import com.covenant.monitoring.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/covenants")
public class CovenantController {

    private final CovenantService covenantService;
    private final ContractService contractService;

    @Autowired
    public CovenantController(CovenantService covenantService, ContractService contractService) {
        this.covenantService = covenantService;
        this.contractService = contractService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Covenant>> getAllCovenants() {
        List<Covenant> covenants = covenantService.getAllCovenants();
        return new ResponseEntity<>(covenants, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Covenant> getCovenantById(@PathVariable Long id) {
        Optional<Covenant> covenant = covenantService.getCovenantById(id);
        return covenant.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/covenant-id/{covenantId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Covenant> getCovenantByCovenantId(@PathVariable String covenantId) {
        Optional<Covenant> covenant = covenantService.getCovenantByCovenantId(covenantId);
        return covenant.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/contract/{contractId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Covenant>> getCovenantsByContract(@PathVariable Long contractId) {
        Optional<Contract> contract = contractService.getContractById(contractId);
        if (contract.isPresent()) {
            List<Covenant> covenants = covenantService.getCovenantsByContract(contract.get());
            return new ResponseEntity<>(covenants, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<List<Covenant>> getCovenantsByStatus(@PathVariable String status) {
        List<Covenant> covenants = covenantService.getCovenantsByStatus(status);
        return new ResponseEntity<>(covenants, HttpStatus.OK);
    }

    @GetMapping("/due-for-monitoring")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY')")
    public ResponseEntity<List<Covenant>> getCovenantsDueForMonitoring() {
        // Get covenants that haven't been monitored in the last 3 months
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Covenant> covenants = covenantService.getCovenantsDueForMonitoring(threeMonthsAgo);
        return new ResponseEntity<>(covenants, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Covenant> createCovenant(@RequestBody Covenant covenant) {
        Covenant savedCovenant = covenantService.saveCovenant(covenant);
        return new ResponseEntity<>(savedCovenant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LEGAL_HOLDING', 'ROLE_LEGAL_COUNTRY')")
    public ResponseEntity<Covenant> updateCovenant(@PathVariable Long id, @RequestBody Covenant covenant) {
        Optional<Covenant> existingCovenant = covenantService.getCovenantById(id);
        if (existingCovenant.isPresent()) {
            covenant.setId(id);
            Covenant updatedCovenant = covenantService.saveCovenant(covenant);
            return new ResponseEntity<>(updatedCovenant, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCovenant(@PathVariable Long id) {
        Optional<Covenant> existingCovenant = covenantService.getCovenantById(id);
        if (existingCovenant.isPresent()) {
            covenantService.deleteCovenant(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
