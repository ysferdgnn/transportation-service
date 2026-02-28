package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.common.exception.RouteNotFoundException;
import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final TransportationRepository transportationRepository;
    private final RouteFinder routeFinder;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes", key = "#originCode + '|' + #destinationCode + '|' + #date")
    public List<RouteResponse> findRoutes(String originCode, String destinationCode, LocalDate date) {

        int dayOfWeek = date.getDayOfWeek().getValue(); // 1..7

        List<Transportation> edges = transportationRepository
                .findDistinctByOperatingDaysContains(dayOfWeek);

        List<RouteResponse> routes = routeFinder.findRoutes(originCode, destinationCode, edges);
        if (routes.isEmpty()) {
            throw new RouteNotFoundException();
        }
        return routes;
    }
}