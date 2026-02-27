package com.yusuf.route.transportation.transportation.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum OperatingDay {

    MON(1),
    TUE(2),
    WED(3),
    THU(4),
    FRI(5),
    SAT(6),
    SUN(7);

    private final int code;

    OperatingDay(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // lookup map (O(1))
    private static final Map<Integer, OperatingDay> BY_CODE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(OperatingDay::getCode, d -> d));

    public static OperatingDay fromCode(int code) {
        OperatingDay day = BY_CODE.get(code);

        if (day == null) {
            throw new IllegalArgumentException("Invalid day code: " + code);
        }
        return day;
    }
}
