package com.hrplatform.backend.controller;

import com.hrplatform.backend.model.dto.candidate.CandidateSessionView;
import com.hrplatform.backend.model.dto.candidate.SubmitRequest;
import com.hrplatform.backend.model.dto.candidate.SubmitResponse;
import com.hrplatform.backend.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/session/{token}")
    public ResponseEntity<CandidateSessionView> getSession(@PathVariable String token) {
        return ResponseEntity.ok(candidateService.loadSession(token));
    }

    @PostMapping("/session/{token}/start")
    public ResponseEntity<CandidateSessionView> startSession(@PathVariable String token) {
        return ResponseEntity.ok(candidateService.startSession(token));
    }

    @PostMapping("/session/{token}/submit")
    public ResponseEntity<SubmitResponse> submit(@PathVariable String token,
                                                 @Valid @RequestBody SubmitRequest request) {
        return ResponseEntity.ok(candidateService.submitAnswers(token, request));
    }
}
