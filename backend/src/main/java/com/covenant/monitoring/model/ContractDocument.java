package com.covenant.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
    }
}
