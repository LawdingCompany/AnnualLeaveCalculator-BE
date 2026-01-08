package com.lawding.leavecalc.domain.dictionary.repository.impl;

import com.lawding.leavecalc.domain.dictionary.repository.DictionaryRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DictionaryRepositoryCustomImpl implements DictionaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;
}
