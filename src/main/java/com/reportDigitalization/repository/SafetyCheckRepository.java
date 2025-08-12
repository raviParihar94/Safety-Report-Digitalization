package com.reportDigitalization.repository;

import com.factory.safety.entity.SafetyCheck;
import com.factory.safety.entity.User;
import com.factory.safety.enums.CheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SafetyCheckRepository extends JpaRepository<SafetyCheck, Long> {
    List<SafetyCheck> findByUserOrderByCreatedAtDesc(User user);
    List<SafetyCheck> findByStatus(CheckStatus status);

    @Query("SELECT sc FROM SafetyCheck sc WHERE sc.user = ?1 ORDER BY sc.createdAt DESC")
    List<SafetyCheck> findLatestByUser(User user);

    @Query("SELECT sc FROM SafetyCheck sc WHERE sc.user = ?1 AND sc.createdAt >= ?2")
    List<SafetyCheck> findByUserAndCreatedAtAfter(User user, LocalDateTime date);

    @Query("SELECT AVG(sc.percentageScore) FROM SafetyCheck sc WHERE sc.user = ?1 AND sc.status = 'COMPLETED'")
    Double findAverageScoreByUser(User user);
}
