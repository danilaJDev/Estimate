package cyfr.ae.estimate.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateEstimateItemRequestDto {
    private Integer positionId;
    private BigDecimal quantity;
    private BigDecimal localCoefficient;
}