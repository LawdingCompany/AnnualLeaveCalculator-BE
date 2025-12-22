package com.lawding.leavecalc.domain.dictionary.repository;

import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    List<Dictionary> findAllByDeletedFalse();

    Optional<Dictionary> findByIdAndDeletedFalse(Long id);

    List<Dictionary> findAllByCategoryId(Long categoryId);
}
