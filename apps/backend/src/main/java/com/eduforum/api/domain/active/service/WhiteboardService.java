package com.eduforum.api.domain.active.service;

import com.eduforum.api.common.exception.BusinessException;
import com.eduforum.api.common.exception.ErrorCode;
import com.eduforum.api.domain.active.dto.whiteboard.*;
import com.eduforum.api.domain.active.entity.*;
import com.eduforum.api.domain.active.repository.*;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import com.eduforum.api.domain.seminar.repository.SeminarRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WhiteboardService {

    private final WhiteboardRepository whiteboardRepository;
    private final WhiteboardElementRepository whiteboardElementRepository;
    private final SeminarRoomRepository seminarRoomRepository;

    @Transactional
    public WhiteboardResponse createWhiteboard(Long userId, Long seminarRoomId, CreateWhiteboardRequest request) {
        log.info("Creating whiteboard for seminar {} by user {}", seminarRoomId, userId);

        SeminarRoom seminarRoom = seminarRoomRepository.findById(seminarRoomId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Whiteboard whiteboard = Whiteboard.builder()
            .room(seminarRoom)
            .name(request.getName())
            .canvasSettings(request.getCanvasSettings() != null ? request.getCanvasSettings() : Map.of(
                "width", 1920,
                "height", 1080,
                "backgroundColor", "#FFFFFF"
            ))
            .build();

        whiteboard = whiteboardRepository.save(whiteboard);
        log.info("Created whiteboard {}", whiteboard.getId());
        return mapToResponse(whiteboard);
    }

    public WhiteboardResponse getWhiteboard(Long whiteboardId) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return mapToResponse(whiteboard);
    }

    public List<WhiteboardResponse> getWhiteboardsBySeminar(Long seminarRoomId) {
        return whiteboardRepository.findByRoomIdAndNotDeleted(seminarRoomId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public WhiteboardResponse saveWhiteboardState(Long userId, Long whiteboardId, SaveWhiteboardRequest request) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // Clear existing elements if this is a full save
        if (request.getClearExisting() != null && request.getClearExisting()) {
            whiteboard.clearElements();
            whiteboardElementRepository.deleteByWhiteboardId(whiteboardId);
        }

        // Add new elements
        if (request.getElements() != null) {
            for (WhiteboardElementDto dto : request.getElements()) {
                WhiteboardElement element = WhiteboardElement.builder()
                    .whiteboard(whiteboard)
                    .elementId(dto.getElementId())
                    .type(dto.getType())
                    .x(dto.getX())
                    .y(dto.getY())
                    .width(dto.getWidth())
                    .height(dto.getHeight())
                    .properties(dto.getProperties() != null ? dto.getProperties() : Map.of())
                    .build();

                whiteboard.addElement(element);
            }
        }

        // Update canvas settings if provided
        if (request.getCanvasSettings() != null) {
            whiteboard.setCanvasSettings(request.getCanvasSettings());
        }

        whiteboard = whiteboardRepository.save(whiteboard);
        log.info("Saved whiteboard state for {}", whiteboardId);
        return mapToResponse(whiteboard);
    }

    @Transactional
    public void addElement(Long whiteboardId, WhiteboardElementDto dto) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        WhiteboardElement element = WhiteboardElement.builder()
            .whiteboard(whiteboard)
            .elementId(dto.getElementId())
            .type(dto.getType())
            .x(dto.getX())
            .y(dto.getY())
            .width(dto.getWidth())
            .height(dto.getHeight())
            .properties(dto.getProperties() != null ? dto.getProperties() : Map.of())
            .build();

        whiteboard.addElement(element);
        whiteboardRepository.save(whiteboard);
    }

    @Transactional
    public void updateElement(Long whiteboardId, String elementId, WhiteboardElementDto dto) {
        WhiteboardElement element = whiteboardElementRepository
            .findByWhiteboardIdAndElementId(whiteboardId, elementId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        element.setType(dto.getType());
        element.setX(dto.getX());
        element.setY(dto.getY());
        element.setWidth(dto.getWidth());
        element.setHeight(dto.getHeight());
        element.setProperties(dto.getProperties() != null ? dto.getProperties() : Map.of());

        whiteboardElementRepository.save(element);
    }

    @Transactional
    public void removeElement(Long whiteboardId, String elementId) {
        WhiteboardElement element = whiteboardElementRepository
            .findByWhiteboardIdAndElementId(whiteboardId, elementId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        whiteboardElementRepository.delete(element);
        log.info("Removed element {} from whiteboard {}", elementId, whiteboardId);
    }

    @Transactional
    public void clearWhiteboard(Long userId, Long whiteboardId) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        whiteboard.clearElements();
        whiteboardElementRepository.deleteByWhiteboardId(whiteboardId);
        whiteboardRepository.save(whiteboard);

        log.info("Cleared whiteboard {}", whiteboardId);
    }

    @Transactional
    public void deleteWhiteboard(Long userId, Long whiteboardId) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        whiteboard.delete();
        whiteboardRepository.save(whiteboard);
        log.info("Deleted whiteboard {}", whiteboardId);
    }

    public String exportWhiteboardImage(Long whiteboardId) {
        Whiteboard whiteboard = whiteboardRepository.findByIdAndNotDeleted(whiteboardId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        // Placeholder for image export functionality
        // In a real implementation, this would generate an image from the whiteboard state
        log.info("Exporting whiteboard {} to image (placeholder)", whiteboardId);
        return "data:image/png;base64,placeholder";
    }

    private WhiteboardResponse mapToResponse(Whiteboard whiteboard) {
        List<WhiteboardElementDto> elements = whiteboard.getElements().stream()
            .map(element -> WhiteboardElementDto.builder()
                .elementId(element.getElementId())
                .type(element.getType())
                .x(element.getX())
                .y(element.getY())
                .width(element.getWidth())
                .height(element.getHeight())
                .properties(element.getProperties())
                .build())
            .collect(Collectors.toList());

        return WhiteboardResponse.builder()
            .id(whiteboard.getId())
            .roomId(whiteboard.getRoom().getId())
            .name(whiteboard.getName())
            .canvasSettings(whiteboard.getCanvasSettings())
            .elements(elements)
            .createdAt(whiteboard.getCreatedAt())
            .updatedAt(whiteboard.getUpdatedAt())
            .build();
    }
}
