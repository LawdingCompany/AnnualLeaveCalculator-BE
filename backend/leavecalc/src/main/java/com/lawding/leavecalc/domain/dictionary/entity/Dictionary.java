package com.lawding.leavecalc.domain.dictionary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private DictionaryCategory category;

    @Column(nullable = false, length = 200)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Dictionary(DictionaryCategory category, String question, String content) {
        this.category = category;
        this.question = question;
        this.content = content;
    }

    public static Dictionary create(DictionaryCategory category, String question, String content) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("질문은 필수입니다.");
        }
        if (question.length() > 200) {
            throw new IllegalArgumentException("질문은 200자를 초과할 수 없습니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        return new Dictionary(category, question, content);
    }
}