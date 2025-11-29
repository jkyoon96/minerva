package com.eduforum.api.domain.active.entity;

import com.eduforum.api.domain.common.entity.BaseEntity;
import com.eduforum.api.domain.seminar.entity.SeminarRoom;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Whiteboard entity - represents a collaborative whiteboard
 */
@Entity
@Table(schema = "active", name = "whiteboards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Whiteboard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private SeminarRoom room;

    @Column(nullable = false, length = 200)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> canvasSettings = Map.of(
        "width", 1920,
        "height", 1080,
        "backgroundColor", "#FFFFFF"
    );

    @OneToMany(mappedBy = "whiteboard", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WhiteboardElement> elements = new ArrayList<>();

    public void addElement(WhiteboardElement element) {
        elements.add(element);
        element.setWhiteboard(this);
    }

    public void clearElements() {
        elements.clear();
    }
}
