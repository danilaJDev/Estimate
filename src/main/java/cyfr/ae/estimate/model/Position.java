package cyfr.ae.estimate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit; // "м2", "п.м.", "шт"

    @Column(nullable = false)
    private BigDecimal costPrice; // Стоимость за ед. (для сметчика)

    @Column(nullable = false)
    private BigDecimal customerPrice; // Стоимость для заказчика

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsection_id", nullable = false)
    private Subsection subsection;
}