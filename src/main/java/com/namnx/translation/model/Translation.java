package com.namnx.translation.model;

import com.namnx.translation.enumeration.TranslationMetadata;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "translation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "source_text", length = 2000)
    private String sourceText;

    @Column(name = "audio_url", length = 2000)
    private String audioUrl;

    @Column(name = "translate_id")
    private Long translateId;

    @Column(name = "translate_text", length = 2000)
    private String translateText;

    public static Translation fromLineCsv(String[] line) {
        return Translation.builder()
                .sourceId(Long.parseLong(line[TranslationMetadata.ID.getIndex()]))
                .sourceText(line[TranslationMetadata.TEXT.getIndex()])
                .translateId(nullIfEmpty(line[TranslationMetadata.TRANS_ID.getIndex()]))
                .translateText(line[TranslationMetadata.TRANS_TEXT.getIndex()])
                .audioUrl(line[TranslationMetadata.AUDIO.getIndex()])
                .build();
    }

    private static Long nullIfEmpty(String id) {
        return "".equals(id) ? null : Long.parseLong(id);
    }
}
