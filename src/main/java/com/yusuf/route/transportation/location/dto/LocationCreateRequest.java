package com.yusuf.route.transportation.location.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationCreateRequest(

        @NotBlank(message = "{validation.location.locationCode.notBlank}")
        String locationCode,

        @NotBlank(message = "{validation.location.name.notBlank}")
        String name,

        @NotBlank(message = "{validation.location.city.notBlank}")
        String city,

        @NotBlank(message = "{validation.location.country.notBlank}")
        String country
) {}