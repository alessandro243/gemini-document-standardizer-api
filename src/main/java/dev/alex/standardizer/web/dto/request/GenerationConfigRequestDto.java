package dev.alex.standardizer.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerationConfigRequestDto {
    Integer maxOutputTokens;
    Double temperature;
}
