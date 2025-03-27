package com.covenant.monitoring.controller;

import com.covenant.monitoring.model.Covenant;
import com.covenant.monitoring.model.MonitoringResult;
import com.covenant.monitoring.model.User;
import com.covenant.monitoring.service.CovenantService;
import com.covenant.monitoring.service.MonitoringResultService;
import com.covenant.monitoring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/monitoring-results")
public class MonitoringResultController {

    private final MonitoringResultService monitoringResultService;
    private final CovenantService covenantService;
    private final UserService userService;

    @Autowired
    public MonitoringResultController(MonitoringResultService monitoringResultService, 
                                     CovenantService covenantService,
                                     UserService userService) {
        this.monitoringResultService = monitoringResultService;
        this.covenantService = covenantService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<List<MonitoringResult>> getAllMonitoringResults() {
        List<MonitoringResult> results = monitoringResultService.getAllMonitoringResults();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<MonitoringResult> getMonitoringResultById(@PathVariable Long id) {
        Optional<MonitoringResult> result = monitoringResultService.getMonitoringResultById(id);
        return result.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/covenant/{covenantId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<List<MonitoringResult>> getMonitoringResultsByCovenant(@PathVariable Long covenantId) {
        Optional<Covenant> covenant = covenantService.getCovenantById(covenantId);
        if (covenant.isPresent()) {
            List<MonitoringResult> results = monitoringResultService.getMonitoringResultsByCovenant(covenant.get());
            return new ResponseEntity<>(results, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<List<MonitoringResult>> getMonitoringResultsByStatus(@PathVariable String status) {
        List<MonitoringResult> results = monitoringResultService.getMonitoringResultsByStatus(status);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<List<MonitoringResult>> getMonitoringResultsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<MonitoringResult> results = monitoringResultService.getMonitoringResultsByDateRange(start, end);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/latest/{covenantId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY', 'ROLE_ADMIN_HOLDING', 'ROLE_ADMIN_COUNTRY')")
    public ResponseEntity<List<MonitoringResult>> getLatestMonitoringResultForCovenant(@PathVariable Long covenantId) {
        List<MonitoringResult> result = monitoringResultService.getLatestMonitoringResultForCovenant(covenantId);
        if (!result.isEmpty()) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY')")
    public ResponseEntity<MonitoringResult> createMonitoringResult(@RequestBody MonitoringResult monitoringResult) {
        // Set the current user as the creator
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.getUserByUsername(authentication.getName());
        
        if (user.isPresent()) {
            monitoringResult.setCreatedBy(user.get());
            
            // Update the covenant's last monitoring date
            Optional<Covenant> covenant = covenantService.getCovenantById(monitoringResult.getCovenant().getId());
            if (covenant.isPresent()) {
                Covenant c = covenant.get();
                c.setLastMonitoringDate(LocalDateTime.now());
                c.setStatus(monitoringResult.getStatus());
                covenantService.saveCovenant(c);
            }
            
            MonitoringResult savedResult = monitoringResultService.saveMonitoringResult(monitoringResult);
            return new ResponseEntity<>(savedResult, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_FINANCE_HOLDING', 'ROLE_FINANCE_COUNTRY')")
    public ResponseEntity<MonitoringResult> updateMonitoringResult(@PathVariable Long id, @RequestBody MonitoringResult monitoringResult) {
        Optional<MonitoringResult> existingResult = monitoringResultService.getMonitoringResultById(id);
        if (existingResult.isPresent()) {
            monitoringResult.setId(id);
            MonitoringResult updatedResult = monitoringResultService.saveMonitoringResult(monitoringResult);
            return new ResponseEntity<>(updatedResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMonitoringResult(@PathVariable Long id) {
        Optional<MonitoringResult> existingResult = monitoringResultService.getMonitoringResultById(id);
        if (existingResult.isPresent()) {
            monitoringResultService.deleteMonitoringResult(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
