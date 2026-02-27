package com.yusuf.route.transportation.transportation.repository;

import com.yusuf.route.transportation.transportation.entity.Transportation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransportationRepository
        extends JpaRepository<Transportation, Long> {

    /**
     * Loads origin + destination together
     * Prevents N+1 during DTO mapping
     */
    @Override
    @EntityGraph(attributePaths = {"origin", "destination"})
    List<Transportation> findAll();

    /**
     * Single transportation with locations
     */
    @Override
    @EntityGraph(attributePaths = {"origin", "destination"})
    Optional<Transportation> findById(Long id);

    /**
     * Route calculation helper:
     * all outgoing edges from a location
     */
    @EntityGraph(attributePaths = {"origin", "destination"})
    List<Transportation> findByOrigin_LocationCode(String locationCode);

    @EntityGraph(attributePaths = {"origin", "destination"})
    List<Transportation> findDistinctByOperatingDaysContains(Integer dayOfWeek);
}
