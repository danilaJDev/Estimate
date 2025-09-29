package cyfr.ae.estimate.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProjectResponseDto {
    private Integer id;
    private String name;
    private String address;
    private String clientUsername;
    private LocalDate creationDate;
    private String status;
    private String projectType;
    private List<ClientEstimateResponseDto> estimates;
}