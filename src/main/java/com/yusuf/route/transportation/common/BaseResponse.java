package com.yusuf.route.transportation.common;

import lombok.Builder;

@Builder
public record BaseResponse<T>( T data) {

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .data(data)
                .build();
    }


}