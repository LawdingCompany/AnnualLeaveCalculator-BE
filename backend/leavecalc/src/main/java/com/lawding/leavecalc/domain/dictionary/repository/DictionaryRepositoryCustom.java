package com.lawding.leavecalc.domain.dictionary.repository;

import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import org.springframework.data.domain.Page;

public interface DictionaryRepositoryCustom {
    Page<Dictionary> searchDictionaries
}
