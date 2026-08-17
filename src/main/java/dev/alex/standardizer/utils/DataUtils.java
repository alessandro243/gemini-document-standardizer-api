package dev.alex.standardizer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DataUtils {
    private final ArrayList<Map<Boolean, Consumer<String>>> op = new ArrayList<>();
    private Map<String, String> finalMap;
    boolean flag = true;
    private ArrayList<String> divergenceList = new ArrayList<>();
    private StringBuilder stringBuilder = new StringBuilder();

    public StringBuilder processar() {
        for (Map<Boolean, Consumer<String>> mapa : op) {
            if (mapa.containsKey(true)) {
                mapa.get(true).accept(null);
            } else if (mapa.containsKey(false)) {
                mapa.get(false).accept(null);
            }
        }

        if (!divergenceList.isEmpty()){
            finalMap.put("Conciliado", "Divergente");
            finalMap.put("Divergência", divergenceList.getFirst());
        }

        stringBuilder.append(finalMap.get("Conciliado")).append(",");
        stringBuilder.append(finalMap.get("Divergência"));
        stringBuilder.append("\n");
        divergenceList.clear();
        return stringBuilder;
    }

    public void validoCodProdTrue(String register, String codProd_, Map<String, String> returnMap){
        String codProd = codProd_.replaceAll("\\D", "");
        if (!codProd.equals(register)){
            divergenceList.add("Produto não encontrado: " + codProd);
        }
        returnMap.put("Código", codProd);
        this.stringBuilder.append(codProd).append(",");
    }

    public void validoVolumeTrue(String register, String volume_, Map<String, String> returnMap){
        String volume = volume_.replaceAll("\\D", "");
        if (!volume.equals(register)){
            divergenceList.add("Quantidade: " + (Integer.parseInt(volume) - Integer.parseInt(register)));
        }
        returnMap.put("Quantidade", volume);
        this.stringBuilder.append(volume).append(",");
    }

    public void validoNumNotaDestTrue(String register, String numNota, Map<String, String> returnMap){
        if (!numNota.equals(register)){
            divergenceList.add("Nota divergente: " + numNota);
        }
        returnMap.put("N° Nota Fiscal", numNota);
        this.stringBuilder.append(numNota).append(",");
    }

    public void validolojaDestTrue(String register,String lojaDest, Map<String, String> returnMap){
        if (!lojaDest.equals(register)){
            divergenceList.add("Loja divergente: " + lojaDest);
        }
        returnMap.put("Loja Destino", lojaDest);
        this.stringBuilder.append(lojaDest).append(",");
    }

    public void validoQtdDiasTrue(String register, String qtdDias, Map<String, String> returnMap){
        if (!qtdDias.equals(register)){
            divergenceList.add(String.format("Quantidade: %.2f", (Double.parseDouble(qtdDias) - Double.parseDouble(register))));
        }
        returnMap.put("Qtd Dias", qtdDias);
        this.stringBuilder.append(qtdDias).append(",");
    }

    public void validoNatOpTrue(String register, String natOp_, Map<String, String> returnMap){
        String natOp = natOp_.replaceAll("^(\\d+)\\s*[:|\\-]?\\s*", "$1 - ");

        if (!natOp.equals(register)){
            divergenceList.add("Natureza divergente: " + natOp);
        }
        returnMap.put("Natureza", natOp);
        this.stringBuilder.append(natOp).append(",");
    }

    public void validoProdutoTrue(String register, String produto, Map<String, String> returnMap){
        if (!produto.equals(register)){
            divergenceList.add("Produto não encontrado: " + produto);
        }
        returnMap.put("Descrição do Produto", produto);
        this.stringBuilder.append(produto).append(",");
    }

    public void validoDataTrue(String register_, String data_, Map<String, String> returnMap){
        String data = DateUtils.padronizarData(data_);
        String register = DateUtils.padronizarData(register_);

        if (!data.equals(register)){
            divergenceList.add("Data divergente: " + data);
        }
        returnMap.put("Data de Emissão", data);
        this.stringBuilder.append(data).append(",");
    }

    public void validoLojaOrigemTrue(String register, String lojaOrigem, Map<String, String> returnMap){
        if (!lojaOrigem.equals(register)){
            divergenceList.add("Loja origem divergente: " + lojaOrigem);
        }
        returnMap.put("Loja Origem", lojaOrigem);
        this.stringBuilder.append(lojaOrigem).append(",");
    }

    public void validoValorBrutoTrue(String register, String valorBruto_, Map<String, String> returnMap){
        String monetaryValue = valorBruto_.replaceAll("[^0-9,.]", "");
        String cleanValue = monetaryValue.replaceAll("\\.(?=.*\\.)", "");

        if (!cleanValue.equals(register)){
            divergenceList.add(String.format("Valor unitário: R$ %.2f", (Double.parseDouble(cleanValue) - Double.parseDouble(register))));
        }
        returnMap.put("Valor Bruto", cleanValue);
        this.stringBuilder.append(cleanValue).append(",");
    }

    public void validoValorTotalTrue(String register, String valorTotal_, Map<String, String> returnMap){
        String monetaryValue = valorTotal_.replaceAll("[^0-9,.]", "");
        String cleanValue = monetaryValue.replaceAll("\\.(?=.*\\.)", "");
        if (!cleanValue.equals(register)){
            divergenceList.add("Total Produto: R$" + (Double.parseDouble(cleanValue) - Double.parseDouble(register)));
        }
        returnMap.put("Valor Total Produto", cleanValue);
        this.stringBuilder.append(cleanValue).append(",");
    }

    public void validoValorNotaTrue(String register, String valorNota_, Map<String, String> returnMap){
        String monetaryValue = valorNota_.replaceAll("[^0-9,.]", "");
        String cleanValue = monetaryValue.replaceAll("\\.(?=.*\\.)", "");
        if (!cleanValue.equals(register)){
            divergenceList.add(String.format("Valor da nota: R$ %.2f", (Double.parseDouble(cleanValue) - Double.parseDouble(register))));
        }
        returnMap.put("Valor da Nota", cleanValue);
        this.stringBuilder.append(cleanValue).append(",");
    }

    public void buildFinalMap(
            Map<String, Object> register, String valorBruto, String valorTotal, String valorNota, String lojaOrigem,
            String data, String produto, String natOp, String qtdDias, String lojaDest, String numNota, String volume,
            String codProd
    )
    {
        this.finalMap = new HashMap<>();
        this.finalMap.put("Loja Origem", null);
        this.finalMap.put("Loja Destino", null);
        this.finalMap.put("Data de Emissão", null);
        this.finalMap.put("N° Nota Fiscal", null);
        this.finalMap.put("Qtd Dias", null);
        this.finalMap.put("Quantidade", null);
        this.finalMap.put("Código", null);
        this.finalMap.put("Descrição do Produto", null);
        this.finalMap.put("Valor Bruto", null);
        this.finalMap.put("Valor Total Produto", null);
        this.finalMap.put("Valor da Nota", null);
        this.finalMap.put("Natureza", null);
        this.finalMap.put("Conciliado", "OK");
        this.finalMap.put("Divergência", "-");
        dataConfig(register, valorBruto, valorTotal, valorNota, lojaOrigem, data, produto, natOp, qtdDias, lojaDest, numNota, volume, codProd);
    }

    public void dataConfig(
            Map<String, Object> register, String valorBruto, String valorTotal, String valorNota, String lojaOrigem,
            String data, String produto, String natOp, String qtdDias, String lojaDest, String numNota, String volume,
            String codProd
    )
    {
        Map<Boolean, Consumer<String>> lojaDestMap = new HashMap<>();
        Map<Boolean, Consumer<String>> volumeMap = new HashMap<>();
        Map<Boolean, Consumer<String>> qtdDiasMap = new HashMap<>();
        Map<Boolean, Consumer<String>> natOpMap = new HashMap<>();
        Map<Boolean, Consumer<String>> produtoMap = new HashMap<>();
        Map<Boolean, Consumer<String>> dataMap = new HashMap<>();
        Map<Boolean, Consumer<String>> lojaOrigemMap = new HashMap<>();
        Map<Boolean, Consumer<String>> valorBrutoMap = new HashMap<>();
        Map<Boolean, Consumer<String>> valorTotalMap = new HashMap<>();
        Map<Boolean, Consumer<String>> valorNotaMap = new HashMap<>();
        Map<Boolean, Consumer<String>> numNotaMap = new HashMap<>();
        Map<Boolean, Consumer<String>> codProdMap = new HashMap<>();

        valorBrutoMap.put(true, valor -> validoValorBrutoTrue(register.get("valor_bruto").toString(), valorBruto, finalMap));
        valorTotalMap.put(true, valor -> validoValorTotalTrue(register.get("valor_total_produto").toString(), valorTotal, finalMap));
        valorNotaMap.put(true, valor -> validoValorNotaTrue(register.get("valor_nota").toString(), valorNota, finalMap));
        lojaOrigemMap.put(true, valor -> validoLojaOrigemTrue(register.get("loja_origem").toString(), lojaOrigem, finalMap));
        dataMap.put(true, valor -> validoDataTrue(register.get("data_emissao").toString(), data, finalMap));
        produtoMap.put(true, valor -> validoProdutoTrue(register.get("descricao_produto").toString(), produto, finalMap));
        natOpMap.put(true, valor -> validoNatOpTrue(register.get("natureza_operacao").toString(), natOp, finalMap));
        qtdDiasMap.put(true, valor -> validoQtdDiasTrue(register.get("qtd_dias").toString(), qtdDias, finalMap));
        lojaDestMap.put(true, valor -> validolojaDestTrue(register.get("loja_destino").toString(), lojaDest, finalMap));
        numNotaMap.put(true, valor -> validoNumNotaDestTrue(register.get("num_nota_fiscal").toString(), numNota, finalMap));
        volumeMap.put(true, valor -> validoVolumeTrue(register.get("quantidade").toString(), volume, finalMap));
        codProdMap.put(true, valor -> validoCodProdTrue(register.get("codigo_produto").toString(), codProd, finalMap));

        op.add(valorNotaMap);
        op.add(valorTotalMap);
        op.add(valorBrutoMap);
        op.add(dataMap);
        op.add(qtdDiasMap);
        op.add(volumeMap);
        op.add(numNotaMap);
        op.add(codProdMap);
        op.add(lojaOrigemMap);
        op.add(lojaDestMap);
        op.add(natOpMap);
        op.add(produtoMap);
    }
}
