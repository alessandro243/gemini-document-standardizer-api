package dev.alex.standardizer.web.controller;

import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.request.GeminiRequestdto;
import dev.alex.standardizer.web.dto.response.GeminiResponsedto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService service;

    @PostMapping
    public String GeminiConnector(){
        GeminiResponsedto response = service.callGemini(new GeminiRequestdto(), "quanto é a metade de 2 mais dois?");
        return response.getCandidates().getFirst().getContent().getParts().getFirst().getText();
    }
}
