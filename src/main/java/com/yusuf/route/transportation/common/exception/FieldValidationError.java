package com.yusuf.route.transportation.common.exception;

import lombok.Builder;

@Builder
public record FieldValidationError(String field, String message) {
}