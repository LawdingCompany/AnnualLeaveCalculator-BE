package com.lawding.leavecalc.domain.dictionary.controller;

import com.lawding.leavecalc.domain.dictionary.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;
//    @GetMapping
//    public ResponseEntity<ApiResponse<?>> getAllDictionaryList(){
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<?>> getDictionary(@PathVariable Long id){
//    }
}
