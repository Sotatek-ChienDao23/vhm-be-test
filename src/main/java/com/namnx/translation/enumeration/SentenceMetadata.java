package com.namnx.translation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SentenceMetadata {
    ID(0), LANG(1), SENTENCE(2);

    private final int index;
}
