package com.lawding.leavecalc.domain.dictionary.service.impl;

import com.lawding.leavecalc.domain.dictionary.dto.request.admin.DictionaryRequest;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import com.lawding.leavecalc.domain.dictionary.category.entity.DictionaryCategory;
import com.lawding.leavecalc.domain.dictionary.category.repository.DictionaryCategoryRepository;
import com.lawding.leavecalc.domain.dictionary.repository.DictionaryRepository;
import com.lawding.leavecalc.domain.dictionary.service.DictionaryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryCategoryRepository categoryRepository;

    @Transactional(readOnly = true) // JPA 성능 최적화를 위한 Read 전용임을 표시
    @Override
    public List<Dictionary> findAllDictionaries() {
        return dictionaryRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Dictionary> findActiveDictionaries() {
        return dictionaryRepository.findAllByDeletedFalse();
    }
    @Transactional(readOnly = true)
    @Override
    public Dictionary findActiveDictionary(Long id) {
        return dictionaryRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("사전 항목이 존재하지 않습니다. id = " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Dictionary findDictionary(Long id) {
        return dictionaryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사전 항목이 존재하지 않습니다. id =" + id));
    }

    @Override
    public void createDictionary(DictionaryRequest request) {
        // 카테고리가 있는지 확인
        DictionaryCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "카테고리 항목이 존재하지 않습니다. id =" + request.categoryId()));

        Dictionary dictionary = Dictionary.create(category, request.question(), request.content());
        dictionaryRepository.save(dictionary);
        log.info("백과사전 생성 성공. DictionaryId = {}", dictionary.getId());
    }

    @Override
    public void updateDictionary(Long id, DictionaryRequest request) {
        Dictionary dictionary = dictionaryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사전 항목이 존재하지 않습니다. id =" + id));

        DictionaryCategory category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException(
                "카테고리 항목이 존재하지 않습니다. id =" + request.categoryId()));

        dictionary.changeCategory(category);
        dictionary.changeQuestion(request.question());
        dictionary.changeContent(request.content());
    }

    @Override
    public void disableDictionary(Long id) {
        Dictionary dictionary = dictionaryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사전 항목이 존재하지 않습니다. id =" + id));
        if (dictionary.isDeleted()) {
            throw new IllegalArgumentException("이미 비활성화된 사전 항목입니다. id = " + id);
        }
        dictionary.delete();
        log.info("백과사전 비활성화 성공. DictionaryId = {}", id);
    }

    @Override
    public void enableDictionary(Long id) {
        Dictionary dictionary = dictionaryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사전 항목이 존재하지 않습니다. id =" + id));
        if (!dictionary.isDeleted()) {
            throw new IllegalArgumentException("이미 활성화된 사전 항목입니다. id = " + id);
        }
        dictionary.restore();
        log.info("백과사전 활성화 성공. DictionaryId = {}", id);
    }


}
