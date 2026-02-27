package com.yusuf.route.transportation.transportation.controller;

import com.yusuf.route.transportation.common.BaseResponse;
import com.yusuf.route.transportation.transportation.dto.TransportationCreateRequest;
import com.yusuf.route.transportation.transportation.dto.TransportationResponse;
import com.yusuf.route.transportation.transportation.service.TransportationService;
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

@Tag(name = "Transportations", description = "Transportation CRUD endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transportations")
public class TransportationController {

    private final TransportationService transportationService;

    @Operation(
            summary = "Create transportation",
            description = "Creates a new transportation record with origin/destination, departure/arrival and price."
    )
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<TransportationResponse> create(
            @Valid @RequestBody TransportationCreateRequest request
    ) {
        return BaseResponse.ok(transportationService.create(request));
    }

    @Operation(
            summary = "List transportations",
            description = "Returns all transportation records."
    )
    @ApiResponse(responseCode = "200", description = "OK")

    @GetMapping
    public BaseResponse<List<TransportationResponse>> list() {
        return BaseResponse.ok(transportationService.list());
    }

    @Operation(
            summary = "Get transportation by id",
            description = "Returns a single transportation record by its id."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transportation not found",
            content = @Content
    )
    @GetMapping("/{id}")
    public BaseResponse<TransportationResponse> get(
            @Parameter(description = "Transportation id", example = "1")
            @PathVariable Long id
    ) {
        return BaseResponse.ok(transportationService.get(id));
    }


}