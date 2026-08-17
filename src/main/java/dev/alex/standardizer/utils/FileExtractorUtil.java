package dev.alex.standardizer.utils;

import dev.alex.standardizer.TestMap;
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
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class FileExtractorUtil {
    public Boolean isEmptyLine(String line){
        if(line.trim().isEmpty()){
            return true;
        }
        return false;
    }

    public String containMonetary(String line){
        if (line.contains("R$")) {
            line = line.replaceAll("(R\\$\\s?[\\d.]+),(\\d{2})", "$1.$2");
            line = line.replaceAll(",", ";");
            ///line = line.replace("R$ ", "");
        }
        return line;
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

    public StringBuilder testando(ArrayList<String> splitedLine){
        return new StringBuilder();
    }

    public StringBuilder parseRows(Map<String,String> geminiResponseMap, MultipartFile file, Db_Utils database){
        int idxNota = 0, idxLoja = 0, idxVol = 0, idxValor = 0, idxData = 0, idxProd = 0, idxLojaDest = 0, idxNatOp = 0, idxQtdDias = 0, idxCodProd = 0, idxVBrut = 0, idxTotalProd = 0;
        String reportId = geminiResponseMap.get("id_relatorio");
        String line;
        boolean firstLine = true;
        StringBuilder resultado = new StringBuilder();
        Map<String, String> finalMap = new HashMap<>();
        ArrayList<String> columns = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
            while ((line = br.readLine()) != null){

                if (isEmptyLine(line)){
                    continue;
                }

                if (firstLine){

                    String separator = separatorDetector(line);
                    columns = removeQuotes(line, separator);

                    idxLoja = columns.indexOf(geminiResponseMap.get("loja_origem"));
                    idxLojaDest = columns.indexOf(geminiResponseMap.get("loja_destino"));
                    idxData = columns.indexOf(geminiResponseMap.get("data"));
                    idxNatOp = columns.indexOf(geminiResponseMap.get("natureza_operacao"));
                    idxQtdDias = columns.indexOf(geminiResponseMap.get("qtd_dias"));
                    idxVol = columns.indexOf(geminiResponseMap.get("volume"));
                    idxCodProd = columns.indexOf(geminiResponseMap.get("codigo_produto"));
                    idxProd = columns.indexOf(geminiResponseMap.get("produto"));
                    idxVBrut = columns.indexOf(geminiResponseMap.get("valor_bruto"));
                    idxTotalProd = columns.indexOf(geminiResponseMap.get("valor_total_produto"));
                    idxValor = columns.indexOf(geminiResponseMap.get("valor_declarado"));
                    idxNota = columns.indexOf(geminiResponseMap.get("nota_fiscal"));

                    firstLine = false;
                    continue;
                }

                line = containMonetary(line);
                String separator = separatorDetector(line);
                ArrayList<String> splitedLine = removeQuotes(line, separator);

                String lojaOrigem = splitedLine.get(idxLoja).trim();
                String data = splitedLine.get(idxData).trim();
                String produto = splitedLine.get(idxProd).trim();
                String codProd = splitedLine.get(idxCodProd).trim();
                String natOp = splitedLine.get(idxNatOp).trim();
                String vBruto = splitedLine.get(idxVBrut).trim();
                String qtdDias = splitedLine.get(idxQtdDias).trim();
                String totalProd = splitedLine.get(idxTotalProd).trim();
                String lojaDest = splitedLine.get(idxLojaDest).trim();
                String numNota = splitedLine.get(idxNota).trim();
                String volume = splitedLine.get(idxVol).trim();
                String valorNota = splitedLine.get(idxValor).trim();

                String monetaryValorBruto = vBruto.replaceAll("[^0-9,.]", "");
                String cleanValorBruto = monetaryValorBruto.replaceAll("\\.(?=.*\\.)", "");

                String monetaryTotalProd = totalProd.replaceAll("[^0-9,.]", "");
                String cleanTotalProd = monetaryTotalProd.replaceAll("\\.(?=.*\\.)", "");

                String monetaryValueNota = valorNota.replaceAll("[^0-9,.]", "");
                String cleanValueNota = monetaryValueNota.replaceAll("\\.(?=.*\\.)", "");

                if (!numNota.contains("-")){
                    numNota = numNota.replaceAll("(.+)(\\d)", "$1-$2");

                    ///System.out.println("Entrei: " + numNota);
                }

                List<Map<String, Object>> bru = database.selectRowsReport(database, codProd, numNota);

                if (bru.size() < 1){
                    ///System.out.println("Vazio");
                    continue;
                }

                Map<String, Object> register = bru.get(0);
                TestMap testMap = new TestMap();
                testMap.buildFinalMap(register, cleanValorBruto, cleanTotalProd, cleanValueNota, lojaOrigem, data, produto, natOp, qtdDias, lojaDest, numNota, volume, codProd);
                String retorno = testMap.processar().toString();
                System.out.println(retorno);
            }

        }catch (IOException e){

        }

        return resultado;
    }

    public StringBuilder parseCsvContent(Map<String, String> geminiResponseMap, MultipartFile filePath, Db_Utils database) {
        Map<String, StoreReportDto> finalStoreReport = new HashMap<>();
        String line;
        boolean firstLine = true;
        Set<String> processedNotes = new HashSet<>();
        int idxNota = 0, idxLoja = 0, idxVol = 0, idxValor = 0, idxData = 0, idxProd = 0, idxLojaDest = 0, idxNatOp = 0, idxQtdDias = 0, idxCodProd = 0, idxVBrut = 0, idxTotalProd = 0;
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
                    idxCodProd = columns.indexOf(geminiResponseMap.get("codigo_produto").trim());
                    idxNatOp = columns.indexOf(geminiResponseMap.get("natureza_operacao").trim());
                    idxVBrut = columns.indexOf(geminiResponseMap.get("valor_bruto").trim());
                    idxQtdDias = columns.indexOf(geminiResponseMap.get("qtd_dias"));
                    idxTotalProd = columns.indexOf(geminiResponseMap.get("valor_total_produto"));
                    idxLojaDest = columns.indexOf(geminiResponseMap.get("loja_destino").trim());

                    System.out.println(geminiResponseMap.get("valor_declarado").trim() + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    System.out.println(columns.get(idxValor) + "¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨");
                    firstLine = false;
                    continue;
                }

                line = containMonetary(line);

                System.out.println(line);
                String separator = separatorDetector(line);
                ArrayList<String> columns = removeQuotes(line, separator);

                String lojaOrigem = columns.get(idxLoja).trim();
                String data = columns.get(idxData).trim();
                String produto = columns.get(idxProd).trim();
                String codProd = columns.get(idxCodProd).trim();
                String natOp = columns.get(idxNatOp).trim();
                String vBruto = columns.get(idxVBrut).trim();
                String qtdDias = columns.get(idxQtdDias).trim();
                String totalProd = columns.get(idxTotalProd).trim();
                String lojaDest = columns.get(idxLojaDest).trim();
                String numNota = columns.get(idxNota).trim();
                String valorVolume = columns.get(idxVol).trim();
                String storeName = columns.get(idxLoja).trim();
                int volum = Integer.parseInt(valorVolume);
                String primeiramente = columns.get(idxValor).trim();
                String monetaryValue = primeiramente.replaceAll("[^0-9,.]", "");
                String cleanValue = monetaryValue.replaceAll("\\.(?=.*\\.)", "");
                Double decimalValue = Double.parseDouble(cleanValue);
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
        List<Map<String, Object>> data = database.selectFinalReport(database, reportId);
        List<String > geminiValues = new ArrayList<>(geminiResponseMap.values());
        StringBuilder finalReport = makeDivergenceDto(data, finalStoreReport);
        return finalReport;
    }

    public StringBuilder makeDivergenceDto(List<Map<String, Object>> data, Map<String, StoreReportDto> finalStore){
        System.out.println(finalStore + "Finaaaaaaaaaaaaaaaaaaaaaaaaaal");
        List<LinkedHashMap<String, Object>> returnMap = new ArrayList<>();
        Map<String, String>  keys_ = new HashMap<>();
        Double valorTotal = 0.0;
        int volumeTotal = 0;
        Double valorTotalBank = 0.0;
        Double volumeTotalBank = 0.0;
        StringBuilder csv = new StringBuilder();
        csv.append("loja_origem;valor_total_retido;atraso_medio_dias;unidade_auditora;volume_total_pecas;Volume conciliado;Divergencia de volume;Valor conciliado;Divergencia de valor" + "\n");
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
///finalStoreReport: {Filial Batel (PR)=StoreReportDto(accumulatedVolume=188, accumulatedValue=3840.2), Megastore Jardins (SP)=StoreReportDto(accumulatedVolume=5, accumulatedValue=38.52), Filial Floripa (SC)=StoreReportDto(accumulatedVolume=137, accumulatedValue=541.82), Filial Copacabana (RJ)=StoreReportDto(accumulatedVolume=34, accumulatedValue=187.94)}FinalStore<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<,,,
///meuHashMap, valores antés do cálculo de diferenças: {loja_origem=Filial Copacabana (RJ), valor_total_retido=187.94, atraso_medio_dias=28.0, unidade_auditora=Central Norte (Relatório II), volume_total_pecas=34}meuHashMap<<<<<<<<<<<<<<<<<<
