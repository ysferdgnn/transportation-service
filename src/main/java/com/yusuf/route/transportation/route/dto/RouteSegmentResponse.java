package com.yusuf.route.transportation.route.dto;


import com.yusuf.route.transportation.transportation.enums.TransportationType;

public record RouteSegmentResponse(
        TransportationType type,
        String originCode,
        String destinationCode
) {}