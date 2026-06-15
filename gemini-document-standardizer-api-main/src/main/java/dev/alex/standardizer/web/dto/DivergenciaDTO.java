package dev.alex.standardizer.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DivergenciaDTO {
    private String loja;
    private String status;
    private String message;
}
