package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Optional<Contract> getContractById(Long id) {
        return contractRepository.findById(id);
    }

    public Optional<Contract> getContractByContractId(String contractId) {
        return contractRepository.findByContractId(contractId);
    }

    public List<Contract> getContractsByCountry(String country) {
        return contractRepository.findByCountry(country);
    }

    public List<Contract> getContractsByLegalEntity(String legalEntity) {
        return contractRepository.findByLegalEntity(legalEntity);
    }

    public List<Contract> getContractsByCountryAndLegalEntity(String country, String legalEntity) {
        return contractRepository.findByCountryAndLegalEntity(country, legalEntity);
    }

    public List<Contract> getContractsByStatus(String status) {
        return contractRepository.findByStatus(status);
    }

    public Contract saveContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }
}
