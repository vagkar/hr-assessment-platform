package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.analytics.AssessmentAnalyticsResponse;
import com.hrplatform.backend.model.dto.dashboard.DashboardStatsResponse;
import com.hrplatform.backend.model.entity.Assessment;
import com.hrplatform.backend.model.entity.CandidateSession;
import com.hrplatform.backend.repository.AnswerRepository;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AssessmentRepository assessmentRepository;
    private final CandidateSessionRepository candidateSessionRepository;
    private final AnswerRepository answerRepository;

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

    public AssessmentAnalyticsResponse getAssessmentAnalytics(Long assessmentId, Long companyId) {
        Assessment assessment = assessmentRepository.findByIdAndCompanyId(assessmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        long totalInvites = candidateSessionRepository.countByAssessmentId(assessmentId);

        List<BigDecimal> scores = candidateSessionRepository
                .findAllCompletedScoresByAssessmentIdAndCompanyId(assessmentId, companyId);
        long completedCount = scores.size();

        BigDecimal avgScore = null, medianScore = null, highScore = null, lowScore = null;
        if (!scores.isEmpty()) {
            avgScore = scores.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);

            int n = scores.size();
            medianScore = n % 2 == 0
                    ? scores.get(n / 2 - 1).add(scores.get(n / 2))
                            .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP)
                    : scores.get(n / 2).setScale(1, RoundingMode.HALF_UP);

            highScore = scores.get(scores.size() - 1).setScale(1, RoundingMode.HALF_UP);
            lowScore = scores.get(0).setScale(1, RoundingMode.HALF_UP);
        }

        BigDecimal b25 = BigDecimal.valueOf(25), b50 = BigDecimal.valueOf(50), b75 = BigDecimal.valueOf(75);
        long d0 = scores.stream().filter(s -> s.compareTo(b25) < 0).count();
        long d1 = scores.stream().filter(s -> s.compareTo(b25) >= 0 && s.compareTo(b50) < 0).count();
        long d2 = scores.stream().filter(s -> s.compareTo(b50) >= 0 && s.compareTo(b75) < 0).count();
        long d3 = scores.stream().filter(s -> s.compareTo(b75) >= 0).count();
        List<Long> scoreDistribution = List.of(d0, d1, d2, d3);

        List<CandidateSession> completedSessions = candidateSessionRepository
                .findCompletedSessionsWithTimeByAssessmentIdAndCompanyId(assessmentId, companyId);
        Integer avgCompletionMinutes = null;
        if (!completedSessions.isEmpty()) {
            long totalMinutes = completedSessions.stream()
                    .mapToLong(s -> Duration.between(s.getStartedAt(), s.getCompletedAt()).toMinutes())
                    .sum();
            avgCompletionMinutes = (int) (totalMinutes / completedSessions.size());
        }

        List<Object[]> rawStats = answerRepository
                .findQuestionDifficultyByAssessmentIdAndCompanyId(assessmentId, companyId);
        List<AssessmentAnalyticsResponse.QuestionDifficulty> perQuestionStats = rawStats.stream()
                .map(row -> {
                    Long questionId = (Long) row[0];
                    String questionText = (String) row[1];
                    long total = ((Number) row[3]).longValue();
                    long correct = ((Number) row[4]).longValue();
                    BigDecimal correctRate = total > 0
                            ? BigDecimal.valueOf(correct * 100.0 / total).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new AssessmentAnalyticsResponse.QuestionDifficulty(questionId, questionText, correctRate);
                })
                .toList();

        return new AssessmentAnalyticsResponse(
                completedCount, totalInvites, avgScore, medianScore, highScore, lowScore,
                avgCompletionMinutes,
                assessment.getDurationMinutes() != null ? assessment.getDurationMinutes() : 0,
                scoreDistribution, perQuestionStats
        );
    }
}
