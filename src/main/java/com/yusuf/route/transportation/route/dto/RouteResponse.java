package com.yusuf.route.transportation.route.dto;

import java.util.List;

public record RouteResponse(
        List<RouteSegmentResponse> segments
) {}