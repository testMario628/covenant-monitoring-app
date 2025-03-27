package com.covenant.monitoring.repository;

import com.covenant.monitoring.model.Contract;
import com.covenant.monitoring.model.ContractDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractDocumentRepository extends JpaRepository<ContractDocument, Long> {
    
    List<ContractDocument> findByContract(Contract contract);
    
    List<ContractDocument> findByDocumentType(String documentType);
    
    List<ContractDocument> findByContractAndDocumentType(Contract contract, String documentType);
}
