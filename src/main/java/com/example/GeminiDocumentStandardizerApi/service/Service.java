package com.example.GeminiDocumentStandardizerApi.service;

import com.example.GeminiDocumentStandardizerApi.config.RestClientConfig;
import com.example.GeminiDocumentStandardizerApi.repository.StandardizerRepository;
import com.example.GeminiDocumentStandardizerApi.web.dto.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    String url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    StandardizerRepository standardizerRepository;
    private final RestClient restClient;


    public Service(){
        this.restClient = RestClientConfig.rest_Client();
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

    public String callGemini(GeminiRequestDTO requestDTO, String key){
        return this.restClient
                .post()
                .uri(url + key)
                .body(requestConstructor(requestDTO))
                .retrieve()
                .body(String.class);
    }

}
