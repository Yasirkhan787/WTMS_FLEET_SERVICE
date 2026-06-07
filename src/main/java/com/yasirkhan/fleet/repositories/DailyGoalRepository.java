package com.yasirkhan.fleet.repositories;

import com.yasirkhan.fleet.models.entities.DailyGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, UUID> {

    // Finds a specific goal for a specific Tehsil on a specific day
    Optional<DailyGoal> findByTehsil_TehsilIdAndTargetDate(UUID tehsilId, LocalDate targetDate);

    // Finds all goals ever assigned to a specific Tehsil
    List<DailyGoal> findAllByTehsil_TehsilId(UUID tehsilId);
}