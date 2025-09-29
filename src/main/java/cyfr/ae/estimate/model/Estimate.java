package cyfr.ae.estimate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estimates")
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimator_id", nullable = false)
    private User estimator;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    private String status; // "Черновик", "На согласование", "Согласована", "Архив"

    @Column(precision = 10, scale = 4)
    private BigDecimal overallCoefficient; // Общий коэффициент

    private boolean applyVat; // Применять НДС

    @Column(precision = 10, scale = 2)
    private BigDecimal vatRate; // Ставка НДС

    private boolean applyMarkup; // Применять наценку

    @Column(precision = 10, scale = 2)
    private BigDecimal markupValue; // Значение наценки

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateItem> items;
}