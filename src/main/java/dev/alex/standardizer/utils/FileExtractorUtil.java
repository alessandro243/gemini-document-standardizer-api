package dev.alex.standardizer.utils;

import dev.alex.standardizer.config.PromptProperties;
import dev.alex.standardizer.web.dto.reportdtos.DivergenceDto;
import dev.alex.standardizer.web.dto.reportdtos.StoreReportDto;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.StreamSupport;

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
        String reportName = report.getOriginalFilename();

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
        String finalPrompt = reportName + "\n" + filePrompt + "\n" + fileReport;
        return finalPrompt;
    }

    public StringBuilder parseCsvContent(Map<String, String> geminiResponseMap, MultipartFile filePath, Db_Utils database) {
        Map<String, StoreReportDto> finalStoreReport = new HashMap<>();
        String line;
        boolean firstLine = true;
        Set<String> processedNotes = new HashSet<>();
        int idxNota = 0, idxLoja = 0, idxVol = 0, idxValor = 0, idxData = 0, idxProd = 0;
        String reportId = geminiResponseMap.get("id_relatorio");
        int volum2 = 0;
        BigDecimal big = BigDecimal.ZERO;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(filePath.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {

                if (isEmptyLine(line)){
                    continue;
                }

                if (firstLine){

                String separator = separatorDetector(line);
                ArrayList<String> columns = removeQuotes(line, separator);

                    idxNota = columns.indexOf(geminiResponseMap.get("nota_fiscal").trim());
                    idxLoja = columns.indexOf(geminiResponseMap.get("loja_origem").trim());
                    idxVol = columns.indexOf(geminiResponseMap.get("volume").trim());
                    idxValor = columns.indexOf(geminiResponseMap.get("valor_declarado").trim());
                    idxData = columns.indexOf(geminiResponseMap.get("data").trim());
                    idxProd = columns.indexOf(geminiResponseMap.get("produto").trim());
                    System.out.println(geminiResponseMap.get("valor_declarado").trim() + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    System.out.println(columns.get(idxValor) + "¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨");
                    firstLine = false;
                    continue;
                }

                if (line.contains("R$")) {
                    line = line.replaceAll("(R\\$\\s?[\\d.]+),(\\d{2})", "$1.$2");
                    line = line.replaceAll(",", ";");
                    ///line = line.replace("R$ ", "");
                }

                System.out.println(line);
                String separator = separatorDetector(line);
                ArrayList<String> columns = removeQuotes(line, separator);

                String numNota = columns.get(idxNota).trim();
                String valorVolume = columns.get(idxVol).trim();
                String storeName = columns.get(idxLoja).trim();
                int volum = Integer.parseInt(valorVolume);
                String primeiramente = columns.get(idxValor).trim();
                ///System.out.println("Loja: " + storeName + " Valor: " + primeiramente);
                String monetaryValue = primeiramente.replaceAll("[^0-9,.]", "");
                String cleanValue = monetaryValue.replaceAll("\\.(?=.*\\.)", "");
                ///String cleanValue = .replaceAll("\\.(?=.*\\.)", "");
                ///System.out.println("Monetary value: " + monetaryValue);
                ///System.out.println("Clean value: " + cleanValue);
                Double decimalValue = Double.parseDouble(cleanValue);
                ///System.out.println("Decimal value: " + decimalValue);


                StoreReportDto storeReportDto = finalStoreReport.computeIfAbsent(storeName, k -> new StoreReportDto());
                storeReportDto.increaseTotalPecas(volum);

                if (!processedNotes.contains(numNota)) {
                    System.out.println("entrei na nota: " + numNota);
                    storeReportDto.increaseTotalRetido(decimalValue);
                    processedNotes.add(numNota);
                }else{
                System.out.println("Não entrei na nota: " + numNota);
                }
            }

        } catch (IOException e) {
            System.out.println("Deu ruim!");
        }
        List<Map<String, Object>> data = database.select(database, reportId);
        List<String > geminiValues = new ArrayList<>(geminiResponseMap.values());
        StringBuilder finalReport = makeDivergenceDto(data, finalStoreReport);
        return finalReport;
    }

    public StringBuilder makeDivergenceDto(List<Map<String, Object>> data, Map<String, StoreReportDto> finalStore){
        List<LinkedHashMap<String, Object>> returnMap = new ArrayList<>();
        Map<String, String>  keys_ = new HashMap<>();
        Double valorTotal = 0.0;
        int volumeTotal = 0;
        Double valorTotalBank = 0.0;
        Double volumeTotalBank = 0.0;
        StringBuilder csv = new StringBuilder();
        csv.append("loja_origem;valor_total_retido;atraso_medio_dias;unidade_auditora;volume_total_pecas;Volume conciliado;Divergencia de volume;Valor conciliado;Divergencia de valor" + "\n");
        System.out.println("Data: " + data);
        System.out.println("finalStore: " + finalStore);

        for (Map<String, Object> map__ : data) {
            LinkedHashMap<String, Object> returnMap2 = new LinkedHashMap<>();
            HashMap<String, Object> meuHashMap = new HashMap<>(map__);
            valorTotal = finalStore.get(meuHashMap.get("loja_origem")).getAccumulatedValue();
            volumeTotal = finalStore.get(meuHashMap.get("loja_origem")).getAccumulatedVolume();
            volumeTotalBank = Double.parseDouble(map__.get("volume_total_pecas").toString());
            valorTotalBank = Double.valueOf(map__.get("valor_total_retido").toString());
            String colunaValorDivergencia = "", valorValorDivergencia = "", valorValorConciliado = "", colunaValorConciliado = "";
            String colunaVolumeConciliado = "", valorVolumeConciliado = "", colunaVolumeDivergencia = "", valorVolumeDivergencia = "";

            for (String s: meuHashMap.keySet()) {
                returnMap2.put(s, meuHashMap.get(s));
            }

            if (!valorTotalBank.equals(valorTotal)){
                colunaValorConciliado = "Valor conciliado";
                valorValorConciliado = "Valor divergente";
                colunaValorDivergencia = "Divergência de valor";
                Double finalValor = valorTotalBank - valorTotal;
                valorValorDivergencia = "R$ " + String.format("%.2f", Math.abs(finalValor));

            } else{
                colunaValorConciliado = "Valor conciliado";
                valorValorConciliado = "OK";
                colunaValorDivergencia = "Divergência de valor";
                valorValorDivergencia = "-";

            }
            if (volumeTotalBank == volumeTotal){
                colunaVolumeConciliado = "Volume conciliado";
                valorVolumeConciliado = "OK";
                colunaVolumeDivergencia = "Divergência de volume";
                valorVolumeDivergencia = "-";

            } else{
                Double finalVolumn;
                colunaVolumeConciliado = "Volume conciliado";
                valorVolumeConciliado = "Volume divergente";
                colunaVolumeDivergencia = "Divergência de volume";
                finalVolumn = volumeTotalBank - volumeTotal;
                valorVolumeDivergencia = finalVolumn.toString();
            }

            returnMap2.put(colunaVolumeConciliado, valorVolumeConciliado);
            returnMap2.put(colunaVolumeDivergencia, valorVolumeDivergencia);
            returnMap2.put(colunaValorConciliado, valorValorConciliado);
            returnMap2.put(colunaValorDivergencia, valorValorDivergencia);

            String line = returnMap2.get("loja_origem") + ";" + valorTotal + ";" + returnMap2.get("atraso_medio_dias") + ";" + returnMap2.get("unidade_auditora") + ";" + returnMap2.get("volume_total_pecas")  + ";" + returnMap2.get("Volume conciliado") + ";" + returnMap2.get("Divergência de volume") + ";" + returnMap2.get("Valor conciliado") + ";" + returnMap2.get("Divergência de valor") + "\n";
            csv.append(line);

            returnMap.add(returnMap2);
            System.out.println("valorTotal: " + valorTotal + "valorTotalBank: " + valorTotalBank);
        }
        ///System.out.println(csv);
        return csv;
    }
}
