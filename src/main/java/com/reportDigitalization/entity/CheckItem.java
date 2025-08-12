package com.reportDigitalization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "check_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false)
    private Double maxPoints;

    @Column(nullable = false)
    private Double scoredPoints = 0.0;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_sheet_id", nullable = false)
    private CheckSheet checkSheet;
}
