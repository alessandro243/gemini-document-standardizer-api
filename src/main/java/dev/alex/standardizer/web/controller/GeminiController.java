package dev.alex.standardizer.web.controller;

import dev.alex.standardizer.service.GeminiService;
import dev.alex.standardizer.web.dto.reportdtos.StoreReportDto;
import dev.alex.standardizer.web.dto.request.GeminiRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService service;

    @PostMapping("/final-report")
    public StringBuilder processFinalReport (@RequestParam("File") MultipartFile multipartFile) throws Exception {
        return service.parseAndProcessReport(new GeminiRequestDto(), multipartFile);
    }

    @PostMapping("/report-parser")
    public StringBuilder compareReportRows(@RequestParam("File") MultipartFile multipartFile) throws Exception{
        return service.compareReportRows(new GeminiRequestDto(), multipartFile );
    }

}
