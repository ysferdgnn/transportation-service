package com.yusuf.route.transportation.location.dto;

public record LocationResponse(
        Long id,
        String locationCode,
        String name,
        String city,
        String country
) {}
