package com.namnx.translation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    EN("eng"), VN("vie");

    private final String value;
}
