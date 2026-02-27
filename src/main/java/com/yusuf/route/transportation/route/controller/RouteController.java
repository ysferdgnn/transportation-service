package com.yusuf.route.transportation.route.controller;

import com.yusuf.route.transportation.common.BaseResponse;
import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Routes", description = "Route search endpoints")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    @Operation(
            summary = "Search routes",
            description = "Finds routes by origin/destination codes and date."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error (missing/blank params or invalid date)",
            content = @Content
    )
    @GetMapping
    public BaseResponse<List<RouteResponse>> routes(
            @Parameter(
                    description = "Origin location code (e.g., IST)",
                    example = "IST",
                    required = true
            )
            @RequestParam
            @NotBlank(message = "{validation.route.originCode.notBlank}")
            String originCode,

            @Parameter(
                    description = "Destination location code (e.g., ADB)",
                    example = "ADB",
                    required = true
            )
            @RequestParam
            @NotBlank(message = "{validation.route.destinationCode.notBlank}")
            String destinationCode,

            @Parameter(
                    description = "Route date (ISO-8601, yyyy-MM-dd)",
                    example = "2026-03-05",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return BaseResponse.ok(routeService.findRoutes(originCode, destinationCode, date));
    }


}