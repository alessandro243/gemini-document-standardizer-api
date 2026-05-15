package dev.alex.standardizer.service;

import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.web.dto.request.*;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final String ROLE = "user";
    private final Integer MAXOUTPUTTOKENS = 500;
    private final Double TEMPERATURE = 0.7;
    private final RestClient restClient;
    private final GeminiProperties properties;

    public GeminiRequestDto requestConstructor(GeminiRequestDto geminiRequestDTO, String prompt){

        PartRequestDto partDto = new PartRequestDto();
        partDto.setText(prompt);

        GenerationConfigRequestDto generationConfigDTO = new GenerationConfigRequestDto();
        generationConfigDTO.setMaxOutputTokens(MAXOUTPUTTOKENS);
        generationConfigDTO.setTemperature(TEMPERATURE);

        ContentRequestDto contentDto = new ContentRequestDto();
        contentDto.setRole(ROLE);
        contentDto.setParts(List.of(partDto));

        geminiRequestDTO.setGenerationConfig(generationConfigDTO);
        geminiRequestDTO.setContents(List.of(contentDto));

        return geminiRequestDTO;
    }

    public GeminiResponseDto callGemini(GeminiRequestDto requestDTO, String prompt){

        if(prompt == null || prompt.isBlank()){
            throw new IllegalArgumentException("O prompt não pode ser nulo ou vazio.");
        }

        return this.restClient
                .post()
                .uri(properties.getUrl() + properties.getKey())
                .body(requestConstructor(requestDTO, prompt))
                .retrieve()
                .body(GeminiResponseDto.class);
    }
}
