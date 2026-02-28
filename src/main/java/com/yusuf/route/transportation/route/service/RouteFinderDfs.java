package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.dto.RouteSegmentResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;

@Primary
@Component
public class RouteFinderDfs implements RouteFinder{

    private static final int MAX_SEGMENTS = 3;
    private static final int REQUIRED_FLIGHTS = 1;

    public List<RouteResponse> findRoutes(String originCode,
                                          String destinationCode,
                                          List<Transportation> transportations) {


        Map<String, List<Transportation>> outgoingNodes = new HashMap<>();

        for (Transportation transportation : transportations) {
            if (transportation.getOrigin() == null || transportation.getDestination() == null) {
                continue;
            }
            String from = transportation.getOrigin().getLocationCode();
            String to = transportation.getDestination().getLocationCode();

            if (from == null || to == null) {
                continue;
            }

            outgoingNodes.computeIfAbsent(from, k -> new ArrayList<>()).add(transportation);
        }

        List<RouteResponse> routes = new ArrayList<>();
        Set<String> signatures = new HashSet<>();

        ArrayList<Transportation> currentEmptyPath = new ArrayList<>();
        depthFirstSearch(
                originCode,
                destinationCode,
                outgoingNodes,
                currentEmptyPath,
                0,
                routes,
                signatures
        );

        return routes;
    }

    private void depthFirstSearch(String current,
                                  String destination,
                                  Map<String, List<Transportation>> outgoing,
                                  List<Transportation> path,
                                  int flightCount,
                                  List<RouteResponse> routes,
                                  Set<String> signatures) {

        if (path.size() > MAX_SEGMENTS) return;

        if (!path.isEmpty() && destination.equals(current)) {
            if (flightCount == REQUIRED_FLIGHTS) {
                addRoute(routes, signatures, path);
            }
            return;
        }

        if (flightCount > REQUIRED_FLIGHTS) return;

        for (Transportation transportation : outgoing.getOrDefault(current, List.of())) {
            int newFlightCount = flightCount + (transportation.getType() == TransportationType.FLIGHT ? 1 : 0);
            if (newFlightCount > REQUIRED_FLIGHTS) continue;

            path.add(transportation);

            // Move to next node
            String next = transportation.getDestination().getLocationCode();
            depthFirstSearch(next, destination, outgoing, path, newFlightCount, routes, signatures);

            path.remove(path.size() - 1);
        }
    }

    private void addRoute(List<RouteResponse> routes, Set<String> signatures, List<Transportation> segments) {
        String signature = buildSignature(segments);
        if (!signatures.add(signature)) return;

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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            Transportation t = segments.get(i);
            String token = (t.getId() != null) ? t.getId().toString() : ("@" + System.identityHashCode(t));
            if (i > 0) sb.append("-");
            sb.append(token);
        }
        return sb.toString();
    }
}