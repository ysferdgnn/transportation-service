package com.yusuf.route.transportation.location.dto;

import com.yusuf.route.transportation.location.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationCreateRequest(

        @NotBlank(message = "{validation.location.locationCode.notBlank}")
        String locationCode,

        @NotBlank(message = "{validation.location.name.notBlank}")
        String name,

        @NotBlank(message = "{validation.location.city.notBlank}")
        String city,

        @NotBlank(message = "{validation.location.country.notBlank}")
        String country,

        @NotNull(message = "{validation.location.type.notNull}")
        LocationType type
) {}