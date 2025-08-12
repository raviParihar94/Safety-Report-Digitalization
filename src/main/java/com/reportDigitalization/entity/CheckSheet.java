package com.reportDigitalization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "check_sheets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double score = 0.0;

    @Column(nullable = false)
    private Double maxScore = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "safety_check_id", nullable = false)
    private SafetyCheck safetyCheck;

    @OneToMany(mappedBy = "checkSheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CheckItem> checkItems;
}