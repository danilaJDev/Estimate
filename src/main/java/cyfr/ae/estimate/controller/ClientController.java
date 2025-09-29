package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.ClientEstimateResponseDto;
import cyfr.ae.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/estimates/{id}")
    public ResponseEntity<ClientEstimateResponseDto> getEstimateById(@PathVariable Integer id) {
        // In a real application, the service layer would need to verify
        // that the authenticated client is the owner of this estimate.
        return ResponseEntity.ok(estimateService.getEstimateForClient(id));
    }
}