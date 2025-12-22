package com.lawding.leavecalc.domain.dictionary.service;

import com.lawding.leavecalc.domain.dictionary.dto.request.admin.DictionaryRequest;
import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import java.util.List;

public interface DictionaryService {

    List<Dictionary> findAllDictionaries();

    Dictionary findDictionary(Long id);

    void createDictionary(DictionaryRequest request);

    void updateDictionary(Long id, DictionaryRequest request);

    void disableDictionary(Long id);

    void enableDictionary(Long id);

    List<Dictionary> findActiveDictionaries();

    Dictionary findActiveDictionary(Long id);
}
