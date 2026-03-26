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

    private String routeName;

    private LineString path;

    private String estimatedDistance;

    private String estimatedTime;

    private Status status;
}
