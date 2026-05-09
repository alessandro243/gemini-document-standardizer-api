package com.example.GeminiDocumentStandardizerApi.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {
    String role;
    List<PartDto> parts;
}
