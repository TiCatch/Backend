package TiCatch.backend.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApproveRequest {
    private String name;
    private int totalPrice;
}