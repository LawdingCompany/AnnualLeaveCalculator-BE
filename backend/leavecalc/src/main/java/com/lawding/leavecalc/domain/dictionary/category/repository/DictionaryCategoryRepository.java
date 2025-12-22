package com.lawding.leavecalc.domain.dictionary.category.repository;

import com.lawding.leavecalc.domain.dictionary.category.entity.DictionaryCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryCategoryRepository extends JpaRepository<DictionaryCategory, Long> {

    Optional<DictionaryCategory> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
