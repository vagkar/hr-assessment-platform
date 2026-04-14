package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.assessment.AssessmentRequest;
import com.hrplatform.backend.model.dto.assessment.AssessmentResponse;
import com.hrplatform.backend.model.dto.assessment.InviteRequest;
import com.hrplatform.backend.model.dto.assessment.InviteResponse;
import com.hrplatform.backend.model.entity.Assessment;
import com.hrplatform.backend.model.entity.CandidateSession;
import com.hrplatform.backend.model.entity.Company;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import com.hrplatform.backend.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final CompanyRepository companyRepository;
    private final CandidateSessionRepository candidateSessionRepository;

    public List<AssessmentResponse> getAllByCompany(Long companyId) {
        return assessmentRepository.findAllByCompanyId(companyId)
                .stream()
                .map(this::toResponse)
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

        return toResponse(assessmentRepository.save(assessment));
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

        return toResponse(assessmentRepository.save(assessment));
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

        return new InviteResponse(token, "/api/candidate/session/" + token);
    }

    private AssessmentResponse toResponse(Assessment a) {
        Long companyId = a.getCompanyId() != null ? a.getCompanyId() : a.getCompany().getId();
        return new AssessmentResponse(
                a.getId(),
                companyId,
                a.getTitle(),
                a.getDescription(),
                a.getDurationMinutes(),
                a.getIsActive(),
                a.getCreatedAt()
        );
    }
}
