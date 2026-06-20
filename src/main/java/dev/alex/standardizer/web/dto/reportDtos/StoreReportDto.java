package dev.alex.standardizer.web.dto.reportDtos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class StoreReportDto {
    private Integer accumulatedVolume = 0;
    private BigDecimal accumulatedValue = BigDecimal.ZERO;

    public void increaseTotalPecas(Integer value){
        accumulatedVolume += value;
    }

    public void increaseTotalRetido(BigDecimal value){
        accumulatedValue = accumulatedValue.add(value);
    }
}
