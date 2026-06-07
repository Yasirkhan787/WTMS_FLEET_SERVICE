package com.yasirkhan.fleet.repositories;

import com.yasirkhan.fleet.models.entities.Tehsil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface TehsilRepository extends JpaRepository<Tehsil, UUID> {
    // Helpful to prevent the Admin from creating duplicate Tehsils
    boolean existsByTehsilNameIgnoreCase(String tehsilName);
}