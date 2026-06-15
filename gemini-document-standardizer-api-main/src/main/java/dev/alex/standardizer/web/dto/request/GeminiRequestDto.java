package dev.alex.standardizer.web.dto.request;

import java.util.List;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeminiRequestDto {
    List<ContentRequestDto> contents;
    GenerationConfigRequestDto generationConfig;
}
