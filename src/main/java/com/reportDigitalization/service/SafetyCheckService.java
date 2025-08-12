package com.reportDigitalization.service;

import com.factory.safety.entity.*;
import com.factory.safety.enums.CheckStatus;
import com.factory.safety.repository.*;
import com.factory.safety.dto.SafetyCheckDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SafetyCheckService {

    @Autowired
    private SafetyCheckRepository safetyCheckRepository;

    @Autowired
    private CheckSheetRepository checkSheetRepository;

    @Autowired
    private CheckItemRepository checkItemRepository;

    public List<SafetyCheck> findByUser(User user) {
        return safetyCheckRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Optional<SafetyCheck> findById(Long id) {
        return safetyCheckRepository.findById(id);
    }

    @Transactional
    public SafetyCheck createSafetyCheck(SafetyCheck safetyCheck) {
        safetyCheck = safetyCheckRepository.save(safetyCheck);

        // Create default check sheets
        createDefaultCheckSheets(safetyCheck);

        return safetyCheck;
    }

    private void createDefaultCheckSheets(SafetyCheck safetyCheck) {
        // Safety Management System
        CheckSheet smsSheet = new CheckSheet();
        smsSheet.setCategory("Safety Management");
        smsSheet.setTitle("Safety Management System");
        smsSheet.setDescription("Evaluation of safety policies and procedures");
        smsSheet.setSafetyCheck(safetyCheck);
        smsSheet.setMaxScore(100.0);
        smsSheet = checkSheetRepository.save(smsSheet);
        createSMSCheckItems(smsSheet);

        // Personal Protective Equipment
        CheckSheet ppeSheet = new CheckSheet();
        ppeSheet.setCategory("Personal Safety");
        ppeSheet.setTitle("Personal Protective Equipment");
        ppeSheet.setDescription("Assessment of PPE usage and availability");
        ppeSheet.setSafetyCheck(safetyCheck);
        ppeSheet.setMaxScore(100.0);
        ppeSheet = checkSheetRepository.save(ppeSheet);
        createPPECheckItems(ppeSheet);

        // Workplace Safety
        CheckSheet workplaceSheet = new CheckSheet();
        workplaceSheet.setCategory("Workplace Safety");
        workplaceSheet.setTitle("Workplace Environment");
        workplaceSheet.setDescription("Assessment of workplace safety conditions");
        workplaceSheet.setSafetyCheck(safetyCheck);
        workplaceSheet.setMaxScore(100.0);
        workplaceSheet = checkSheetRepository.save(workplaceSheet);
        createWorkplaceCheckItems(workplaceSheet);
    }

    private void createSMSCheckItems(CheckSheet checkSheet) {
        String[] questions = {
                "Is there a documented safety policy in place?",
                "Are safety procedures regularly reviewed and updated?",
                "Is there a safety committee with regular meetings?",
                "Are incident reporting procedures clearly defined?",
                "Is safety training provided to all employees?"
        };

        for (String question : questions) {
            CheckItem item = new CheckItem();
            item.setQuestion(question);
            item.setMaxPoints(20.0);
            item.setCheckSheet(checkSheet);
            checkItemRepository.save(item);
        }
    }

    private void createPPECheckItems(CheckSheet checkSheet) {
        String[] questions = {
                "Are appropriate PPE items available for all workers?",
                "Is PPE properly maintained and inspected regularly?",
                "Are workers trained on proper PPE usage?",
                "Is PPE usage monitored and enforced?",
                "Are PPE replacement procedures in place?"
        };

        for (String question : questions) {
            CheckItem item = new CheckItem();
            item.setQuestion(question);
            item.setMaxPoints(20.0);
            item.setCheckSheet(checkSheet);
            checkItemRepository.save(item);
        }
    }

    private void createWorkplaceCheckItems(CheckSheet checkSheet) {
        String[] questions = {
                "Are emergency exits clearly marked and unobstructed?",
                "Is adequate lighting provided in all work areas?",
                "Are fire safety equipment properly maintained?",
                "Are hazardous materials properly stored and labeled?",
                "Is workplace housekeeping maintained to safety standards?"
        };

        for (String question : questions) {
            CheckItem item = new CheckItem();
            item.setQuestion(question);
            item.setMaxPoints(20.0);
            item.setCheckSheet(checkSheet);
            checkItemRepository.save(item);
        }
    }

    @Transactional
    public SafetyCheck updateCheckItem(Long checkItemId, Double points, String remarks) {
        Optional<CheckItem> itemOpt = checkItemRepository.findById(checkItemId);
        if (itemOpt.isPresent()) {
            CheckItem item = itemOpt.get();
            item.setScoredPoints(Math.min(points, item.getMaxPoints()));
            item.setRemarks(remarks);
            item.setCompleted(true);
            checkItemRepository.save(item);

            // Update check sheet score
            updateCheckSheetScore(item.getCheckSheet());

            return item.getCheckSheet().getSafetyCheck();
        }
        return null;
    }

    private void updateCheckSheetScore(CheckSheet checkSheet) {
        List<CheckItem> items = checkItemRepository.findByCheckSheet(checkSheet);
        double totalScore = items.stream().mapToDouble(CheckItem::getScoredPoints).sum();
        checkSheet.setScore(totalScore);
        checkSheetRepository.save(checkSheet);

        // Update safety check total score
        updateSafetyCheckScore(checkSheet.getSafetyCheck());
    }

    private void updateSafetyCheckScore(SafetyCheck safetyCheck) {
        List<CheckSheet> sheets = checkSheetRepository.findBySafetyCheck(safetyCheck);
        double totalScore = sheets.stream().mapToDouble(CheckSheet::getScore).sum();
        double maxScore = sheets.stream().mapToDouble(CheckSheet::getMaxScore).sum();

        safetyCheck.setTotalScore(totalScore);
        safetyCheck.setMaxScore(maxScore);
        safetyCheck.setPercentageScore(maxScore > 0 ? (totalScore / maxScore) * 100 : 0);

        safetyCheckRepository.save(safetyCheck);
    }

    @Transactional
    public SafetyCheck submitSafetyCheck(Long safetyCheckId, String comments) {
        Optional<SafetyCheck> checkOpt = safetyCheckRepository.findById(safetyCheckId);
        if (checkOpt.isPresent()) {
            SafetyCheck safetyCheck = checkOpt.get();
            safetyCheck.setStatus(CheckStatus.COMPLETED);
            safetyCheck.setSubmittedAt(LocalDateTime.now());
            safetyCheck.setComments(comments);
            return safetyCheckRepository.save(safetyCheck);
        }
        return null;
    }

    public Double getAverageScoreByUser(User user) {
        return safetyCheckRepository.findAverageScoreByUser(user);
    }
}
