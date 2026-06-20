package dev.alex.standardizer.utils;

import dev.alex.standardizer.config.PromptProperties;
import dev.alex.standardizer.web.dto.reportDtos.StoreReportDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileExtractorUtil {
    public Boolean isEmptyLine(String line){
        if(line.trim().isEmpty()){
            return true;
        }
        return false;
    }

    public String separatorDetector(String reader){

        if (reader.contains(",")) {
            return ",";
        }else if (reader.contains(";")) {
            return ";";
        }
        return null;
    }

    public ArrayList<String> removeQuotes(String line, String separator){
        String[] columns = line.split(separator);
        ArrayList<String> newColumns = new ArrayList<>();
        for(String column: columns){
            if (column.contains("\"")) {
                String newColumn = column.replace("\"", "").trim();
                newColumns.add(newColumn);
                continue;
            }
            newColumns.add(column);
        }
        return newColumns;
    }

    public String promptConfig(MultipartFile report) {

        String fileReport = "";
        String filePrompt = new PromptProperties().getPromptDir();

        try (BufferedReader bf = new BufferedReader(new InputStreamReader(report.getInputStream(), StandardCharsets.UTF_8))) {
            String line1 = bf.readLine();
            String line2 = bf.readLine();
            String line3 = bf.readLine();

            fileReport = line1 + "\n\n" +
                    "[AMOSTRA DE DADOS (LINHAS 2 E 3)]\n" +
                    line2 + "\n" +
                    line3 + "\n" +
                    "--------------------------------------------------";
        } catch (IOException error) {
            System.out.println("Deu ruim ao ler o relatório físico");
        }

        try (BufferedReader bf = new BufferedReader(new FileReader(filePrompt))) {

            StringBuilder sbPrompt = new StringBuilder();
            for (String line : bf.lines().toList()) {
                sbPrompt.append(line).append("\n");
            }
            filePrompt = sbPrompt.toString();

        } catch (IOException error) {
            System.out.println("Deu ruim ao ler o arquivo de prompt");
        }

        return filePrompt + "\n" + fileReport;
    }

    public Map<String, StoreReportDto> parseCsvContent(Map<String, String> geminiResponseMap, MultipartFile filePath) {
        Map<String, StoreReportDto> finalStoreReport = new HashMap<>();
        String line;
        boolean firstLine = true;
        Set<String> notasProcessadas = new HashSet<>();
        int idxNota = 0, idxLoja = 0, idxVol = 0, idxValor = 0, idxData = 0, idxProd = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePath.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {

                if (isEmptyLine(line)){
                    continue;
                }

                if (firstLine){

                String separator = separatorDetector(line);
                ArrayList<String> colunas = removeQuotes(line, separator);

                    idxNota = colunas.indexOf(geminiResponseMap.get("nota_fiscal").trim());
                    idxLoja = colunas.indexOf(geminiResponseMap.get("loja_origem").trim());
                    idxVol = colunas.indexOf(geminiResponseMap.get("volume").trim());
                    idxValor = colunas.indexOf(geminiResponseMap.get("valor_declarado").trim());
                    idxData = colunas.indexOf(geminiResponseMap.get("data").trim());
                    idxProd = colunas.indexOf(geminiResponseMap.get("produto").trim());

                    firstLine = false;
                    continue;
                }

                if (line.contains("R$")) {
                    line = line.replaceAll("(R\\$\\s?\\d+),(\\d{2})", "$1.$2");
                }

                String separator = separatorDetector(line);
                ArrayList<String> columns = removeQuotes(line, separator);

                String numNota = columns.get(idxNota).trim();
                String valorVolume = columns.get(idxVol).trim();
                int volum = Integer.parseInt(valorVolume);
                String monetaryValue = columns.get(idxValor).trim();
                String cleanValue = monetaryValue.replaceAll("[^0-9.]", "");
                BigDecimal decimalValue = new BigDecimal(cleanValue);
                String storeName = columns.get(idxLoja).trim();

                StoreReportDto storeReportDto = finalStoreReport.computeIfAbsent(storeName, k -> new StoreReportDto());
                storeReportDto.increaseTotalPecas(volum);

                if (!notasProcessadas.contains(numNota)) {
                    storeReportDto.increaseTotalRetido(decimalValue);
                    notasProcessadas.add(numNota);
                }
            }
        } catch (IOException e) {
            System.out.println("Deu ruim!");
        }
        return finalStoreReport;
    }
}
