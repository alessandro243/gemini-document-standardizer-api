package dev.alex.standardizer.web.controller;

import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.reportDtos.StoreReportDto;
import dev.alex.standardizer.web.dto.request.GeminiRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService service;

    @PostMapping
    public Map<String, StoreReportDto> GeminiConnector(@RequestParam("File") MultipartFile multipartFile) throws Exception {
        return service.callGemini(new GeminiRequestDto(), multipartFile);
    }
}
