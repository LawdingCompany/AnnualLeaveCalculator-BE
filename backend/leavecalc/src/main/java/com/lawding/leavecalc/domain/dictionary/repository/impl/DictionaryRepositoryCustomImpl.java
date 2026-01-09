package com.lawding.leavecalc.domain.dictionary.repository.impl;

import static com.lawding.leavecalc.domain.dictionary.category.entity.QDictionaryCategory.dictionaryCategory;
import static com.lawding.leavecalc.domain.dictionary.entity.QDictionary.dictionary;

import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import com.lawding.leavecalc.domain.dictionary.repository.DictionaryRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class DictionaryRepositoryCustomImpl implements DictionaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Dictionary> search(String keyword, Pageable pageable) {

        List<Dictionary> content = queryFactory
            .selectFrom(dictionary)
            .join(dictionary.category, dictionaryCategory).fetchJoin()
            .where(
                dictionary.deleted.isFalse(),
                containsKeyword(keyword)
            )
            .orderBy(dictionary.updatedAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 쿼리 설계만, 마지막 페이지의 경우 실행할 필요x
        JPAQuery<Long> countQuery = queryFactory
            .select(dictionary.count())
            .from(dictionary)
            .where(
                dictionary.deleted.isFalse(),
                containsKeyword(keyword)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return dictionary.question.containsIgnoreCase(keyword);
    }
}
