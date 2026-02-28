package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;

import java.util.List;

public interface RouteFinder {

     List<RouteResponse> findRoutes(String originCode,
                                          String destinationCode,
                                          List<Transportation> transportations);
}
