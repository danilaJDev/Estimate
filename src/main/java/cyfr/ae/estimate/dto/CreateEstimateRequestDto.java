package cyfr.ae.estimate.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateEstimateRequestDto {
    private Integer projectId;
    private BigDecimal overallCoefficient;
    private boolean applyVat;
    private BigDecimal vatRate;
    private boolean applyMarkup;
    private BigDecimal markupValue;
    private List<CreateEstimateItemRequestDto> items;
}