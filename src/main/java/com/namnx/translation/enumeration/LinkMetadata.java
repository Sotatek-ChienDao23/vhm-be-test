package com.namnx.translation.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LinkMetadata {
    ID(0), TRANS_ID(1);

    private final int index;
}
