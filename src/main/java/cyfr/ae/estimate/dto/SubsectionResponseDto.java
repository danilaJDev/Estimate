package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SubsectionResponseDto {
    private Integer id;
    private String name;
    private List<PositionResponseDto> positions;
}