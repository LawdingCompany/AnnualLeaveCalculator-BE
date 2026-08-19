package com.lawding.dictionary.controller.admin;

import com.lawding.dictionary.dto.response.admin.DictionaryAdminResponse;
import com.lawding.dictionary.dto.request.admin.DictionaryRequest;
import com.lawding.dictionary.service.DictionaryService;
import com.lawding.global.common.dto.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dictionaries")
public class DictionaryAdminController {

    private final DictionaryService dictionaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DictionaryAdminResponse>>> getAllDictionaries() {
        return ResponseEntity.ok(
            ApiResponse.ok(
                dictionaryService.findAllDictionaries()
                    .stream()
                    .map(DictionaryAdminResponse::from)
                    .toList()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryAdminResponse>> getDictionary(
        @PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.ok(
                DictionaryAdminResponse.from(
                    dictionaryService.findDictionary(id)
                )
            )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createDictionary(
        @RequestBody @Valid DictionaryRequest request) {
        dictionaryService.createDictionary(request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전에 등록되었습니다."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateDictionary(
        @PathVariable Long id,
        @RequestBody @Valid DictionaryRequest request) {
        dictionaryService.updateDictionary(id, request);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 항목이 수정되었습니다."));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<ApiResponse<Void>> disableDictionary(@PathVariable Long id) {
        dictionaryService.disableDictionary(id);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 항목이 비활성화되었습니다."));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<ApiResponse<Void>> enableDictionary(@PathVariable Long id) {
        dictionaryService.enableDictionary(id);
        return ResponseEntity.ok(ApiResponse.okMessage("사전 항목이 활성화되었습니다."));
    }

}
