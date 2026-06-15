package dev.alex.standardizer.web.dto.request;

import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContentRequestDto {
    String role;
    List<PartRequestDto> parts;
}
