package com.covenant.monitoring.service;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.repository.MonitoringResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoringResultService {

    private final MonitoringResultRepository monitoringResultRepository;

    @Autowired
    public MonitoringResultService(MonitoringResultRepository monitoringResultRepository) {
        this.monitoringResultRepository = monitoringResultRepository;
    }

    public List<MonitoringResult> getAllMonitoringResults() {
        return monitoringResultRepository.findAll();
    }

    public Optional<MonitoringResult> getMonitoringResultById(Long id) {
        return monitoringResultRepository.findById(id);
    }

    public List<MonitoringResult> getMonitoringResultsByCovenant(Covenant covenant) {
        return monitoringResultRepository.findByCovenant(covenant);
    }

    public List<MonitoringResult> getMonitoringResultsByStatus(String status) {
        return monitoringResultRepository.findByStatus(status);
    }

    public List<MonitoringResult> getMonitoringResultsByCreatedBy(User user) {
        return monitoringResultRepository.findByCreatedBy(user);
    }

    public List<MonitoringResult> getMonitoringResultsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return monitoringResultRepository.findByMonitoringDateBetween(startDate, endDate);
    }

    public List<MonitoringResult> getMonitoringResultsByContractId(Long contractId) {
        return monitoringResultRepository.findByCovenant_ContractId(contractId);
    }

    public List<MonitoringResult> getLatestMonitoringResultForCovenant(Long covenantId) {
        return monitoringResultRepository.findTop1ByCovenant_IdOrderByMonitoringDateDesc(covenantId);
    }

    public MonitoringResult saveMonitoringResult(MonitoringResult monitoringResult) {
        return monitoringResultRepository.save(monitoringResult);
    }

    public void deleteMonitoringResult(Long id) {
        monitoringResultRepository.deleteById(id);
    }
}
