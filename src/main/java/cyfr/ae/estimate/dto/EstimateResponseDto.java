package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EstimateResponseDto {
    private Integer id;
    private Integer projectId;
    private String projectName;
    private String estimatorUsername;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private String status;

    private BigDecimal overallCoefficient;
    private boolean applyVat;
    private BigDecimal vatRate;
    private boolean applyMarkup;
    private BigDecimal markupValue;

    private List<EstimateItemResponseDto> items;

    // Calculated fields
    private BigDecimal subtotal; // Сумма всех позиций до общего коэффициента и налогов
    private BigDecimal totalWithOverallCoefficient; // Подитог * общий коэффициент
    private BigDecimal vatAmount; // Сумма НДС
    private BigDecimal markupAmount; // Сумма наценки
    private BigDecimal finalTotal; // Итоговая стоимость сметы
}