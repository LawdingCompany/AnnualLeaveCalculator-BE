package com.lawding.leavecalc.domain.dictionary.entity;

import com.lawding.leavecalc.domain.dictionary.category.entity.DictionaryCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dictionary")
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

    @Column(nullable = false)
    private boolean deleted = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private Dictionary(DictionaryCategory category, String question, String content) {
        validateCategory(category);
        validateQuestion(question);
        validateContent(content);
        this.category = category;
        this.question = question;
        this.content = content;
    }

    public static Dictionary create(DictionaryCategory category, String question, String content) {
        return new Dictionary(category, question, content);
    }

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }

    public void changeCategory(DictionaryCategory category) {
        validateCategory(category);
        this.category = category;
    }

    public void changeQuestion(String question) {
        validateQuestion(question);
        this.question = question;
    }

    public void changeContent(String content) {
        validateContent(content);
        this.content = content;
    }

    private static void validateCategory(DictionaryCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리는 필수입니다.");
        }
    }

    private static void validateQuestion(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("질문은 필수입니다.");
        }
        if (question.length() > 200) {
            throw new IllegalArgumentException("질문은 200자를 초과할 수 없습니다.");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
    }
}