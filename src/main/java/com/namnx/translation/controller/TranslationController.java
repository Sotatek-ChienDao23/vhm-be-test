package com.namnx.translation.controller;

import com.namnx.translation.service.CsvParseAndMergeService;
import com.namnx.translation.service.TranslationService;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/translations")
@RequiredArgsConstructor
@Slf4j
public class TranslationController {

    private final TranslationService translationService;
    private final CsvParseAndMergeService csvParseAndMergeService;

    @PostMapping("/prepare-translation-file")
    public ResponseEntity<Object> prepareData() {
        log.info("Start prepareData!!!!!!!");
        long start = System.currentTimeMillis();
        try {
            csvParseAndMergeService.parseAndMerge();
            log.info("prepareData successfully in [{}]ms!!!!!!!", (System.currentTimeMillis() - start));
            return ResponseEntity.ok("Prepare data successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("prepareData failed in [{}]ms!!!!!!!", (System.currentTimeMillis() - start));
            return ResponseEntity.internalServerError().body("Somethings error. Exception: " + e);
        }
    }

    @PostMapping("/save-data")
    public ResponseEntity<Object> saveData() throws CsvValidationException, IOException {
        log.info("Start saveData!!!!!!!");
        long start = System.currentTimeMillis();
        translationService.importToDb();
        log.info("saveData successfully in [{}]ms!!!!!!!", (System.currentTimeMillis() - start));
        return ResponseEntity.ok("OK!");
    }

    @GetMapping("")
    public ResponseEntity<Object> getPage(@RequestParam int page,
                                          @RequestParam int size) {
        if (page <= 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Page and size param must be greater than 0!");
        }
        return ResponseEntity.ok(translationService.getPageTranslation(page, size));
    }
}
