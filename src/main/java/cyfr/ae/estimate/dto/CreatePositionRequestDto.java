package cyfr.ae.estimate.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePositionRequestDto {
    private String name;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal customerPrice;
    private String description;
    private Integer subsectionId;
}