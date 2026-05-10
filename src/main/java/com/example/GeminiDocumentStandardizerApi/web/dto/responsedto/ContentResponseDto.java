package com.example.GeminiDocumentStandardizerApi.web.dto.responsedto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContentResponseDto {
    List<PartsResponseDto> parts;
}
