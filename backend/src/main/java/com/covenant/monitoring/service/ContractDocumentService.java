package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.model.ContractDocument;
import com.covenant.monitoring.repository.ContractDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractDocumentService {

    private final ContractDocumentRepository contractDocumentRepository;

    @Autowired
    public ContractDocumentService(ContractDocumentRepository contractDocumentRepository) {
        this.contractDocumentRepository = contractDocumentRepository;
    }

    public List<ContractDocument> getAllContractDocuments() {
        return contractDocumentRepository.findAll();
    }

    public Optional<ContractDocument> getContractDocumentById(Long id) {
        return contractDocumentRepository.findById(id);
    }

    public List<ContractDocument> getContractDocumentsByContract(Contract contract) {
        return contractDocumentRepository.findByContract(contract);
    }

    public List<ContractDocument> getContractDocumentsByDocumentType(String documentType) {
        return contractDocumentRepository.findByDocumentType(documentType);
    }

    public List<ContractDocument> getContractDocumentsByContractAndDocumentType(Contract contract, String documentType) {
        return contractDocumentRepository.findByContractAndDocumentType(contract, documentType);
    }

    public ContractDocument saveContractDocument(ContractDocument contractDocument) {
        return contractDocumentRepository.save(contractDocument);
    }

    public void deleteContractDocument(Long id) {
        contractDocumentRepository.deleteById(id);
    }
}
