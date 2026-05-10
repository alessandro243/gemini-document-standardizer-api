package com.example.GeminiDocumentStandardizerApi.web.controller;

import com.example.GeminiDocumentStandardizerApi.service.Service;
import com.example.GeminiDocumentStandardizerApi.web.dto.requestdto.GeminiRequestDTO;
import com.example.GeminiDocumentStandardizerApi.web.dto.responsedto.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class Controller {
    private final Service service;

    @PostMapping("/{key}")
    public String GeminiConnector(@PathVariable String key){
        GeminiResponseDto retorno = service.callGemini(new GeminiRequestDTO(), key);
        return retorno.getCandidates().getFirst().getContent().getParts().getFirst().getText();
    }
}
