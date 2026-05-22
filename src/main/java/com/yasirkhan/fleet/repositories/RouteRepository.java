package com.yasirkhan.fleet.repositories;

import com.yasirkhan.fleet.models.entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, UUID> {

    // Check if the exact geometric path already exists
    @Query(value = "SELECT EXISTS(SELECT 1 FROM wtms_routes WHERE ST_Equals(path, :path))", nativeQuery = true)
    boolean existsByPathEquals(@Param("path") LineString path);
}
