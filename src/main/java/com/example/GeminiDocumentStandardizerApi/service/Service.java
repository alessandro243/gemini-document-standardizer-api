package com.example.GeminiDocumentStandardizerApi.service;

import com.example.GeminiDocumentStandardizerApi.config.RestClientConfig;
import com.example.GeminiDocumentStandardizerApi.repository.StandardizerRepository;
import com.example.GeminiDocumentStandardizerApi.web.dto.requestdto.ContentDto;
import com.example.GeminiDocumentStandardizerApi.web.dto.requestdto.GeminiRequestDTO;
import com.example.GeminiDocumentStandardizerApi.web.dto.requestdto.GenerationConfigDTO;
import com.example.GeminiDocumentStandardizerApi.web.dto.requestdto.PartDto;
import com.example.GeminiDocumentStandardizerApi.web.dto.responsedto.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.List;

@org.springframework.stereotype.Service
public class Service {
    @Value("${gemini.api.url}")
    String url;

    @Value("${gemini.api.key}")
    String key;

    StandardizerRepository standardizerRepository;
    private final RestClient restClient;


    public Service(){
        this.restClient = RestClientConfig.rest_Client();
    }

    public GeminiRequestDTO requestConstructor(GeminiRequestDTO geminiRequestDTO, String prompt){

        PartDto partDto = new PartDto();
        partDto.setText(prompt);

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

    public GeminiResponseDto callGemini(GeminiRequestDTO requestDTO, String prompt){
        return this.restClient
                .post()
                .uri(url + key)
                .body(requestConstructor(requestDTO, prompt))
                .retrieve()
                .body(GeminiResponseDto.class);
    }

}
