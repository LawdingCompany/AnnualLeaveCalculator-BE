package com.lawding.leavecalc.domain.dictionary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DictionaryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Builder
    private DictionaryCategory(String name) {
        this.name = name;
    }

    public static DictionaryCategory create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("카테고리 이름은 20자를 초과할 수 없습니다.");
        }
        return DictionaryCategory.builder().name(name).build();
    }
}
