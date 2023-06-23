package com.namnx.translation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SentenceAudioMetadata {
    SENTENCE_ID(0), USERNAME(1), LICENSE(2), URL(3);

    private final int index;
}
