package com.yasirkhan.fleet.models.entities;

import com.yasirkhan.fleet.models.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.LineString;

import java.util.UUID;

@Entity
@Table(name = "WTMS_ROUTE")
@Data
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID routeId;

    @Column(nullable = false)
    private String routeName; // e.g., "G7 to Losar Fast Track"

    // The Territory this route belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tehsil_id", nullable = false)
    private Tehsil tehsil;

    // The Specific Yards
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_yard_id", nullable = false)
    private Yard sourceYard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_yard_id", nullable = false)
    private Yard destinationYard;

    @Column(nullable = false)
    private LineString path;

    @Column(nullable = false)
    private String estimatedDistance;

    @Column(nullable = false)
    private String estimatedTime;

    @Enumerated(EnumType.STRING)
    private Status status;
}