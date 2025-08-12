package com.reportDigitalization.repository;

import com.factory.safety.entity.CheckItem;
import com.factory.safety.entity.CheckSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckItemRepository extends JpaRepository<CheckItem, Long> {
    List<CheckItem> findByCheckSheet(CheckSheet checkSheet);
    List<CheckItem> findByCompletedFalse();
}
