package com.namnx.translation.service;

import com.namnx.translation.enumeration.Language;
import com.namnx.translation.enumeration.LinkMetadata;
import com.namnx.translation.enumeration.SentenceAudioMetadata;
import com.namnx.translation.enumeration.SentenceMetadata;
import com.namnx.translation.utils.Constants;
import com.opencsv.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvParseAndMergeService {

    private static final String AUDIO_COMMON_URL = "https://audio.tatoeba.org/sentences/%s/%s.mp3";

    public void parseAndMerge() throws IOException {
        Map<String, Map<Long, String>> mapSentenceByIdAndLang = getOnlyEnglishAndVietnamSentences();
        Set<Long> englishIds = mapSentenceByIdAndLang
            .getOrDefault(Language.EN.getValue(), Collections.emptyMap()).keySet();
        Set<Long> vnIds = mapSentenceByIdAndLang
            .getOrDefault(Language.VN.getValue(), Collections.emptyMap()).keySet();
        Map<Long, Long> mapIdAndTranslationId = getLinkTranslation(englishIds, vnIds);
        Map<Long, String> mapIdAndAudioUrl = getEngAudioUrl(englishIds);

        File file = new File(Constants.TRANSLATED_FILE);
        Map<Long, String> mapSentenceEng = mapSentenceByIdAndLang.get(Language.EN.getValue());
        Map<Long, String> mapSentenceVn = mapSentenceByIdAndLang.get(Language.VN.getValue());
        try (FileWriter outputFile = new FileWriter(file);
             ICSVWriter writer = new CSVWriterBuilder(outputFile)
                 .withSeparator(Constants.TAB)
                 .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                 .build();
        ) {
            englishIds = new TreeSet<>(englishIds);
            englishIds.forEach(engId -> {
                Long translationId = mapIdAndTranslationId.get(engId);
                if (translationId != null) {
                    String[] lines = new String[]{
                        engId.toString(), mapSentenceEng.get(engId), mapIdAndAudioUrl.get(engId),
                        translationId.toString(), mapSentenceVn.getOrDefault(translationId, "")};
                    writer.writeNext(lines);
                }
            });
        }
    }


    public List<String[]> readLineByLine(String fileName, Predicate<String[]> passPredicate) {
        try {
            List<String[]> list = new ArrayList<>();
            ClassPathResource res = new ClassPathResource(fileName, getClass().getClassLoader());
            try (Reader reader = new BufferedReader(new InputStreamReader(res.getInputStream()));
                 CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(Constants.TAB)
                         .withIgnoreQuotations(true)
                         .build())
                     .build()
            ) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (passPredicate.test(line)) {
                        list.add(line);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception when try to get data from file [{}], e: {}", fileName, e.getMessage());
            return Collections.emptyList();
        }
    }

    private Map<String, Map<Long, String>> getOnlyEnglishAndVietnamSentences() {
        Predicate<String[]> passLinePredicate = lines -> lines.length == 3
            && (Language.EN.getValue().equals(lines[SentenceMetadata.LANG.getIndex()])
            || Language.VN.getValue().equals(lines[SentenceMetadata.LANG.getIndex()]));

        List<String[]> lines = readLineByLine(Constants.SENTENCES_FILE, passLinePredicate);
        return lines
            .stream()
            .collect(Collectors.groupingBy(
                    lineStc -> lineStc[SentenceMetadata.LANG.getIndex()],
                    Collectors.toMap(
                        curr -> Long.parseLong(curr[SentenceMetadata.ID.getIndex()]),
                        curr -> curr[SentenceMetadata.SENTENCE.getIndex()])
                )
            );
    }

    private Map<Long, Long> getLinkTranslation(Set<Long> engIds,
                                               Set<Long> vnIds) {
        if (engIds == null || engIds.isEmpty() || vnIds == null || vnIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Predicate<String[]> passLinePredicate = lines -> lines.length == 2
            && engIds.contains(Long.parseLong(lines[LinkMetadata.ID.getIndex()]))
            && vnIds.contains(Long.parseLong(lines[LinkMetadata.TRANS_ID.getIndex()]));

        return readLineByLine(Constants.LINKS_FILE, passLinePredicate)
            .stream()
            .collect(Collectors.toMap(
                lineStc -> Long.parseLong(lineStc[LinkMetadata.ID.getIndex()]),
                lineStc -> Long.parseLong(lineStc[LinkMetadata.TRANS_ID.getIndex()]),
                (firstDup, secondDup) -> firstDup
            ));
    }

    private Map<Long, String> getEngAudioUrl(Set<Long> engIds) {
        if (engIds == null || engIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Predicate<String[]> passLinePredicate = lines -> lines.length == 4
            && engIds.contains(Long.parseLong(lines[SentenceAudioMetadata.SENTENCE_ID.getIndex()]))
            && !"N".equals(lines[SentenceAudioMetadata.LICENSE.getIndex()]);

        return readLineByLine(Constants.SENTENCES_AUDIO_FILE, passLinePredicate)
            .stream()
            .collect(Collectors.toMap(
                lineStc -> Long.parseLong(lineStc[SentenceMetadata.ID.getIndex()]),
                lineStc -> String.format(
                    AUDIO_COMMON_URL,
                    Language.EN.getValue(),
                    lineStc[SentenceMetadata.ID.getIndex()]
                ),
                (firstDup, secondDup) -> firstDup)
            );
    }
}
