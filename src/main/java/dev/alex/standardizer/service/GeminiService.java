package dev.alex.standardizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alex.standardizer.config.GeminiProperties;
import dev.alex.standardizer.utils.Db_Utils;
import dev.alex.standardizer.utils.FileExtractorUtil;
import dev.alex.standardizer.web.dto.reportdtos.StoreReportDto;
import dev.alex.standardizer.web.dto.request.*;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final Db_Utils database;

    public StringBuilder parseAndProcessReport(GeminiRequestDto requestDTO, MultipartFile report) throws Exception{
        GeminiResponseDto retorno = callGemini(requestDTO, report);
        ObjectMapper jsonMapper = new ObjectMapper();
        Map<String, String> columnMapping = jsonMapper.readValue(retorno.getCandidates().get(0).getContent().getParts().get(0).getText(), Map.class);
        return fileExtractorUtil.parseCsvContent(columnMapping, report, database);
    }

    public StringBuilder compareReportRows(GeminiRequestDto report ,MultipartFile multipartFile) throws Exception{
        GeminiResponseDto retorno = callGemini(report, multipartFile);
        return new StringBuilder();
    }

    public GeminiRequestDto requestConstructor(GeminiRequestDto geminiRequestDTO, MultipartFile file){

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

    public GeminiResponseDto callGemini(GeminiRequestDto requestDTO, MultipartFile report) throws Exception{
        GeminiResponseDto retorno = this.restClient
                .post()
                .uri(properties.getUrl() + properties.getKey())
                .body(requestConstructor(requestDTO, report))
                .retrieve()
                .body(GeminiResponseDto.class);
        return retorno;
    }
}
