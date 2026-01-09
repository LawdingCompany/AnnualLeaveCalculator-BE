package com.lawding.leavecalc.domain.global.common.dto.response;

import org.springframework.data.domain.Page;

public record PageInfo(
    int page,   // 현재 페이지
    int size,   // 한 페이지에 담긴 개수
    long totalElements, // 전체 데이터 수
    int totalPages, // 전체 페이지 수
    boolean hasNext, // 다음 페이지 존재 여부
    boolean hasPrevious // 이전 페이지 존재 여부
) {

    public static PageInfo from(Page<?> page) {
        return new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
