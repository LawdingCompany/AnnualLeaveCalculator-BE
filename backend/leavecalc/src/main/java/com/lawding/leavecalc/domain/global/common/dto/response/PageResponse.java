package com.lawding.leavecalc.domain.global.common.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content,
    PageInfo page
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<T>(
            page.getContent(),
            PageInfo.from(page)
        );
    }

}
