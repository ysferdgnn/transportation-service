package com.yusuf.route.transportation.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yusuf.route.transportation.common.exception.FieldValidationError;
import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(String code,
                            String message,
                            @JsonInclude(JsonInclude.Include.NON_NULL) List<FieldValidationError> fieldErrors) {

}