package com.covenant.monitoring.repository;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CovenantRepository extends JpaRepository<Covenant, Long> {
    
    Optional<Covenant> findByCovenantId(String covenantId);
    
    List<Covenant> findByContract(Contract contract);
    
    List<Covenant> findByStatus(String status);
    
    List<Covenant> findByContractAndStatus(Contract contract, String status);
    
    List<Covenant> findByLastMonitoringDateBefore(LocalDateTime date);
    
    List<Covenant> findByContractInAndStatus(List<Contract> contracts, String status);
}
