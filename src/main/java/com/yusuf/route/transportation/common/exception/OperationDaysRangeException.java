package com.yusuf.route.transportation.common.exception;



public class OperationDaysRangeException extends BusinessException {

    public OperationDaysRangeException() {
        super(ErrorCode.OPERATION_DAYS_RANGE);
    }
}