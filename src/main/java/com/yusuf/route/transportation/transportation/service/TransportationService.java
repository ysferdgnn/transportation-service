package com.yusuf.route.transportation.transportation.service;


import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.repository.LocationRepository;
import com.yusuf.route.transportation.transportation.dto.TransportationCreateRequest;
import com.yusuf.route.transportation.transportation.dto.TransportationResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;

    // CREATE
    public TransportationResponse create(TransportationCreateRequest request) {

        Location origin = locationRepository
                .findByLocationCode(request.originCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("Origin location not found"));

        Location destination = locationRepository
                .findByLocationCode(request.destinationCode())
                .orElseThrow(() ->
                        new IllegalArgumentException("Destination location not found"));

        validateDays(request.operatingDays());

        Transportation entity = Transportation.builder()
                .origin(origin)
                .destination(destination)
                .type(request.type())
                .operatingDays(request.operatingDays())
                .build();

        transportationRepository.save(entity);

        return map(entity);
    }

    // LIST
    @Transactional(readOnly = true)
    public List<TransportationResponse> list() {
        return transportationRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    // GET
    @Transactional(readOnly = true)
    public TransportationResponse get(Long id) {
        Transportation t = transportationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportation not found"));

        return map(t);
    }

    // ---------- helpers ----------

    private void validateDays(Set<Integer> days) {
        boolean invalid = days.stream()
                .anyMatch(d -> d < 1 || d > 7);

        if (invalid) {
            throw new IllegalArgumentException("Operating days must be between 1 and 7");
        }
    }

    private TransportationResponse map(Transportation t) {
        return new TransportationResponse(
                t.getId(),
                t.getOrigin().getLocationCode(),
                t.getDestination().getLocationCode(),
                t.getType(),
                t.getOperatingDays()
        );
    }
}
