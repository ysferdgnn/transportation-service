package com.yusuf.route.transportation.location.controller;

import com.yusuf.route.transportation.common.BaseResponse;
import com.yusuf.route.transportation.location.dto.LocationCreateRequest;
import com.yusuf.route.transportation.location.dto.LocationResponse;
import com.yusuf.route.transportation.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Locations", description = "Location CRUD endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Operation(
            summary = "Create location",
            description = "Creates a new location record."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Created"

    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<LocationResponse> create(
            @Valid @RequestBody LocationCreateRequest req
    ) {
        return BaseResponse.ok(locationService.create(req));
    }

    @Operation(
            summary = "List locations",
            description = "Returns all locations."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK"

    )
    @GetMapping
    public BaseResponse<List<LocationResponse>> list() {
        return BaseResponse.ok(locationService.list());
    }

    @Operation(
            summary = "Get location by id",
            description = "Returns a single location by its id."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK"

    )
    @ApiResponse(
            responseCode = "404",
            description = "Location not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public BaseResponse<LocationResponse> get(
            @Parameter(description = "Location id", example = "1")
            @PathVariable Long id
    ) {
        return BaseResponse.ok(locationService.get(id));
    }


}