package com.yasirkhan.fleet.repositories;

import com.yasirkhan.fleet.models.entities.Yard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface YardRepository extends JpaRepository<Yard, UUID> {

    // Fetches all Yards (TCPs and Dump Sites) for a specific territory
    List<Yard> findAllByTehsil_TehsilId(UUID tehsilId);
}