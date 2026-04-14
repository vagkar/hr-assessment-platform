package com.hrplatform.backend.controller;

import com.hrplatform.backend.model.dto.question.QuestionRequest;
import com.hrplatform.backend.model.dto.question.QuestionResponse;
import com.hrplatform.backend.security.UserPrincipal;
import com.hrplatform.backend.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    @GetMapping("/api/assessments/{assessmentId}/questions")
    public ResponseEntity<List<QuestionResponse>> getAll(@PathVariable Long assessmentId) {
        return ResponseEntity.ok(questionService.getByAssessment(assessmentId, getPrincipal().companyId()));
    }

    @PostMapping("/api/assessments/{assessmentId}/questions")
    public ResponseEntity<QuestionResponse> create(@PathVariable Long assessmentId,
                                                   @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.create(assessmentId, request, getPrincipal().companyId()));
    }

    @PutMapping("/api/questions/{id}")
    public ResponseEntity<QuestionResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(questionService.update(id, request, getPrincipal().companyId()));
    }

    @DeleteMapping("/api/questions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        questionService.delete(id, getPrincipal().companyId());
        return ResponseEntity.noContent().build();
    }

}
