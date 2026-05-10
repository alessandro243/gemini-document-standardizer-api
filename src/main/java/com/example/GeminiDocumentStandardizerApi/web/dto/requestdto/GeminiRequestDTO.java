package com.example.GeminiDocumentStandardizerApi.web.dto.requestdto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeminiRequestDTO {
    List<ContentDto> contents;
    GenerationConfigDTO generationConfig;
}
