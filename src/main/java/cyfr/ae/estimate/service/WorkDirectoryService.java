package cyfr.ae.estimate.service;

import cyfr.ae.estimate.dto.*;
import cyfr.ae.estimate.model.Position;
import cyfr.ae.estimate.model.Section;
import cyfr.ae.estimate.model.Subsection;
import cyfr.ae.estimate.repository.PositionRepository;
import cyfr.ae.estimate.repository.SectionRepository;
import cyfr.ae.estimate.repository.SubsectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkDirectoryService {

    private final SectionRepository sectionRepository;
    private final SubsectionRepository subsectionRepository;
    private final PositionRepository positionRepository;

    // CREATE operations

    @Transactional
    public SectionResponseDto createSection(CreateSectionRequestDto request) {
        Section section = Section.builder()
                .name(request.getName())
                .subsections(Collections.emptyList())
                .build();
        section = sectionRepository.save(section);
        return toSectionDto(section);
    }

    @Transactional
    public SubsectionResponseDto createSubsection(CreateSubsectionRequestDto request) {
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new EntityNotFoundException("Section not found with id: " + request.getSectionId()));

        Subsection subsection = Subsection.builder()
                .name(request.getName())
                .section(section)
                .positions(Collections.emptyList())
                .build();
        subsection = subsectionRepository.save(subsection);
        return toSubsectionDto(subsection);
    }

    @Transactional
    public PositionResponseDto createPosition(CreatePositionRequestDto request) {
        Subsection subsection = subsectionRepository.findById(request.getSubsectionId())
                .orElseThrow(() -> new EntityNotFoundException("Subsection not found with id: " + request.getSubsectionId()));

        Position position = Position.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .costPrice(request.getCostPrice())
                .customerPrice(request.getCustomerPrice())
                .description(request.getDescription())
                .subsection(subsection)
                .build();
        position = positionRepository.save(position);
        return toPositionDto(position);
    }

    // READ operations

    @Transactional(readOnly = true)
    public List<SectionResponseDto> getAllSections() {
        return sectionRepository.findAll().stream()
                .map(this::toSectionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SectionResponseDto getSectionById(Integer id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section not found with id: " + id));
        return toSectionDto(section);
    }

    // UPDATE operations

    @Transactional
    public SectionResponseDto updateSection(Integer id, CreateSectionRequestDto request) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section not found with id: " + id));
        section.setName(request.getName());
        section = sectionRepository.save(section);
        return toSectionDto(section);
    }

    @Transactional
    public SubsectionResponseDto updateSubsection(Integer id, CreateSubsectionRequestDto request) {
        Subsection subsection = subsectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subsection not found with id: " + id));
        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new EntityNotFoundException("Section not found with id: " + request.getSectionId()));

        subsection.setName(request.getName());
        subsection.setSection(section);
        subsection = subsectionRepository.save(subsection);
        return toSubsectionDto(subsection);
    }

    @Transactional
    public PositionResponseDto updatePosition(Integer id, CreatePositionRequestDto request) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + id));
        Subsection subsection = subsectionRepository.findById(request.getSubsectionId())
                .orElseThrow(() -> new EntityNotFoundException("Subsection not found with id: " + request.getSubsectionId()));

        position.setName(request.getName());
        position.setUnit(request.getUnit());
        position.setCostPrice(request.getCostPrice());
        position.setCustomerPrice(request.getCustomerPrice());
        position.setDescription(request.getDescription());
        position.setSubsection(subsection);
        position = positionRepository.save(position);
        return toPositionDto(position);
    }

    // DELETE operations

    @Transactional
    public void deleteSection(Integer id) {
        if (!sectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Section not found with id: " + id);
        }
        sectionRepository.deleteById(id);
    }

    @Transactional
    public void deleteSubsection(Integer id) {
        if (!subsectionRepository.existsById(id)) {
            throw new EntityNotFoundException("Subsection not found with id: " + id);
        }
        subsectionRepository.deleteById(id);
    }

    @Transactional
    public void deletePosition(Integer id) {
        if (!positionRepository.existsById(id)) {
            throw new EntityNotFoundException("Position not found with id: " + id);
        }
        positionRepository.deleteById(id);
    }


    // Mappers

    private SectionResponseDto toSectionDto(Section section) {
        return SectionResponseDto.builder()
                .id(section.getId())
                .name(section.getName())
                .subsections(section.getSubsections() != null ?
                        section.getSubsections().stream().map(this::toSubsectionDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    private SubsectionResponseDto toSubsectionDto(Subsection subsection) {
        return SubsectionResponseDto.builder()
                .id(subsection.getId())
                .name(subsection.getName())
                .positions(subsection.getPositions() != null ?
                        subsection.getPositions().stream().map(this::toPositionDto).collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }

    public PositionResponseDto toPositionDto(Position position) {
        return PositionResponseDto.builder()
                .id(position.getId())
                .name(position.getName())
                .unit(position.getUnit())
                .costPrice(position.getCostPrice())
                .customerPrice(position.getCustomerPrice())
                .description(position.getDescription())
                .build();
    }
}