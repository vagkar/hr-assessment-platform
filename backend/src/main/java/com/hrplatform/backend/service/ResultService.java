package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.result.AnswerDetail;
import com.hrplatform.backend.model.dto.result.SessionResultDetail;
import com.hrplatform.backend.model.dto.result.SessionResultSummary;
import com.hrplatform.backend.model.entity.CandidateSession;
import com.hrplatform.backend.repository.AnswerRepository;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private final CandidateSessionRepository sessionRepository;
    private final AssessmentRepository assessmentRepository;
    private final AnswerRepository answerRepository;

    public List<SessionResultSummary> getResultsForAssessment(Long assessmentId, Long companyId) {
        assessmentRepository.findByIdAndCompanyId(assessmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        return sessionRepository.findAllByAssessmentIdAndCompanyId(assessmentId, companyId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public SessionResultDetail getSessionDetail(Long sessionId, Long companyId) {
        CandidateSession session = sessionRepository.findByIdAndCompanyId(sessionId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        List<AnswerDetail> answers = answerRepository.findAllBySessionId(sessionId)
                .stream()
                .map(a -> new AnswerDetail(
                        a.getQuestion().getId(),
                        a.getQuestion().getText(),
                        a.getQuestion().getType(),
                        a.getSelectedOption() != null ? a.getSelectedOption().getId() : null,
                        a.getSelectedOption() != null ? a.getSelectedOption().getText() : null,
                        a.getOpenTextAnswer(),
                        a.getIsCorrect()
                ))
                .toList();

        return new SessionResultDetail(
                session.getId(),
                session.getCandidateName(),
                session.getCandidateEmail(),
                session.getStatus(),
                session.getScore(),
                session.getStartedAt(),
                session.getCompletedAt(),
                answers
        );
    }

    private SessionResultSummary toSummary(CandidateSession s) {
        return new SessionResultSummary(
                s.getId(),
                s.getCandidateName(),
                s.getCandidateEmail(),
                s.getStatus(),
                s.getScore(),
                s.getStartedAt(),
                s.getCompletedAt()
        );
    }
}
