package com.hrplatform.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"session", "question", "selectedOption"})
@EqualsAndHashCode(exclude = {"session", "question", "selectedOption"})
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CandidateSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @Column(name = "open_text_answer", columnDefinition = "TEXT")
    private String openTextAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;
}
