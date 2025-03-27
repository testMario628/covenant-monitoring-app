package com.covenant.monitoring.repository;

import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {
    
    List<MonitoringResult> findByCovenant(Covenant covenant);
    
    List<MonitoringResult> findByStatus(String status);
    
    List<MonitoringResult> findByCreatedBy(User user);
    
    List<MonitoringResult> findByMonitoringDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MonitoringResult> findByCovenant_ContractId(Long contractId);
    
    List<MonitoringResult> findTop1ByCovenant_IdOrderByMonitoringDateDesc(Long covenantId);
}
