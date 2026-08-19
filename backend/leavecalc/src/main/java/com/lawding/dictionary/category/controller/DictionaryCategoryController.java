package com.lawding.dictionary.category.controller;

import com.lawding.dictionary.category.dto.request.DictionaryCategoryRequest;
import com.lawding.dictionary.category.dto.response.DictionaryCategoryResponse;
import com.lawding.dictionary.category.service.DictionaryCategoryService;
import com.lawding.global.common.dto.response.ApiResponse;
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
        dictionaryCategoryService.createDictionaryCategory(request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 등록되었습니다."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateDictionaryCategory(
        @PathVariable Long id,
        @RequestBody @Valid DictionaryCategoryRequest request) {
        dictionaryCategoryService.updateDictionaryCategory(id, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 수정되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDictionaryCategory(
        @PathVariable Long id){
        dictionaryCategoryService.deleteDictionaryCategory(id);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 카테고리가 삭제되었습니다."));
    }
}
