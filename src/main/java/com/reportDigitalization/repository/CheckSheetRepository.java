package com.reportDigitalization.repository;

import com.factory.safety.entity.CheckSheet;
import com.factory.safety.entity.SafetyCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckSheetRepository extends JpaRepository<CheckSheet, Long> {
    List<CheckSheet> findBySafetyCheck(SafetyCheck safetyCheck);
    List<CheckSheet> findByCategory(String category);
}
