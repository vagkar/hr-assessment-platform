package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.CandidateSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidateSessionRepository extends JpaRepository<CandidateSession, Long> {
    Optional<CandidateSession> findByInviteToken(String inviteToken);
    List<CandidateSession> findAllByAssessmentId(Long assessmentId);

    @Query("""
        SELECT cs FROM CandidateSession cs
        JOIN cs.assessment a
        WHERE a.id = :assessmentId AND a.company.id = :companyId
    """)
    List<CandidateSession> findAllByAssessmentIdAndCompanyId(
            @Param("assessmentId") Long assessmentId,
            @Param("companyId") Long companyId
    );

    @Query("""
        SELECT cs FROM CandidateSession cs
        JOIN cs.assessment a
        WHERE cs.id = :sessionId AND a.company.id = :companyId
    """)
    Optional<CandidateSession> findByIdAndCompanyId(
            @Param("sessionId") Long sessionId,
            @Param("companyId") Long companyId
    );
}
