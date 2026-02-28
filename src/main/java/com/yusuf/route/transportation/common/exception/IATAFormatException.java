package com.yusuf.route.transportation.common.exception;



public class IATAFormatException extends BusinessException {

    public IATAFormatException() {
        super(ErrorCode.INVALID_IATA_CODE);
    }
}