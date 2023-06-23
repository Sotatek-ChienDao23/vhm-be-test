package com.namnx.translation.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationResponse {
    private long id;
    private String text;
    private String audioUrl;
    private long translateId;
    private String translateText;

}
