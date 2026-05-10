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

    @PostMapping
    public String GeminiConnector(){
        GeminiResponseDto retorno = service.callGemini(new GeminiRequestDTO(), "Olá");
        return retorno.getCandidates().getFirst().getContent().getParts().getFirst().getText();
    }
}
