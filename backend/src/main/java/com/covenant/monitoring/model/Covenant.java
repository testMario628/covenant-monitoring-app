package com.covenant.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "covenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Covenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "covenant_id", nullable = false)
    private String covenantId;

    @Column(nullable = false)
    private String title;

    @Column(name = "has_cure_period")
    private Boolean hasCurePeriod;

    @Column(name = "contract_article")
    private String contractArticle;

    @Column(nullable = false)
    private String status;

    @Column(name = "last_monitoring_date")
    private LocalDateTime lastMonitoringDate;

    @Column(name = "future_risks")
    private String futureRisks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
