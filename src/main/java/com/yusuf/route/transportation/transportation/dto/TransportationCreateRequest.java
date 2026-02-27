package com.yusuf.route.transportation.transportation.dto;

import com.yusuf.route.transportation.transportation.enums.TransportationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record TransportationCreateRequest(

        @NotBlank(message = "{validation.transportation.originCode.notBlank}")
        String originCode,

        @NotBlank(message = "{validation.transportation.destinationCode.notBlank}")
        String destinationCode,

        @NotNull(message = "{validation.transportation.type.notNull}")
        TransportationType type,

        @NotEmpty(message = "{validation.transportation.operatingDays.notEmpty}")
        Set<Integer> operatingDays
) {}