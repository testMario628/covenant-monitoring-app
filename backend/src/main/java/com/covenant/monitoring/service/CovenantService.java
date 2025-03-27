package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.repository.CovenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CovenantService {

    private final CovenantRepository covenantRepository;

    @Autowired
    public CovenantService(CovenantRepository covenantRepository) {
        this.covenantRepository = covenantRepository;
    }

    public List<Covenant> getAllCovenants() {
        return covenantRepository.findAll();
    }

    public Optional<Covenant> getCovenantById(Long id) {
        return covenantRepository.findById(id);
    }

    public Optional<Covenant> getCovenantByCovenantId(String covenantId) {
        return covenantRepository.findByCovenantId(covenantId);
    }

    public List<Covenant> getCovenantsByContract(Contract contract) {
        return covenantRepository.findByContract(contract);
    }

    public List<Covenant> getCovenantsByStatus(String status) {
        return covenantRepository.findByStatus(status);
    }

    public List<Covenant> getCovenantsByContractAndStatus(Contract contract, String status) {
        return covenantRepository.findByContractAndStatus(contract, status);
    }

    public List<Covenant> getCovenantsDueForMonitoring(LocalDateTime date) {
        return covenantRepository.findByLastMonitoringDateBefore(date);
    }

    public List<Covenant> getCovenantsByContractsAndStatus(List<Contract> contracts, String status) {
        return covenantRepository.findByContractInAndStatus(contracts, status);
    }

    public Covenant saveCovenant(Covenant covenant) {
        return covenantRepository.save(covenant);
    }

    public void deleteCovenant(Long id) {
        covenantRepository.deleteById(id);
    }
}
