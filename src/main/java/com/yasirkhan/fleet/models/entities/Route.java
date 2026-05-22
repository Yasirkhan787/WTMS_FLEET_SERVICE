package com.yasirkhan.fleet.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "WTMS_ROUTES")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID routeId;

    @Column(nullable = false)
    private String routeName;

    @Column(nullable = false)
    private String origin;

    // Store the exact pin coordinates
    @Column(nullable = false)
    private Double originLat;

    @Column(nullable = false)
    private Double originLng;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private Double destinationLat;

    @Column(nullable = false)
    private Double destinationLng;

    @Column(nullable = false)
    private LineString path;

    @Column(nullable = false)
    private String estimatedDistance;

    @Column(nullable = false)
    private String estimatedTime;

    @Enumerated(EnumType.STRING)
    private Status status;
}
