package com.lawding.leavecalc.dictionary.repository;

import com.lawding.leavecalc.dictionary.entity.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DictionaryRepositoryCustom {
    Page<Dictionary> search(String keyword, Pageable pageable);
}
