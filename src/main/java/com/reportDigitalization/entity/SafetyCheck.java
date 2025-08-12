package com.reportDigitalization.entity;

import com.factory.safety.enums.CheckStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "safety_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String checkTitle;

    @Column(nullable = false)
    private String factoryName;

    @Column(nullable = false)
    private String factoryLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckStatus status = CheckStatus.PENDING;

    @Column(nullable = false)
    private Double totalScore = 0.0;

    @Column(nullable = false)
    private Double maxScore = 0.0;

    @Column(nullable = false)
    private Double percentageScore = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "safetyCheck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CheckSheet> checkSheets;

    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;

    @Column(length = 1000)
    private String comments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}