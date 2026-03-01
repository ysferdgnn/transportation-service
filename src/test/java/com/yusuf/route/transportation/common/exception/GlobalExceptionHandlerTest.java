package com.yusuf.route.transportation.common.exception;

import com.yusuf.route.transportation.common.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusiness_returnsErrorResponseWithCodeAndStatus() {
        FlightRequiresAirportException ex = new FlightRequiresAirportException();

        ResponseEntity<ErrorResponse> response = handler.handleBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getCode());
        assertThat(response.getBody().message()).isEqualTo(ErrorCode.FLIGHT_REQUIRES_AIRPORT.getDescription());
    }

    @Test
    void handleBusiness_returnsNotFoundForRouteNotFoundException() {
        RouteNotFoundException ex = new RouteNotFoundException();

        ResponseEntity<ErrorResponse> response = handler.handleBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("ROUTE_NOT_FOUND");
    }

    @Test
    void handleValidation_returnsValidationErrorWithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("request", "originCode", "must not be blank"),
                new FieldError("request", "destinationCode", "must not be blank")
        ));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ErrorResponse response = handler.handleValidation(ex);

        assertThat(response.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.message()).isEqualTo("Request validation failed");
        assertThat(response.fieldErrors()).hasSize(2);
        assertThat(response.fieldErrors().get(0).field()).isEqualTo("originCode");
        assertThat(response.fieldErrors().get(0).message()).isEqualTo("must not be blank");
    }

    @Test
    void handleGeneric_returnsInternalError() {
        Exception ex = new RuntimeException("Unexpected");

        ErrorResponse response = handler.handleGeneric(ex);

        assertThat(response.code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.message()).isEqualTo("Unexpected error occurred");
    }
}
