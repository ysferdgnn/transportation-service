package com.yusuf.route.transportation.location.dto;

import com.yusuf.route.transportation.location.enums.LocationType;

public record LocationResponse(
        Long id,
        String locationCode,
        String name,
        String city,
        String country,
        LocationType type
) {}
