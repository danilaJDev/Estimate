package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class EstimateItemResponseDto {
    private Integer id;
    private PositionResponseDto position;
    private BigDecimal quantity;
    private BigDecimal localCoefficient;
    private BigDecimal total; // Рассчитанная итоговая стоимость для этой позиции
}