package com.yusuf.route.transportation.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldValidationError {
    private final String field;
    private final String message;
}