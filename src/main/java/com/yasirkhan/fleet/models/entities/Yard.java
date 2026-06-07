package com.yasirkhan.fleet.models.entities;

import com.yasirkhan.fleet.models.enums.Status;
import com.yasirkhan.fleet.models.enums.YardType;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.UUID;

@Entity
@Table(name = "WTMS_YARD")
@Data
public class Yard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String yardName; // e.g., "I-9 Sector Transfer Station", "Losar Landfill"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private YardType yardType; // Enum: COLLECTION_POINT, DUMP_SITE, PARKING_DEPOT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tehsil_id", nullable = false)
    private Tehsil tehsil;

    // GEOGRAPHIC DATA (PostGIS)
    // For the Radius Method
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point centerPoint;
    private Double radiusMeters; // e.g., 200.0

    // For the Exact Boundaries Method
    @Column(columnDefinition = "geometry(Polygon, 4326)")
    private Polygon boundaryPolygon;

    private Status status;
}