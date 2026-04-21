package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.BadRequestException;
import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.candidate.*;
import com.hrplatform.backend.model.entity.*;
import com.hrplatform.backend.repository.AnswerRepository;
import com.hrplatform.backend.repository.CandidateSessionRepository;
import com.hrplatform.backend.repository.QuestionOptionRepository;
import com.hrplatform.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateService {

    private final CandidateSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final AnswerRepository answerRepository;

    public CandidateSessionView loadSession(String token) {
        CandidateSession session = findSessionByToken(token);
        Assessment assessment = session.getAssessment();

        List<CandidateQuestionView> questions = questionRepository
                .findAllByAssessmentIdOrderByOrderIndexAsc(assessment.getId())
                .stream()
                .map(this::toQuestionView)
                .toList();

        return new CandidateSessionView(
                session.getId(),
                assessment.getTitle(),
                assessment.getDurationMinutes(),
                session.getStatus(),
                session.getStartedAt(),
                questions
        );
    }

    public CandidateSessionView startSession(String token) {
        CandidateSession session = findSessionByToken(token);

        if (session.getStatus() != SessionStatus.PENDING) {
            throw new BadRequestException("Session is already " + session.getStatus());
        }

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return loadSession(token);
    }

    public SubmitResponse submitAnswers(String token, SubmitRequest request) {
        CandidateSession session = findSessionByToken(token);

        if (session.getStatus() != SessionStatus.IN_PROGRESS) {
            throw new BadRequestException("Session is not in progress");
        }

        LocalDateTime deadline = session.getStartedAt()
                .plusMinutes(session.getAssessment().getDurationMinutes())
                .plusSeconds(30);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new BadRequestException("Time has expired");
        }

        Long assessmentId = session.getAssessment().getId();
        int correct = 0;
        int scoreable = 0;

        for (AnswerSubmission submission : request.answers()) {
            Question question = questionRepository.findByIdAndAssessmentId(submission.questionId(), assessmentId)
                    .orElseThrow(() -> new BadRequestException("Question does not belong to this assessment"));

            Answer answer = new Answer();
            answer.setSession(session);
            answer.setQuestion(question);

            if (question.getType() == QuestionType.OPEN_TEXT) {
                answer.setOpenTextAnswer(submission.openTextAnswer());
                answer.setIsCorrect(null);
            } else {
                scoreable++;
                if (submission.selectedOptionId() != null) {
                    QuestionOption option = optionRepository.findById(submission.selectedOptionId())
                            .orElseThrow(() -> new BadRequestException("Option not found"));
                    answer.setSelectedOption(option);
                    answer.setIsCorrect(option.getIsCorrect());
                    if (option.getIsCorrect()) correct++;
                } else {
                    answer.setIsCorrect(false);
                }
            }

            answerRepository.save(answer);
        }

        BigDecimal score = scoreable > 0
                ? BigDecimal.valueOf(correct * 100.0 / scoreable).setScale(2, RoundingMode.HALF_UP)
                : null;

        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());
        session.setScore(score);
        sessionRepository.save(session);

        return new SubmitResponse(session.getId(), session.getStatus(), score, session.getCompletedAt());
    }

    private CandidateSession findSessionByToken(String token) {
        return sessionRepository.findByInviteToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    private CandidateQuestionView toQuestionView(Question q) {
        List<CandidateOptionView> options = q.getOptions().stream()
                .map(o -> new CandidateOptionView(o.getId(), o.getText()))
                .toList();

        return new CandidateQuestionView(q.getId(), q.getText(), q.getType(), q.getOrderIndex(), options);
    }
}
