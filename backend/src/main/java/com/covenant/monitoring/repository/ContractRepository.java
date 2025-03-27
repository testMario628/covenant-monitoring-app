package com.covenant.monitoring.repository;

import com.covenant.monitoring.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    Optional<Contract> findByContractId(String contractId);
    
    List<Contract> findByCountry(String country);
    
    List<Contract> findByLegalEntity(String legalEntity);
    
    List<Contract> findByCountryAndLegalEntity(String country, String legalEntity);
    
    List<Contract> findByStatus(String status);
}
