package com.covenant.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitoring_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "covenant_id", nullable = false)
    private Covenant covenant;

    @Column(nullable = false)
    private String status;

    @Column(name = "monitoring_date", nullable = false)
    private LocalDateTime monitoringDate;

    @Column
    private String notes;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (monitoringDate == null) {
            monitoringDate = LocalDateTime.now();
        }
    }
}
