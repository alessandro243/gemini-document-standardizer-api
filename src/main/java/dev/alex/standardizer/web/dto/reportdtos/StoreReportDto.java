package dev.alex.standardizer.web.dto.reportdtos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class StoreReportDto {
    private Integer accumulatedVolume = 0;
    private Double accumulatedValue = 0.0;
    private String notaFiscal;
    private String lojaOrigem;
    private String volum;
    private String valor;
    private String data;
    private String prod;
    private String codProd;
    private String natOp;
    private String valorBruto;
    private String qtdDias;
    private String valorTotalP;
    private String lojaDest;

    public void increaseTotalPecas(Integer value){
        accumulatedVolume += value;
    }

    public void increaseTotalRetido(Double value){
        accumulatedValue = accumulatedValue + value;
    }
}
