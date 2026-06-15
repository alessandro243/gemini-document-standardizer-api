package dev.alex.standardizer.utils;

import dev.alex.standardizer.config.PromptProperties;
import dev.alex.standardizer.web.dto.DivergenciaDTO;
import dev.alex.standardizer.web.dto.response.GeminiResponseDto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class FileExtractorUtil {

    public String promptConfig(File report){

        String fileReport = report.getPath();
        String filePrompt = new PromptProperties().getPromptDir();

        try(BufferedReader bf = new BufferedReader(new FileReader(fileReport))) {
            String line1 = bf.readLine();
            String line2 = bf.readLine();
            String line3 = bf.readLine();

            fileReport = line1 + "\n\n" +
                    "[AMOSTRA DE DADOS (LINHAS 2 E 3)]\n" +
                    line2 + "\n" +
                    line3 + "\n" +
                    "--------------------------------------------------";
        }catch (IOException error){
            System.out.println("Deu ruim");
        }

        try(BufferedReader bf = new BufferedReader(new FileReader(filePrompt))) {
            filePrompt = "";
            for(String line: bf.lines().toList()){
                filePrompt += line + "\n";
            }
        }catch (IOException error){
            System.out.println("Deu ruim");
        }

        return filePrompt + "\n" + fileReport;
    }

    public List<DivergenciaDTO> parseCsvContent(GeminiResponseDto responseDto){
        return null;
    }
}
