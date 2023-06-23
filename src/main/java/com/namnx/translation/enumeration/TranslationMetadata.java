package com.namnx.translation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TranslationMetadata {
    ID(0), TEXT(1), AUDIO(2), TRANS_ID(3), TRANS_TEXT(4);

    private final int index;
}
