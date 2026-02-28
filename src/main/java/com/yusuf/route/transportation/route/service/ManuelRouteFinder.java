package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.dto.RouteSegmentResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Finds all valid routes with constraints:
 * - At most 3 segments
 * - Exactly 1 FLIGHT segment
 *
 * Patterns:
 * 1) Direct flight
 * 2) Ground transfer before flight
 * 3) Ground transfer after flight
 * 4) Ground transfers before and after flight
 */
@Component
public class ManuelRouteFinder implements RouteFinder {

    public List<RouteResponse> findRoutes(String originCode,
                                          String destinationCode,
                                          List<Transportation> transportations) {

        Index index = buildIndexes(transportations);

        List<RouteResponse> routes = new ArrayList<>();
        Set<String> signatures = new HashSet<>(); // dedupe

        addDirectFlightRoutes(originCode, destinationCode, index, routes, signatures);

        addRoutesWithGroundTransferBeforeFlight(originCode, destinationCode, index, routes, signatures);

        addRoutesWithGroundTransferAfterFlight(originCode, destinationCode, index, routes, signatures);

        addRoutesWithGroundTransfersBeforeAndAfterFlight(originCode, destinationCode, index, routes, signatures);

        return routes;
    }

    // -------- Pattern 1: Direct flight --------
    private void addDirectFlightRoutes(String originCode,
                                       String destinationCode,
                                       Index index,
                                       List<RouteResponse> routes,
                                       Set<String> signatures) {

        for (Transportation flight : index.flightsFrom(originCode)) {
            if (destinationCode.equals(flight.getDestination().getLocationCode())) {
                addRoute(routes, signatures, List.of(flight));
            }
        }
    }

    // -------- Pattern 2: Ground -> Flight --------
    private void addRoutesWithGroundTransferBeforeFlight(String originCode,
                                                         String destinationCode,
                                                         Index index,
                                                         List<RouteResponse> routes,
                                                         Set<String> signatures) {

        for (Transportation groundTransport : index.nonFlightsFrom(originCode)) {
            String transferLocation = groundTransport.getDestination().getLocationCode();

            for (Transportation flight : index.flightsFrom(transferLocation)) {
                if (destinationCode.equals(flight.getDestination().getLocationCode())) {
                    addRoute(routes, signatures, List.of(groundTransport, flight));
                }
            }
        }
    }

    // -------- Pattern 3: Flight -> Ground --------
    private void addRoutesWithGroundTransferAfterFlight(String originCode,
                                                        String destinationCode,
                                                        Index index,
                                                        List<RouteResponse> routes,
                                                        Set<String> signatures) {

        for (Transportation flight : index.flightsFrom(originCode)) {
            String arrivalLocation = flight.getDestination().getLocationCode();

            for (Transportation groundTransport : index.nonFlightsFrom(arrivalLocation)) {
                if (destinationCode.equals(groundTransport.getDestination().getLocationCode())) {
                    addRoute(routes, signatures, List.of(flight, groundTransport));
                }
            }
        }
    }

    // -------- Pattern 4: Ground -> Flight -> Ground --------
    private void addRoutesWithGroundTransfersBeforeAndAfterFlight(String originCode,
                                                                  String destinationCode,
                                                                  Index index,
                                                                  List<RouteResponse> routes,
                                                                  Set<String> signatures) {

        for (Transportation firstGround : index.nonFlightsFrom(originCode)) {
            String firstTransferLocation = firstGround.getDestination().getLocationCode();

            for (Transportation flight : index.flightsFrom(firstTransferLocation)) {
                String secondTransferLocation = flight.getDestination().getLocationCode();

                for (Transportation lastGround : index.nonFlightsFrom(secondTransferLocation)) {
                    if (destinationCode.equals(lastGround.getDestination().getLocationCode())) {
                        addRoute(routes, signatures, List.of(firstGround, flight, lastGround));
                    }
                }
            }
        }
    }

    // -------- Index building --------
    private Index buildIndexes(List<Transportation> transportations) {
        Map<String, List<Transportation>> flightsFrom = new HashMap<>();
        Map<String, List<Transportation>> nonFlightsFrom = new HashMap<>();

        for (Transportation t : transportations) {
            String from = t.getOrigin().getLocationCode();

            if (t.getType() == TransportationType.FLIGHT) {
                flightsFrom.computeIfAbsent(from, k -> new ArrayList<>()).add(t);
            } else {
                nonFlightsFrom.computeIfAbsent(from, k -> new ArrayList<>()).add(t);
            }
        }

        return new Index(flightsFrom, nonFlightsFrom);
    }

    // -------- Output + dedupe --------
    private void addRoute(List<RouteResponse> routes, Set<String> signatures, List<Transportation> segments) {
        String signature = buildSignature(segments);
        if (!signatures.add(signature)) {
            return;
        }

        List<RouteSegmentResponse> dtoSegments = segments.stream()
                .map(t -> new RouteSegmentResponse(
                        t.getType(),
                        t.getOrigin().getLocationCode(),
                        t.getDestination().getLocationCode()
                ))
                .toList();

        routes.add(new RouteResponse(dtoSegments));
    }

    private String buildSignature(List<Transportation> segments) {
        // If IDs are null (edge case before persistence), fall back to object identity.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            Transportation t = segments.get(i);
            String token = (t.getId() != null) ? t.getId().toString() : ("@" + System.identityHashCode(t));
            if (i > 0) sb.append("-");
            sb.append(token);
        }
        return sb.toString();
    }

    // -------- Tiny helper record --------
    private record Index(
            Map<String, List<Transportation>> flightsFrom,
            Map<String, List<Transportation>> nonFlightsFrom
    ) {
        List<Transportation> flightsFrom(String originCode) {
            return flightsFrom.getOrDefault(originCode, List.of());
        }

        List<Transportation> nonFlightsFrom(String originCode) {
            return nonFlightsFrom.getOrDefault(originCode, List.of());
        }
    }
}