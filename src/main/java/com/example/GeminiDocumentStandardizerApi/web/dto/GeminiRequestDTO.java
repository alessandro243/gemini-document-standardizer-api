package com.example.GeminiDocumentStandardizerApi.web.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeminiRequestDTO {
    List<ContentDto> contents;
    GenerationConfigDTO generationConfig;
}
