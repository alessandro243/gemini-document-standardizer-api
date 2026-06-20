package dev.alex.standardizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.utils.FileExtractorUtil;
import dev.alex.standardizer.web.dto.reportDtos.StoreReportDto;
import dev.alex.standardizer.web.dto.request.*;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final String ROLE = "user";
    private final Integer MAXOUTPUTTOKENS = 2048;
    private final Double TEMPERATURE = 0.7;
    private final RestClient restClient;
    private final GeminiProperties properties;
    private FileExtractorUtil fileExtractorUtil = new FileExtractorUtil();

    public GeminiRequestDto requestConstructor(GeminiRequestDto geminiRequestDTO, MultipartFile file){

        PartRequestDto partDto = new PartRequestDto();
        partDto.setText(fileExtractorUtil.promptConfig(file));

        GenerationConfigRequestDto generationConfigDTO = new GenerationConfigRequestDto();
        generationConfigDTO.setMaxOutputTokens(MAXOUTPUTTOKENS);
        generationConfigDTO.setTemperature(TEMPERATURE);
        ///generationConfigDTO.setResponseMimeType("application/json");

        ContentRequestDto contentDto = new ContentRequestDto();
        contentDto.setRole(ROLE);
        contentDto.setParts(List.of(partDto));

        geminiRequestDTO.setGenerationConfig(generationConfigDTO);
        geminiRequestDTO.setContents(List.of(contentDto));

        return geminiRequestDTO;
    }

    public Map<String, StoreReportDto> callGemini(GeminiRequestDto requestDTO, MultipartFile report) throws Exception{

        GeminiResponseDto retorno = this.restClient
                .post()
                .uri(properties.getUrl() + properties.getKey())
                .body(requestConstructor(requestDTO, report))
                .retrieve()
                .body(GeminiResponseDto.class);
        ObjectMapper ferramentaJson = new ObjectMapper();
        Map<String, String> manualIa = ferramentaJson.readValue(retorno.getCandidates().get(0).getContent().getParts().get(0).getText(), Map.class);

        return fileExtractorUtil.parseCsvContent(manualIa, report);
    }
}
