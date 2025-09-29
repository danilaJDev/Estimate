package cyfr.ae.estimate.controller;

import cyfr.ae.estimate.dto.*;
import cyfr.ae.estimate.service.WorkDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/work-directory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WorkDirectoryController {

    private final WorkDirectoryService workDirectoryService;

    // ============== Sections ==============

    @PostMapping("/sections")
    public ResponseEntity<SectionResponseDto> createSection(@RequestBody CreateSectionRequestDto request) {
        return new ResponseEntity<>(workDirectoryService.createSection(request), HttpStatus.CREATED);
    }

    @GetMapping("/sections")
    public ResponseEntity<List<SectionResponseDto>> getAllSections() {
        return ResponseEntity.ok(workDirectoryService.getAllSections());
    }

    @GetMapping("/sections/{id}")
    public ResponseEntity<SectionResponseDto> getSectionById(@PathVariable Integer id) {
        return ResponseEntity.ok(workDirectoryService.getSectionById(id));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<SectionResponseDto> updateSection(@PathVariable Integer id, @RequestBody CreateSectionRequestDto request) {
        return ResponseEntity.ok(workDirectoryService.updateSection(id, request));
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Integer id) {
        workDirectoryService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    // ============== Subsections ==============

    @PostMapping("/subsections")
    public ResponseEntity<SubsectionResponseDto> createSubsection(@RequestBody CreateSubsectionRequestDto request) {
        return new ResponseEntity<>(workDirectoryService.createSubsection(request), HttpStatus.CREATED);
    }

    @PutMapping("/subsections/{id}")
    public ResponseEntity<SubsectionResponseDto> updateSubsection(@PathVariable Integer id, @RequestBody CreateSubsectionRequestDto request) {
        return ResponseEntity.ok(workDirectoryService.updateSubsection(id, request));
    }

    @DeleteMapping("/subsections/{id}")
    public ResponseEntity<Void> deleteSubsection(@PathVariable Integer id) {
        workDirectoryService.deleteSubsection(id);
        return ResponseEntity.noContent().build();
    }

    // ============== Positions ==============

    @PostMapping("/positions")
    public ResponseEntity<PositionResponseDto> createPosition(@RequestBody CreatePositionRequestDto request) {
        return new ResponseEntity<>(workDirectoryService.createPosition(request), HttpStatus.CREATED);
    }

    @PutMapping("/positions/{id}")
    public ResponseEntity<PositionResponseDto> updatePosition(@PathVariable Integer id, @RequestBody CreatePositionRequestDto request) {
        return ResponseEntity.ok(workDirectoryService.updatePosition(id, request));
    }

    @DeleteMapping("/positions/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Integer id) {
        workDirectoryService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}