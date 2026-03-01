package com.yusuf.route.transportation.common.exception;

public class FlightRequiresAirportException extends BusinessException {

    public FlightRequiresAirportException() {
        super(ErrorCode.FLIGHT_REQUIRES_AIRPORT);
    }
}
