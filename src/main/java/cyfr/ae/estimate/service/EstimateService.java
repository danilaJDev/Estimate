package cyfr.ae.estimate.service;

import cyfr.ae.estimate.dto.*;
import cyfr.ae.estimate.model.*;
import cyfr.ae.estimate.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final EstimateRepository estimateRepository;
    private final ProjectRepository projectRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final WorkDirectoryService workDirectoryService;

    @Transactional
    public EstimateResponseDto createEstimate(CreateEstimateRequestDto request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.getProjectId()));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User estimator = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Estimator not found with username: " + username));

        Estimate estimate = Estimate.builder()
                .project(project)
                .estimator(estimator)
                .creationDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .status("Черновик") // Draft
                .overallCoefficient(request.getOverallCoefficient())
                .applyVat(request.isApplyVat())
                .vatRate(request.getVatRate())
                .applyMarkup(request.isApplyMarkup())
                .markupValue(request.getMarkupValue())
                .build();

        List<EstimateItem> items = request.getItems().stream().map(itemDto -> {
            Position position = positionRepository.findById(itemDto.getPositionId())
                    .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + itemDto.getPositionId()));
            return EstimateItem.builder()
                    .estimate(estimate)
                    .position(position)
                    .quantity(itemDto.getQuantity())
                    .localCoefficient(itemDto.getLocalCoefficient())
                    .build();
        }).collect(Collectors.toList());

        estimate.setItems(items);
        Estimate savedEstimate = estimateRepository.save(estimate);

        return toEstimateResponseDto(savedEstimate);
    }

    @Transactional(readOnly = true)
    public EstimateResponseDto getEstimateById(Integer id) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found with id: " + id));
        return toEstimateResponseDto(estimate);
    }

    @Transactional
    public EstimateResponseDto updateEstimate(Integer id, CreateEstimateRequestDto request) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found with id: " + id));

        // For simplicity, we remove old items and add new ones.
        // A more sophisticated approach might involve matching and updating existing items.
        estimate.getItems().clear();
        List<EstimateItem> items = request.getItems().stream().map(itemDto -> {
            Position position = positionRepository.findById(itemDto.getPositionId())
                    .orElseThrow(() -> new EntityNotFoundException("Position not found with id: " + itemDto.getPositionId()));
            return EstimateItem.builder()
                    .estimate(estimate)
                    .position(position)
                    .quantity(itemDto.getQuantity())
                    .localCoefficient(itemDto.getLocalCoefficient())
                    .build();
        }).collect(Collectors.toList());
        estimate.getItems().addAll(items);

        estimate.setOverallCoefficient(request.getOverallCoefficient());
        estimate.setApplyVat(request.isApplyVat());
        estimate.setVatRate(request.getVatRate());
        estimate.setApplyMarkup(request.isApplyMarkup());
        estimate.setMarkupValue(request.getMarkupValue());
        estimate.setLastModifiedDate(LocalDateTime.now());

        Estimate savedEstimate = estimateRepository.save(estimate);
        return toEstimateResponseDto(savedEstimate);
    }

    @Transactional
    public EstimateResponseDto updateEstimateStatus(Integer id, UpdateStatusRequestDto request) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found with id: " + id));
        estimate.setStatus(request.getStatus());
        estimate.setLastModifiedDate(LocalDateTime.now());
        estimate = estimateRepository.save(estimate);
        return toEstimateResponseDto(estimate);
    }

    private EstimateResponseDto toEstimateResponseDto(Estimate estimate) {
        List<EstimateItemResponseDto> itemDtos = estimate.getItems().stream()
                .map(this::toEstimateItemResponseDto)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemDtos.stream()
                .map(EstimateItemResponseDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithOverallCoefficient = subtotal.multiply(
                estimate.getOverallCoefficient() != null ? estimate.getOverallCoefficient() : BigDecimal.ONE
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal vatAmount = BigDecimal.ZERO;
        if (estimate.isApplyVat() && estimate.getVatRate() != null) {
            vatAmount = totalWithOverallCoefficient.multiply(estimate.getVatRate().divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal markupAmount = BigDecimal.ZERO;
        if (estimate.isApplyMarkup() && estimate.getMarkupValue() != null) {
            markupAmount = totalWithOverallCoefficient.multiply(estimate.getMarkupValue().divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal finalTotal = totalWithOverallCoefficient.add(vatAmount).add(markupAmount);

        return EstimateResponseDto.builder()
                .id(estimate.getId())
                .projectId(estimate.getProject().getId())
                .projectName(estimate.getProject().getName())
                .estimatorUsername(estimate.getEstimator().getUsername())
                .creationDate(estimate.getCreationDate())
                .lastModifiedDate(estimate.getLastModifiedDate())
                .status(estimate.getStatus())
                .overallCoefficient(estimate.getOverallCoefficient())
                .applyVat(estimate.isApplyVat())
                .vatRate(estimate.getVatRate())
                .applyMarkup(estimate.isApplyMarkup())
                .markupValue(estimate.getMarkupValue())
                .items(itemDtos)
                .subtotal(subtotal)
                .totalWithOverallCoefficient(totalWithOverallCoefficient)
                .vatAmount(vatAmount)
                .markupAmount(markupAmount)
                .finalTotal(finalTotal)
                .build();
    }

    private EstimateItemResponseDto toEstimateItemResponseDto(EstimateItem item) {
        BigDecimal itemTotal = item.getPosition().getCustomerPrice()
                .multiply(item.getQuantity())
                .multiply(item.getLocalCoefficient() != null ? item.getLocalCoefficient() : BigDecimal.ONE)
                .setScale(2, RoundingMode.HALF_UP);

        return EstimateItemResponseDto.builder()
                .id(item.getId())
                .position(workDirectoryService.toPositionDto(item.getPosition()))
                .quantity(item.getQuantity())
                .localCoefficient(item.getLocalCoefficient())
                .total(itemTotal)
                .build();
    }

    // ============== Client-facing methods ==============

    @Transactional(readOnly = true)
    public ClientEstimateResponseDto getEstimateForClient(Integer id) {
        Estimate estimate = estimateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estimate not found with id: " + id));
        // Here you should add a security check to ensure the logged-in client owns this estimate
        return toClientEstimateResponseDto(estimate);
    }

    private ClientEstimateResponseDto toClientEstimateResponseDto(Estimate estimate) {
        List<ClientEstimateItemResponseDto> itemDtos = estimate.getItems().stream()
                .map(this::toClientEstimateItemResponseDto)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemDtos.stream()
                .map(ClientEstimateItemResponseDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithOverallCoefficient = subtotal.multiply(
                estimate.getOverallCoefficient() != null ? estimate.getOverallCoefficient() : BigDecimal.ONE
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal vatAmount = BigDecimal.ZERO;
        if (estimate.isApplyVat() && estimate.getVatRate() != null) {
            vatAmount = totalWithOverallCoefficient.multiply(estimate.getVatRate().divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal markupAmount = BigDecimal.ZERO;
        if (estimate.isApplyMarkup() && estimate.getMarkupValue() != null) {
            markupAmount = totalWithOverallCoefficient.multiply(estimate.getMarkupValue().divide(new BigDecimal("100")))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal finalTotal = totalWithOverallCoefficient.add(vatAmount).add(markupAmount);

        return ClientEstimateResponseDto.builder()
                .id(estimate.getId())
                .projectName(estimate.getProject().getName())
                .creationDate(estimate.getCreationDate())
                .status(estimate.getStatus())
                .items(itemDtos)
                .subtotal(subtotal)
                .vatAmount(vatAmount)
                .markupAmount(markupAmount)
                .finalTotal(finalTotal)
                .build();
    }

    private ClientEstimateItemResponseDto toClientEstimateItemResponseDto(EstimateItem item) {
        BigDecimal itemTotal = item.getPosition().getCustomerPrice()
                .multiply(item.getQuantity())
                .multiply(item.getLocalCoefficient() != null ? item.getLocalCoefficient() : BigDecimal.ONE)
                .setScale(2, RoundingMode.HALF_UP);

        return ClientEstimateItemResponseDto.builder()
                .id(item.getId())
                .position(toClientPositionDto(item.getPosition()))
                .quantity(item.getQuantity())
                .total(itemTotal)
                .build();
    }

    private ClientPositionResponseDto toClientPositionDto(Position position) {
        return ClientPositionResponseDto.builder()
                .id(position.getId())
                .name(position.getName())
                .unit(position.getUnit())
                .customerPrice(position.getCustomerPrice())
                .description(position.getDescription())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<EstimateResponseDto> getEstimatesByEstimator(String username, Pageable pageable) {
        User estimator = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return estimateRepository.findAllByEstimator(estimator, pageable)
                .map(this::toEstimateResponseDto);
    }
}