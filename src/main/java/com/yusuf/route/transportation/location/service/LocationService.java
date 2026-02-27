package com.yusuf.route.transportation.location.service;

import com.yusuf.route.transportation.location.dto.LocationCreateRequest;
import com.yusuf.route.transportation.location.dto.LocationResponse;
import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    public LocationResponse create(LocationCreateRequest req) {
        if (locationRepository.existsByLocationCode(req.locationCode())) {
            throw new IllegalArgumentException("locationCode already exists: " + req.locationCode());
        }

        Location saved = locationRepository.save(Location.builder()
                .locationCode(req.locationCode())
                .name(req.name())
                .city(req.city())
                .country(req.country())
                .build());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LocationResponse> list() {
        return locationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse get(Long id) {
        Location loc = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("location not found: " + id));
        return toResponse(loc);
    }

    private LocationResponse toResponse(Location loc) {
        return new LocationResponse(loc.getId(), loc.getLocationCode(), loc.getName(), loc.getCity(), loc.getCountry());
    }
}
