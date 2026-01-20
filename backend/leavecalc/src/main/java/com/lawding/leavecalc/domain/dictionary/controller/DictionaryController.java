package com.lawding.leavecalc.domain.dictionary.controller;

import com.lawding.leavecalc.domain.dictionary.entity.Dictionary;
import com.lawding.leavecalc.domain.dictionary.service.DictionaryService;
import com.lawding.leavecalc.domain.dictionary.dto.response.DictionaryResponse;
import com.lawding.leavecalc.domain.global.common.dto.response.ApiResponse;
import com.lawding.leavecalc.domain.global.common.dto.response.PageResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DictionaryResponse>>> getActiveDictionaries() {
        log.info("[req] GET /dictionary - 연차 백과사전 전체 리스트 요청");
        return ResponseEntity.ok(
            ApiResponse.ok(
                dictionaryService.findActiveDictionaries().stream()
                    .map(DictionaryResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryResponse>> getActiveDictionary(
        @PathVariable Long id) {
        log.info("[req] GET /dictionary/[id] - 연차 백과사전 항목({}) 요청", id);
        return ResponseEntity.ok(
            ApiResponse.ok(
                DictionaryResponse.from(
                    dictionaryService.findActiveDictionary(id)
                )
            )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<DictionaryResponse>>> searchDictionaries(
        @RequestParam @Size(max = 20) String keyword,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "6") @Min(1) int size
    ) {
        log.info("[req] GET /search?keyword=[]&page=[]&size=[] - 연차 백과사전 검색 키워드 = {}, 페이지 = {}, 크기 = {}",
            keyword, page, size);

        Page<Dictionary> result = dictionaryService.searchDictionaries(keyword, page, size);

        return ResponseEntity.ok(
            ApiResponse.ok(
                PageResponse.from(
                    result.map(DictionaryResponse::from)
                )
            )
        );
    }

}
