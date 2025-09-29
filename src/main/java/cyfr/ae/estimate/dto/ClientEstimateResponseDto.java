package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClientEstimateResponseDto {
    private Integer id;
    private String projectName;
    private LocalDateTime creationDate;
    private String status;

    private List<ClientEstimateItemResponseDto> items;

    // Final calculated values for the client
    private BigDecimal subtotal; // Sum of all item totals
    private BigDecimal vatAmount;
    private BigDecimal markupAmount;
    private BigDecimal finalTotal;
}