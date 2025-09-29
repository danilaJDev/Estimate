package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.CreateEstimateRequestDto;
import cyfr.ae.estimate.dto.EstimateResponseDto;
import cyfr.ae.estimate.dto.UpdateStatusRequestDto;
import cyfr.ae.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    @PostMapping
    @PreAuthorize("hasRole('ESTIMATOR')")
    public ResponseEntity<EstimateResponseDto> createEstimate(@RequestBody CreateEstimateRequestDto request) {
        return new ResponseEntity<>(estimateService.createEstimate(request), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ESTIMATOR')")
    public ResponseEntity<Page<EstimateResponseDto>> getEstimatesForCurrentUser(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(estimateService.getEstimatesByEstimator(userDetails.getUsername(), pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ESTIMATOR') or hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<EstimateResponseDto> getEstimateById(@PathVariable Integer id) {
        // Note: Further logic will be needed to ensure clients can only see their own estimates.
        // This will be handled in the client-specific controller or by refining the service method.
        return ResponseEntity.ok(estimateService.getEstimateById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ESTIMATOR')")
    public ResponseEntity<EstimateResponseDto> updateEstimate(@PathVariable Integer id, @RequestBody CreateEstimateRequestDto request) {
        return ResponseEntity.ok(estimateService.updateEstimate(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ESTIMATOR')")
    public ResponseEntity<EstimateResponseDto> updateEstimateStatus(@PathVariable Integer id, @RequestBody UpdateStatusRequestDto request) {
        return ResponseEntity.ok(estimateService.updateEstimateStatus(id, request));
    }
}