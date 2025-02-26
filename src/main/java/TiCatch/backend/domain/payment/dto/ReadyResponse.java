package TiCatch.backend.domain.payment.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReadyResponse {
    private String tid;
    private String next_redirect_pc_url;
}