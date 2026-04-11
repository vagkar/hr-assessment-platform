package com.hrplatform.backend.repository;

import com.hrplatform.backend.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findAllBySessionId(Long sessionId);
    void deleteAllBySessionId(Long sessionId);
}
