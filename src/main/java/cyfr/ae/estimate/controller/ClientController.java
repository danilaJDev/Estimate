package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.ClientEstimateResponseDto;
import cyfr.ae.estimate.dto.ProjectResponseDto;
import cyfr.ae.estimate.service.EstimateService;
import cyfr.ae.estimate.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    private final EstimateService estimateService;
    private final ProjectService projectService;

    @GetMapping("/estimates/{id}")
    public ResponseEntity<ClientEstimateResponseDto> getEstimateById(@PathVariable Integer id) {
        // In a real application, the service layer would need to verify
        // that the authenticated client is the owner of this estimate.
        return ResponseEntity.ok(estimateService.getEstimateForClient(id));
    }

    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectResponseDto>> getProjectsForCurrentUser(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(projectService.getProjectsByClient(userDetails.getUsername(), pageable));
    }
}