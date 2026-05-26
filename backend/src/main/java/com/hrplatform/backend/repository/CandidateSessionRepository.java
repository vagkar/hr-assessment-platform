package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.CandidateSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("SELECT COUNT(cs) FROM CandidateSession cs JOIN cs.assessment a WHERE a.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(cs) FROM CandidateSession cs JOIN cs.assessment a WHERE a.company.id = :companyId AND cs.status = 'COMPLETED'")
    long countCompletedByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT AVG(cs.score) FROM CandidateSession cs JOIN cs.assessment a WHERE a.company.id = :companyId AND cs.status = 'COMPLETED' AND cs.score IS NOT NULL")
    BigDecimal avgScoreByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(cs) FROM CandidateSession cs WHERE cs.assessment.id = :assessmentId")
    long countByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("SELECT COUNT(cs) FROM CandidateSession cs WHERE cs.assessment.id = :assessmentId AND cs.status = 'COMPLETED'")
    long countCompletedByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("SELECT AVG(cs.score) FROM CandidateSession cs WHERE cs.assessment.id = :assessmentId AND cs.status = 'COMPLETED' AND cs.score IS NOT NULL")
    BigDecimal avgScoreByAssessmentId(@Param("assessmentId") Long assessmentId);

    @Query("""
        SELECT cs.score FROM CandidateSession cs
        WHERE cs.assessment.id = :assessmentId AND cs.status = 'COMPLETED' AND cs.score IS NOT NULL
        ORDER BY cs.completedAt DESC
    """)
    List<BigDecimal> findRecentScoresByAssessmentId(@Param("assessmentId") Long assessmentId,
                                                     org.springframework.data.domain.Pageable pageable);
}
