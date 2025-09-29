package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ClientEstimateItemResponseDto {
    private Integer id;
    private ClientPositionResponseDto position;
    private BigDecimal quantity;
    private BigDecimal total; // Final calculated total for the client
}