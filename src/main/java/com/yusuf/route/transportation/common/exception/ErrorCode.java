package com.yusuf.route.transportation.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    ROUTE_NOT_FOUND(
            "ROUTE_NOT_FOUND",
            "No route found between locations",
            HttpStatus.NOT_FOUND
    ),

    LOCATION_NOT_FOUND(
            "LOCATION_NOT_FOUND",
            "Location not found",
            HttpStatus.NOT_FOUND
    ),

    INVALID_REQUEST(
            "INVALID_REQUEST",
            "Request validation failed",
            HttpStatus.BAD_REQUEST
    ),

    INTERNAL_ERROR(
            "INTERNAL_ERROR",
            "Unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final String description;
    private final HttpStatus status;

    ErrorCode(String code, String description, HttpStatus status) {
        this.code = code;
        this.description = description;
        this.status = status;
    }
}