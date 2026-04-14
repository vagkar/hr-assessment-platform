package com.hrplatform.backend.service;

import com.hrplatform.backend.exception.ResourceNotFoundException;
import com.hrplatform.backend.model.dto.question.QuestionOptionResponse;
import com.hrplatform.backend.model.dto.question.QuestionRequest;
import com.hrplatform.backend.model.dto.question.QuestionResponse;
import com.hrplatform.backend.model.entity.Question;
import com.hrplatform.backend.model.entity.QuestionOption;
import com.hrplatform.backend.repository.AssessmentRepository;
import com.hrplatform.backend.repository.QuestionOptionRepository;
import com.hrplatform.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final AssessmentRepository assessmentRepository;

    public List<QuestionResponse> getByAssessment(Long assessmentId, Long companyId) {
        assessmentRepository.findByIdAndCompanyId(assessmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        return questionRepository.findAllByAssessmentIdOrderByOrderIndexAsc(assessmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public QuestionResponse create(Long assessmentId, QuestionRequest request, Long companyId) {
        var assessment = assessmentRepository.findByIdAndCompanyId(assessmentId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        Question question = new Question();
        question.setAssessment(assessment);
        question.setText(request.text());
        question.setType(request.type());
        question.setOrderIndex(request.orderIndex());

        if (request.options() != null) {
            request.options().forEach(optReq -> {
                QuestionOption option = new QuestionOption();
                option.setQuestion(question);
                option.setText(optReq.text());
                option.setIsCorrect(optReq.isCorrect());
                question.getOptions().add(option);
            });
        }

        return toResponse(questionRepository.save(question));
    }

    public QuestionResponse update(Long questionId, QuestionRequest request, Long companyId) {
        Question question = questionRepository.findByIdAndAssessmentCompanyId(questionId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        question.setText(request.text());
        question.setType(request.type());
        question.setOrderIndex(request.orderIndex());

        question.getOptions().clear();
        if (request.options() != null) {
            request.options().forEach(optReq -> {
                QuestionOption option = new QuestionOption();
                option.setQuestion(question);
                option.setText(optReq.text());
                option.setIsCorrect(optReq.isCorrect());
                question.getOptions().add(option);
            });
        }

        return toResponse(questionRepository.save(question));
    }

    public void delete(Long questionId, Long companyId) {
        Question question = questionRepository.findByIdAndAssessmentCompanyId(questionId, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        questionRepository.delete(question);
    }

    private QuestionResponse toResponse(Question q) {
        List<QuestionOptionResponse> options = q.getOptions().stream()
                .map(o -> new QuestionOptionResponse(o.getId(), o.getText(), o.getIsCorrect()))
                .toList();

        return new QuestionResponse(
                q.getId(),
                q.getAssessment().getId(),
                q.getText(),
                q.getType(),
                q.getOrderIndex(),
                options
        );
    }
}
