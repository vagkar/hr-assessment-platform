package com.hrplatform.backend.controller;

import com.hrplatform.backend.model.dto.result.SessionResultDetail;
import com.hrplatform.backend.model.dto.result.SessionResultSummary;
import com.hrplatform.backend.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResultController extends BaseController {

    private final ResultService resultService;

    @GetMapping("/api/assessments/{id}/results")
    public ResponseEntity<List<SessionResultSummary>> getResults(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getResultsForAssessment(id, getPrincipal().companyId()));
    }

    @GetMapping("/api/results/{sessionId}")
    public ResponseEntity<SessionResultDetail> getDetail(@PathVariable Long sessionId) {
        return ResponseEntity.ok(resultService.getSessionDetail(sessionId, getPrincipal().companyId()));
    }
}
