package com.yusuf.route.transportation.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlightRequiresAirportExceptionTest {

    @Test
    void hasCorrectErrorCode() {
        FlightRequiresAirportException ex = new FlightRequiresAirportException();
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FLIGHT_REQUIRES_AIRPORT);
    }

    @Test
    void messageFromErrorCode() {
        FlightRequiresAirportException ex = new FlightRequiresAirportException();
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getDescription());
    }
}
