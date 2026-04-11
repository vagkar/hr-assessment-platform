package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findAllByCompanyId(Long companyId);
    Optional<Assessment> findByIdAndCompanyId(Long id, Long companyId);
    boolean existsByIdAndCompanyId(Long id, Long companyId);
}
