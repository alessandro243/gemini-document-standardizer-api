package dev.alex.standardizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestMap {
    private final ArrayList<Map<Boolean, Consumer<String>>> op = new ArrayList<>();
    private Map<String, String> finalMap;
    boolean flag = true;
    private ArrayList<String> divergenceList = new ArrayList<>();

    public Map<String, String> processar() {
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
        divergenceList.clear();
        return finalMap;
    }

    public void validoCodProdTrue(String codProd, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Produto não encontrado");
        }
        returnMap.put("Código", codProd);
    }

    public void validoVolumeTrue(String register, String volume, Map<String, String> returnMap, boolean cond){
        if (!cond){
            divergenceList.add("Quantidade: " + (Integer.parseInt(volume) - Integer.parseInt(register)));
        }
        returnMap.put("Quantidade", volume);
    }

    public void validoNumNotaDestTrue(String numNota, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Nota divergente");
        }
        returnMap.put("N° Nota Fiscal", numNota);
    }

    public void validolojaDestTrue(String lojaDest, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Loja divergente");
        }
        returnMap.put("Loja Destino", lojaDest);
    }

    public void validoQtdDiasTrue(String qtdDias, Map<String, String> returnMap, boolean b){
        returnMap.put("Qtd Dias", qtdDias);
    }

    public void validoNatOpTrue(String register, String natOp, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Natureza divergente");
        }
        returnMap.put("Natureza", natOp);
    }

    public void validoProdutoTrue(String produto, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Produto não encontrado");
        }
        returnMap.put("Descrição do Produto", produto);

    }

    public void validoDataTrue(String data, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Data divergente");
        }
        returnMap.put("Data de Emissão", data);
    }

    public void validoLojaOrigemTrue(String lojaOrigem, Map<String, String> returnMap, Boolean cond){
        if (!cond){
            divergenceList.add("Loja origem divergente");
        }
        returnMap.put("Loja Origem", lojaOrigem);
    }

    public void validoValorBrutoTrue(String register, String valorBruto, Map<String, String> returnMap, boolean b){
        returnMap.put("Valor Bruto", valorBruto);
    }

    public void validoValorTotalTrue(String register, String valorTotal, Map<String, String> returnMap, boolean cond){
        if (!cond){
            divergenceList.add("Total Produto: " + (Double.parseDouble(valorTotal) - Double.parseDouble(register)));
        }
        returnMap.put("Valor Total Produto", valorTotal);
    }

    public void validoValorNotaTrue(String register, String valorNota, Map<String, String> returnMap, boolean b){
        returnMap.put("Valor da Nota", valorNota);
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
        this.finalMap.put("Conciliado", null);
        this.finalMap.put("Divergência", null);
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

        // Configura as 12 condições e suas respectivas ações
        valorBrutoMap.put(true, valor -> validoValorBrutoTrue(register.get("valor_bruto").toString(), valorBruto, finalMap, valorBruto.equals(register.get("valor_bruto").toString())));
        valorTotalMap.put(true, valor -> validoValorTotalTrue(register.get("valor_total_produto").toString(), valorTotal, finalMap, valorTotal.equals(register.get("valor_total_produto").toString())));
        valorNotaMap.put(true, valor -> validoValorNotaTrue(register.get("valor_nota").toString(), valorNota, finalMap, valorNota.equals(register.get("valor_nota").toString())));
        lojaOrigemMap.put(true, valor -> validoLojaOrigemTrue(lojaOrigem, finalMap, lojaOrigem.equals(register.get("loja_origem").toString())));
        dataMap.put(true, valor -> validoDataTrue(data, finalMap, data.equals(register.get("data_emissao").toString())));
        produtoMap.put(true, valor -> validoProdutoTrue(produto, finalMap, produto.equals(register.get("descricao_produto").toString())));
        natOpMap.put(true, valor -> validoNatOpTrue(register.get("natureza_operacao").toString(), natOp, finalMap, natOp.equals(register.get("natureza_operacao").toString())));
        qtdDiasMap.put(true, valor -> validoQtdDiasTrue(qtdDias, finalMap, qtdDias.equals(register.get("qtd_dias").toString())));
        lojaDestMap.put(true, valor -> validolojaDestTrue(lojaDest, finalMap, lojaDest.equals(register.get("loja_destino").toString())));
        numNotaMap.put(true, valor -> validoNumNotaDestTrue(numNota, finalMap, numNota.equals(register.get("num_nota_fiscal").toString())));
        volumeMap.put(true, valor -> validoVolumeTrue(register.get("quantidade").toString(), volume, finalMap, numNota.equals(register.get("quantidade").toString())));
        codProdMap.put(true, valor -> validoCodProdTrue(codProd, finalMap, codProd.equals(register.get("codigo_produto").toString())));

        op.add(valorBrutoMap);
        op.add(valorTotalMap);
        op.add(valorNotaMap);
        op.add(lojaOrigemMap);
        op.add(dataMap);
        op.add(produtoMap);
        op.add(natOpMap);
        op.add(qtdDiasMap);
        op.add(lojaDestMap);
        op.add(numNotaMap);
        op.add(volumeMap);
        op.add(codProdMap);
    }
}
