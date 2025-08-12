package com.reportDigitalization.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafetyCheckDto {
    private Long id;
    private String checkTitle;
    private String factoryName;
    private String factoryLocation;
    private String status;
    private Double totalScore;
    private Double maxScore;
    private Double percentageScore;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private String comments;
    private List<CheckSheetDto> checkSheets;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckSheetDto {
        private Long id;
        private String category;
        private String title;
        private String description;
        private Double score;
        private Double maxScore;
        private List<CheckItemDto> checkItems;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckItemDto {
        private Long id;
        private String question;
        private Double maxPoints;
        private Double scoredPoints;
        private String remarks;
        private boolean completed;
    }
}
