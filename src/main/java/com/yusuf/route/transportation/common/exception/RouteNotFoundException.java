package com.yusuf.route.transportation.common.exception;

public class RouteNotFoundException extends BusinessException {

    public RouteNotFoundException() {
        super(ErrorCode.ROUTE_NOT_FOUND);
    }
}