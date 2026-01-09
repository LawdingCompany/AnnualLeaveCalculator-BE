package com.lawding.leavecalc.domain.dictionary.category.controller;

import com.lawding.leavecalc.domain.dictionary.category.dto.request.DictionaryCategoryRequest;
import com.lawding.leavecalc.domain.dictionary.category.dto.response.DictionaryCategoryResponse;
import com.lawding.leavecalc.domain.dictionary.category.service.DictionaryCategoryService;
import com.lawding.leavecalc.domain.global.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dictionary-categories")
public class DictionaryCategoryController {

    private final DictionaryCategoryService dictionaryCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DictionaryCategoryResponse>>> getAllDictionaryCategories() {
        log.info("GET /admin/dictionary-categories - 사전 카테고리 전체 리스트 요청 for 관리자");
        return ResponseEntity.ok(
            ApiResponse.ok(
                dictionaryCategoryService.findAllDictionaryCategories().stream()
                    .map(DictionaryCategoryResponse::from)
                    .toList()
            )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDictionaryCategory(
        @RequestBody @Valid DictionaryCategoryRequest request) {
        log.info("POST /admin/dictionary-categories - 사전 카테고리 생성 요청 for 관리자");
        dictionaryCategoryService.createDictionaryCategory(request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 등록되었습니다."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateDictionaryCategory(
        @PathVariable Long id,
        @RequestBody @Valid DictionaryCategoryRequest request) {
        log.info("PUT /admin/dictionary-categories - 사전 카테고리({}) 수정 요청", id);
        dictionaryCategoryService.updateDictionaryCategory(id, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 수정되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDictionaryCategory(
        @PathVariable Long id){
        log.info("DELETE /admin/dictionary-categories - 사전 카테고리({}) 삭제 요청", id);
        dictionaryCategoryService.deleteDictionaryCategory(id);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 삭제되었습니다."));
    }
}
