package dev.alex.standardizer.service;

import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.web.dto.request.ContentRequestdto;
import dev.alex.standardizer.web.dto.request.GeminiRequestdto;
import dev.alex.standardizer.web.dto.request.GenerationConfigRequestdto;
import dev.alex.standardizer.web.dto.request.PartRequestdto;
import dev.alex.standardizer.web.dto.response.GeminiResponsedto;
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

    public GeminiRequestdto requestConstructor(GeminiRequestdto geminiRequestDTO, String prompt){

        PartRequestdto partDto = new PartRequestdto();
        partDto.setText(prompt);

        GenerationConfigRequestdto generationConfigDTO = new GenerationConfigRequestdto();
        generationConfigDTO.setMaxOutputTokens(MAXOUTPUTTOKENS);
        generationConfigDTO.setTemperature(TEMPERATURE);

        ContentRequestdto contentDto = new ContentRequestdto();
        contentDto.setRole(ROLE);
        contentDto.setParts(List.of(partDto));

        geminiRequestDTO.setGenerationConfig(generationConfigDTO);
        geminiRequestDTO.setContents(List.of(contentDto));

        return geminiRequestDTO;
    }

    public GeminiResponsedto callGemini(GeminiRequestdto requestDTO, String prompt){
                return this.restClient
                .post()
                .uri(properties.getUrl() + properties.getKey())
                .body(requestConstructor(requestDTO, prompt))
                .retrieve()
                .body(GeminiResponsedto.class);
    }
}
