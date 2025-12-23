package com.lawding.leavecalc.domain.dictionary.controller;

import com.lawding.leavecalc.domain.dictionary.service.DictionaryService;
import com.lawding.leavecalc.domain.dictionary.dto.response.DictionaryResponse;
import com.lawding.leavecalc.domain.global.common.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionaries")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DictionaryResponse>>> getActiveDictionaries() {
        log.info("GET /dictionary - 연차 백과사전 전체 리스트 요청");
        return ResponseEntity.ok(
            ApiResponse.ok(
                dictionaryService.findActiveDictionaries().stream()
                    .map(DictionaryResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryResponse>> getActiveDictionary(@PathVariable Long id) {
        log.info("GET /dictionary/{} - 연차 백과사전 항목({}) 요청", id, id);
        return ResponseEntity.ok(
            ApiResponse.ok(
                DictionaryResponse.from(
                    dictionaryService.findActiveDictionary(id)
                )
            )
        );
    }

}
