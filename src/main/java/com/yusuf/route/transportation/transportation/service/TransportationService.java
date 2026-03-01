package com.yusuf.route.transportation.transportation.service;


import com.yusuf.route.transportation.common.exception.FlightRequiresAirportException;
import com.yusuf.route.transportation.common.exception.LocationNotFoundException;
import com.yusuf.route.transportation.common.exception.OperationDaysRangeException;
import com.yusuf.route.transportation.location.entity.Location;
import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.location.repository.LocationRepository;
import com.yusuf.route.transportation.transportation.dto.TransportationCreateRequest;
import com.yusuf.route.transportation.transportation.dto.TransportationResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import com.yusuf.route.transportation.transportation.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
    @CacheEvict(cacheNames = "routes", allEntries = true)
    public TransportationResponse create(TransportationCreateRequest request) {

        Location origin = locationRepository
                .findByLocationCode(request.originCode())
                .orElseThrow(LocationNotFoundException::new);

        Location destination = locationRepository
                .findByLocationCode(request.destinationCode())
                .orElseThrow(LocationNotFoundException::new);

        if (request.type() == TransportationType.FLIGHT) {
            if (origin.getType() != LocationType.AIRPORT || destination.getType() != LocationType.AIRPORT) {
                throw new FlightRequiresAirportException();
            }
        }

        validateDays(request.operatingDays());

        Transportation entity = Transportation.builder()
                .origin(origin)
                .destination(destination)
                .type(request.type())
                .operatingDays(request.operatingDays())
                .build();

        Transportation saved = transportationRepository.save(entity);

        return map(saved);
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

    @Transactional
    @CacheEvict(cacheNames = "routes", allEntries = true)
    public void delete(Long id) {
        transportationRepository.deleteById(id);
    }

    // ---------- helpers ----------

    private void validateDays(Set<Integer> days) {
        boolean invalid = days.stream()
                .anyMatch(d -> d < 1 || d > 7);

        if (invalid) {
            throw new OperationDaysRangeException();
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
