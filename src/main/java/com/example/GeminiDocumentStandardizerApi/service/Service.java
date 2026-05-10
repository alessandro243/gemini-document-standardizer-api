package com.example.GeminiDocumentStandardizerApi.service;

import com.example.GeminiDocumentStandardizerApi.repository.StandardizerRepository;
import com.example.GeminiDocumentStandardizerApi.web.dto.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    String apiKey = "AIzaSyBUoSrgRsy9M3WNVToxfhyYt6U1btEwI48";
    String url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + apiKey;

    StandardizerRepository standardizerRepository;
    private final RestClient restClient;

    public Service(RestClient restClient){
        this.restClient = restClient;
    }

    public GeminiRequestDTO requestConstructor(GeminiRequestDTO geminiRequestDTO){

        PartDto partDto = new PartDto();
        partDto.setText("Olá");

        GenerationConfigDTO generationConfigDTO = new GenerationConfigDTO();
        generationConfigDTO.setMaxOutputTokens(100);
        generationConfigDTO.setTemperature(0.7);

        ContentDto contentDto = new ContentDto();
        contentDto.setRole("user");
        contentDto.setParts(List.of(partDto));

        geminiRequestDTO.setGenerationConfig(generationConfigDTO);
        geminiRequestDTO.setContents(List.of(contentDto));

        return geminiRequestDTO;
    }

    public GeminiRequestDTO callGemini(GeminiRequestDTO requestDTO){
        return this.restClient
                .post()
                .uri(url)
                .body(requestDTO)
                .retrieve()
                .body(GeminiRequestDTO.class);
    }

}
