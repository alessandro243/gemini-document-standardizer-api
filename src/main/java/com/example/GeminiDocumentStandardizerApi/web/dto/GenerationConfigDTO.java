package com.example.GeminiDocumentStandardizerApi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerationConfigDTO {
    MaxOutputDto maxOutputTokens;
    TemperatureDto temperature;
}
