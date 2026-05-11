package dev.alex.standardizer.web.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GeminiResponsedto {
    List<CandidatesResponsedto> candidates;
}
