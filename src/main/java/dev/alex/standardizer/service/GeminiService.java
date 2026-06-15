package dev.alex.standardizer.service;

import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.utils.FileExtractorUtil;
import dev.alex.standardizer.web.dto.DivergenciaDTO;
import dev.alex.standardizer.web.dto.request.*;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;

import java.io.File;
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
    private FileExtractorUtil fileExtractorUtil = new FileExtractorUtil();

    public GeminiRequestDto requestConstructor(GeminiRequestDto geminiRequestDTO, File file){

        PartRequestDto partDto = new PartRequestDto();
        partDto.setText(fileExtractorUtil.promptConfig(file));

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

    public void callGemini(GeminiRequestDto requestDTO, File report){

        GeminiResponseDto retorno = this.restClient
                .post()
                .uri(properties.getUrl() + properties.getKey())
                .body(requestConstructor(requestDTO, report))
                .retrieve()
                .body(GeminiResponseDto.class);

        fileExtractorUtil.parseCsvContent(retorno);

    }
}
