package com.namnx.translation.service;

import com.namnx.translation.controller.response.TranslationResponse;
import com.namnx.translation.model.Translation;
import com.namnx.translation.repository.TranslationRepository;
import com.namnx.translation.utils.Constants;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private static final int BATCH_SIZE = 50;
    private final TranslationRepository translationRepository;

    @Transactional
    public void importToDb() throws IOException, CsvValidationException {
        try (Reader reader = Files.newBufferedReader(Path.of(Constants.TRANSLATED_FILE));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(Constants.TAB)
                             .withIgnoreQuotations(true)
                             .build())
                     .build()) {
            String[] line;
            List<Translation> translations = new ArrayList<>();
            while ((line = csvReader.readNext()) != null) {
                Translation translation = Translation.fromLineCsv(line);
                translations.add(translation);
                if (translations.size() == BATCH_SIZE) {
                    translationRepository.saveAll(translations);
                    translations.clear();
                }
            }
            if (!translations.isEmpty()) {
                translationRepository.saveAll(translations);
            }
        }
    }

    public Page<TranslationResponse> getPageTranslation(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.Direction.ASC, "sourceId");
        return translationRepository.findAll(pageable).map(this::fromEntity);
    }

    private TranslationResponse fromEntity(Translation translation) {
        return TranslationResponse
                .builder()
                .id(translation.getSourceId())
                .text(translation.getSourceText())
                .audioUrl(translation.getAudioUrl())
                .translateId(translation.getTranslateId())
                .translateText("".equals(translation.getTranslateText()) ? null : translation.getTranslateText())
                .build();
    }
}
