package dev.alex.standardizer.web.controller;

import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.request.GeminiRequestDto;
import dev.alex.standardizer.web.dto.request.PromptDto;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService service;

    @PostMapping
    public void GeminiConnector(){
        /// GeminiResponseDto response =
        service.callGemini(new GeminiRequestDto());
    }
}
