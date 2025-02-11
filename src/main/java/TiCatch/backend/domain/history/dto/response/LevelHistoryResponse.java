package TiCatch.backend.domain.history.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LevelHistoryResponse {
    private Long easyCount;
    private Long normalCount;
    private Long hardCount;
}
