package com.yusuf.route.transportation.common.exception;



public class LocationNotFoundException extends BusinessException {

    public LocationNotFoundException() {
        super(ErrorCode.LOCATION_NOT_FOUND);
    }
}