package dev.alex.standardizer.web.dto.reportDtos;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class StoreReportDto {
    private Integer volumeTotalPecas = 0;
    BigDecimal valorTotalPedidos = BigDecimal.ZERO;

    public void increaseTotalPecas(Integer value){
        volumeTotalPecas += value;
    }

    public void increaseTotalRetido(BigDecimal value){
        valorTotalPedidos = valorTotalPedidos.add(value);
    }
}
