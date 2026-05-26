package com.hrplatform.backend.service;

import com.hrplatform.backend.model.dto.dashboard.DashboardStatsResponse;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AssessmentRepository assessmentRepository;
    private final CandidateSessionRepository candidateSessionRepository;

    public DashboardStatsResponse getDashboardStats(Long companyId) {
        long activeAssessments = assessmentRepository.countByCompanyIdAndIsActiveTrue(companyId);
        long invitesSent = candidateSessionRepository.countByCompanyId(companyId);
        long completed = candidateSessionRepository.countCompletedByCompanyId(companyId);
        BigDecimal avgScore = candidateSessionRepository.avgScoreByCompanyId(companyId);

        BigDecimal completionRate = invitesSent > 0
                ? BigDecimal.valueOf(completed * 100.0 / invitesSent).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new DashboardStatsResponse(activeAssessments, invitesSent, completed, completionRate, avgScore);
    }
}
