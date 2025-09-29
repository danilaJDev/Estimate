package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PositionResponseDto {
    private Integer id;
    private String name;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal customerPrice;
    private String description;
}