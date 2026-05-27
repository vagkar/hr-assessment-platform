package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.assessment.AssessmentRequest;
import com.hrplatform.backend.model.dto.assessment.AssessmentResponse;
import com.hrplatform.backend.model.dto.assessment.AssessmentStatsRow;
import com.hrplatform.backend.model.dto.assessment.InviteRequest;
import com.hrplatform.backend.model.dto.assessment.InviteResponse;
import com.hrplatform.backend.model.entity.Assessment;
import com.hrplatform.backend.model.entity.CandidateSession;
import com.hrplatform.backend.model.entity.Company;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import com.hrplatform.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CompanyRepository companyRepository;
    private final CandidateSessionRepository candidateSessionRepository;
    private final EmailService emailService;

    public List<AssessmentResponse> getAllByCompany(Long companyId) {
        List<Assessment> assessments = assessmentRepository.findAllByCompanyId(companyId);

        Map<Long, AssessmentStatsRow> statsByAssessment = candidateSessionRepository
                .findStatsByCompanyId(companyId)
                .stream()
                .collect(Collectors.toMap(AssessmentStatsRow::assessmentId, Function.identity()));

        Map<Long, List<BigDecimal>> recentScoresByAssessment = new HashMap<>();
        for (Object[] row : candidateSessionRepository.findRecentScoresByCompanyId(companyId)) {
            Long assessmentId = ((Number) row[0]).longValue();
            BigDecimal score = (BigDecimal) row[1];
            recentScoresByAssessment.computeIfAbsent(assessmentId, k -> new ArrayList<>()).add(score);
        }

        return assessments.stream()
                .map(a -> toResponse(a, statsByAssessment.get(a.getId()),
                        recentScoresByAssessment.getOrDefault(a.getId(), List.of())))
                .toList();
    }

    public AssessmentResponse create(AssessmentRequest request, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Assessment assessment = new Assessment();
        assessment.setCompany(company);
        assessment.setTitle(request.title());
        assessment.setDescription(request.description());
        assessment.setDurationMinutes(request.durationMinutes());
        assessment.setIsActive(request.isActive() != null ? request.isActive() : true);

        return toResponseSingle(assessmentRepository.save(assessment));
    }

    public AssessmentResponse update(Long id, AssessmentRequest request, Long companyId) {
        Assessment assessment = assessmentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        assessment.setTitle(request.title());
        assessment.setDescription(request.description());
        assessment.setDurationMinutes(request.durationMinutes());
        if (request.isActive() != null) {
            assessment.setIsActive(request.isActive());
        }

        return toResponseSingle(assessmentRepository.save(assessment));
    }

    public void delete(Long id, Long companyId) {
        Assessment assessment = assessmentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        assessmentRepository.delete(assessment);
    }

    public InviteResponse createInvite(Long assessmentId, InviteRequest request, Long companyId) {
        Assessment assessment = assessmentRepository.findByIdAndCompanyId(assessmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        String token = UUID.randomUUID().toString();

        CandidateSession session = new CandidateSession();
        session.setAssessment(assessment);
        session.setCandidateName(request.candidateName());
        session.setCandidateEmail(request.candidateEmail());
        session.setInviteToken(token);

        candidateSessionRepository.save(session);

        emailService.sendInviteEmail(
                request.candidateEmail(),
                request.candidateName(),
                assessment.getTitle(),
                token
        );

        return new InviteResponse(token, "/api/candidate/session/" + token);
    }

    private AssessmentResponse toResponse(Assessment a, AssessmentStatsRow stats, List<BigDecimal> recentScores) {
        Long companyId = a.getCompanyId() != null ? a.getCompanyId() : a.getCompany().getId();
        long inviteCount = stats != null && stats.inviteCount() != null ? stats.inviteCount() : 0L;
        long completedCount = stats != null && stats.completedCount() != null ? stats.completedCount() : 0L;
        BigDecimal avgScore = stats != null && stats.avgScore() != null
                ? BigDecimal.valueOf(stats.avgScore()).setScale(2, RoundingMode.HALF_UP)
                : null;
        return new AssessmentResponse(
                a.getId(), companyId, a.getTitle(), a.getDescription(),
                a.getDurationMinutes(), a.getIsActive(), a.getCreatedAt(),
                inviteCount, completedCount, avgScore, recentScores
        );
    }

    private AssessmentResponse toResponseSingle(Assessment a) {
        long inviteCount = candidateSessionRepository.countByAssessmentId(a.getId());
        long completedCount = candidateSessionRepository.countCompletedByAssessmentId(a.getId());
        BigDecimal avgScore = candidateSessionRepository.avgScoreByAssessmentId(a.getId());
        List<BigDecimal> recentScores = candidateSessionRepository
                .findRecentScoresByAssessmentId(a.getId(), PageRequest.of(0, 6));
        AssessmentStatsRow stats = new AssessmentStatsRow(
                a.getId(), inviteCount, completedCount,
                avgScore != null ? avgScore.doubleValue() : null);
        return toResponse(a, stats, recentScores);
    }
}
