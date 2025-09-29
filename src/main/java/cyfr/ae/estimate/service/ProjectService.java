package cyfr.ae.estimate.service;

import cyfr.ae.estimate.dto.ClientEstimateResponseDto;
import cyfr.ae.estimate.dto.ProjectResponseDto;
import cyfr.ae.estimate.model.Project;
import cyfr.ae.estimate.model.User;
import cyfr.ae.estimate.repository.ProjectRepository;
import cyfr.ae.estimate.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EstimateService estimateService; // To reuse the DTO mapping logic

    @Transactional(readOnly = true)
    public Page<ProjectResponseDto> getProjectsByClient(String username, Pageable pageable) {
        User client = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        return projectRepository.findAllByClient(client, pageable)
                .map(this::toProjectResponseDto);
    }

    private ProjectResponseDto toProjectResponseDto(Project project) {
        List<ClientEstimateResponseDto> estimateDtos = project.getEstimates().stream()
                .map(estimateService::getEstimateForClient) // Reusing the client-specific DTO conversion
                .collect(Collectors.toList());

        return ProjectResponseDto.builder()
                .id(project.getId())
                .name(project.getName())
                .address(project.getAddress())
                .clientUsername(project.getClient().getUsername())
                .creationDate(project.getCreationDate())
                .status(project.getStatus())
                .projectType(project.getProjectType())
                .estimates(estimateDtos)
                .build();
    }
}