package com.lawding.leavecalc.domain.dictionary.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dictionary_category")
public class DictionaryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    private DictionaryCategory(String name) {
        validateName(name);
        this.name = name;
    }

    public static DictionaryCategory create(String name) {
        return new DictionaryCategory(name);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
        if (name.length() > 20) {
            throw new IllegalArgumentException("카테고리 이름은 20자를 초과할 수 없습니다.");
        }
    }

    public void changeName(String name) {
        validateName(name);
        this.name = name;
    }
}
