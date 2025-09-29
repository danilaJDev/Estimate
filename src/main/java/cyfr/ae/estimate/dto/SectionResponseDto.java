package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SectionResponseDto {
    private Integer id;
    private String name;
    private List<SubsectionResponseDto> subsections;
}