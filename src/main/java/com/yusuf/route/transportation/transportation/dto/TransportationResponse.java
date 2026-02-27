package com.yusuf.route.transportation.transportation.dto;

import com.yusuf.route.transportation.transportation.enums.TransportationType;

import java.util.Set;

public record TransportationResponse(
        Long id,
        String originCode,
        String destinationCode,
        TransportationType type,
        Set<Integer> operatingDays
) {}
