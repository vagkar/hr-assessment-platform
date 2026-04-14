package com.hrplatform.backend.controller;

import com.hrplatform.backend.model.dto.assessment.AssessmentRequest;
import com.hrplatform.backend.model.dto.assessment.AssessmentResponse;
import com.hrplatform.backend.model.dto.assessment.InviteRequest;
import com.hrplatform.backend.model.dto.assessment.InviteResponse;
import com.hrplatform.backend.security.UserPrincipal;
import com.hrplatform.backend.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
public class AssessmentController extends BaseController {

    private final AssessmentService assessmentService;

    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> getAll() {
        return ResponseEntity.ok(assessmentService.getAllByCompany(getPrincipal().companyId()));
    }

    @PostMapping
    public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody AssessmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentService.create(request, getPrincipal().companyId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody AssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.update(id, request, getPrincipal().companyId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentService.delete(id, getPrincipal().companyId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<InviteResponse> createInvite(@PathVariable Long id,
                                                       @Valid @RequestBody InviteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentService.createInvite(id, request, getPrincipal().companyId()));
    }

}
