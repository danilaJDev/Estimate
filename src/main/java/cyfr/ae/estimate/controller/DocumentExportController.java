package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.EstimateResponseDto;
import cyfr.ae.estimate.service.DocumentGenerationService;
import cyfr.ae.estimate.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/v1/estimates/{id}/export")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('ESTIMATOR') or hasRole('CLIENT')")
public class DocumentExportController {

    private final DocumentGenerationService documentGenerationService;
    private final EstimateService estimateService;

    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> exportToPdf(@PathVariable Integer id) {
        try {
            EstimateResponseDto estimate = estimateService.getEstimateById(id);
            ByteArrayInputStream bis = documentGenerationService.generatePdf(estimate);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=estimate-" + id + ".pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/docx")
    public ResponseEntity<InputStreamResource> exportToDocx(@PathVariable Integer id) {
        try {
            EstimateResponseDto estimate = estimateService.getEstimateById(id);
            ByteArrayInputStream bis = documentGenerationService.generateDocx(estimate);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estimate-" + id + ".docx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/xlsx")
    public ResponseEntity<InputStreamResource> exportToXlsx(@PathVariable Integer id) {
        try {
            EstimateResponseDto estimate = estimateService.getEstimateById(id);
            ByteArrayInputStream bis = documentGenerationService.generateXlsx(estimate);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=estimate-" + id + ".xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            // Log the exception
            return ResponseEntity.internalServerError().build();
        }
    }
}