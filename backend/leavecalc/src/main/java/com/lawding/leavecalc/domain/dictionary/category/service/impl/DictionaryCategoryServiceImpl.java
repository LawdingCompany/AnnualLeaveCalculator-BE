package com.lawding.leavecalc.domain.dictionary.category.service.impl;

import com.lawding.leavecalc.domain.dictionary.category.dto.request.DictionaryCategoryRequest;
import com.lawding.leavecalc.domain.dictionary.category.entity.DictionaryCategory;
import com.lawding.leavecalc.domain.dictionary.category.repository.DictionaryCategoryRepository;
import com.lawding.leavecalc.domain.dictionary.category.service.DictionaryCategoryService;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import com.lawding.leavecalc.domain.dictionary.repository.DictionaryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DictionaryCategoryServiceImpl implements DictionaryCategoryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryCategoryRepository categoryRepository;

    @Override
    public List<DictionaryCategory> findAllDictionaryCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createDictionaryCategory(DictionaryCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        DictionaryCategory category = DictionaryCategory.create(request.name());

        // exists 검사 이후에도 동시 요청으로 UNIQUE 제약 위반이 발생할 수 있어, DB 예외를 의미 있는 오류로 변환하기 위함
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) { // DB의 무결성 제약을 어겼을 때 던지는 예외
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
    }

    @Override
    public void updateDictionaryCategory(Long id, DictionaryCategoryRequest request) {
        DictionaryCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "카테고리 항목이 존재하지 않습니다. id =" + id));
        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        category.changeName(request.name());

        // 동시에 같은 이름으로 변경 시 UNIQUE 제약 위반이 발생할 수 있어, DB 예외를 의미 있는 오류로 변환하기 위함
        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) { // DB의 무결성 제약을 어겼을 때 던지는 예외
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }
    }

    @Override
    public void deleteDictionaryCategory(Long id) {
        DictionaryCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "카테고리 항목이 존재하지 않습니다. id =" + id));

        DictionaryCategory defaultCategory = categoryRepository.findByName("기타")
            .orElseThrow(() -> new IllegalArgumentException("기본 카테고리(기타)가 존재하지 않습니다."));

        if (category.getId().equals(defaultCategory.getId())) {
            throw new IllegalArgumentException("기본 카테고리는 삭제할 수 없습니다.");
        }

        List<Dictionary> dictionaries = dictionaryRepository.findAllByCategoryId(id);

        for (Dictionary dictionary : dictionaries) {
            dictionary.changeCategory(defaultCategory);
        }

        categoryRepository.delete(category);
    }
}
