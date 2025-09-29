package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ClientPositionResponseDto {
    private Integer id;
    private String name;
    private String unit;
    private BigDecimal customerPrice; // Renamed to "Стоимость" for client view
    private String description;
}