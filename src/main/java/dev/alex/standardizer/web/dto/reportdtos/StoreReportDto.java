package dev.alex.standardizer.web.dto.reportdtos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class StoreReportDto {
    private Integer accumulatedVolume = 0;
    private Double accumulatedValue = 0.0;

    public void increaseTotalPecas(Integer value){
        accumulatedVolume += value;
    }

    public void increaseTotalRetido(Double value){
        accumulatedValue = accumulatedValue + value;
    }
}
