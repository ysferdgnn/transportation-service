package com.yusuf.route.transportation.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void flightRequiresAirportHasBadRequestStatus() {
        assertThat(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getCode()).isEqualTo("FLIGHT_REQUIRES_AIRPORT");
        assertThat(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getDescription())
                .contains("Flight")
                .contains("AIRPORT");
    }

    @Test
    void routeNotFoundHasNotFoundStatus() {
        assertThat(ErrorCode.ROUTE_NOT_FOUND.getCode()).isEqualTo("ROUTE_NOT_FOUND");
        assertThat(ErrorCode.ROUTE_NOT_FOUND.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void locationNotFoundHasNotFoundStatus() {
        assertThat(ErrorCode.LOCATION_NOT_FOUND.getCode()).isEqualTo("LOCATION_NOT_FOUND");
        assertThat(ErrorCode.LOCATION_NOT_FOUND.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void operationDaysRangeHasStatus() {
        assertThat(ErrorCode.OPERATION_DAYS_RANGE.getCode()).isEqualTo("OPERATION_DAYS_RANGE");
    }
}
