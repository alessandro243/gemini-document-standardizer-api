package dev.alex.standardizer.web.dto.request;

import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContentRequestdto {
    String role;
    List<PartRequestdto> parts;
}
