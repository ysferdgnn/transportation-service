package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.dto.RouteSegmentResponse;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.yusuf.route.transportation.TestDataHelper.location;
import static com.yusuf.route.transportation.TestDataHelper.transportation;
import static org.assertj.core.api.Assertions.assertThat;

class ManuelRouteFinderTest {

    private ManuelRouteFinder routeFinder;

    @BeforeEach
    void setUp() {
        routeFinder = new ManuelRouteFinder();
    }

    @Test
    void findRoutes_returnsEmptyWhenNoTransportations() {
        List<RouteResponse> routes = routeFinder.findRoutes("IST", "ADB", List.of());
        assertThat(routes).isEmpty();
    }

    @Test
    void findRoutes_returnsDirectFlightRoute() {
        var ist = location(1L, "IST", LocationType.AIRPORT);
        var adb = location(2L, "ADB", LocationType.AIRPORT);
        var flight = transportation(10L, ist, adb, TransportationType.FLIGHT);

        List<RouteResponse> routes = routeFinder.findRoutes("IST", "ADB", List.of(flight));

        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).segments()).containsExactly(
                new RouteSegmentResponse(TransportationType.FLIGHT, "IST", "ADB"));
    }

    @Test
    void findRoutes_returnsRouteWithGroundThenFlight() {
        var a = location(1L, "A", LocationType.OTHER);
        var ist = location(2L, "IST", LocationType.AIRPORT);
        var adb = location(3L, "ADB", LocationType.AIRPORT);
        var bus = transportation(10L, a, ist, TransportationType.BUS);
        var flight = transportation(20L, ist, adb, TransportationType.FLIGHT);

        List<RouteResponse> routes = routeFinder.findRoutes("A", "ADB", List.of(bus, flight));

        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).segments()).hasSize(2);
        assertThat(routes.get(0).segments().get(0)).isEqualTo(
                new RouteSegmentResponse(TransportationType.BUS, "A", "IST"));
        assertThat(routes.get(0).segments().get(1)).isEqualTo(
                new RouteSegmentResponse(TransportationType.FLIGHT, "IST", "ADB"));
    }

    @Test
    void findRoutes_returnsRouteWithFlightThenGround() {
        var ist = location(1L, "IST", LocationType.AIRPORT);
        var adb = location(2L, "ADB", LocationType.AIRPORT);
        var b = location(3L, "B", LocationType.OTHER);
        var flight = transportation(10L, ist, adb, TransportationType.FLIGHT);
        var bus = transportation(20L, adb, b, TransportationType.BUS);

        List<RouteResponse> routes = routeFinder.findRoutes("IST", "B", List.of(flight, bus));

        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).segments()).hasSize(2);
        assertThat(routes.get(0).segments().get(0)).isEqualTo(
                new RouteSegmentResponse(TransportationType.FLIGHT, "IST", "ADB"));
        assertThat(routes.get(0).segments().get(1)).isEqualTo(
                new RouteSegmentResponse(TransportationType.BUS, "ADB", "B"));
    }

    @Test
    void findRoutes_returnsRouteWithGroundFlightGround() {
        var a = location(1L, "A", LocationType.OTHER);
        var ist = location(2L, "IST", LocationType.AIRPORT);
        var adb = location(3L, "ADB", LocationType.AIRPORT);
        var b = location(4L, "B", LocationType.OTHER);
        var g1 = transportation(10L, a, ist, TransportationType.BUS);
        var flight = transportation(20L, ist, adb, TransportationType.FLIGHT);
        var g2 = transportation(30L, adb, b, TransportationType.UBER);

        List<RouteResponse> routes = routeFinder.findRoutes("A", "B", List.of(g1, flight, g2));

        assertThat(routes).hasSize(1);
        assertThat(routes.get(0).segments()).hasSize(3);
        assertThat(routes.get(0).segments().get(0).type()).isEqualTo(TransportationType.BUS);
        assertThat(routes.get(0).segments().get(1).type()).isEqualTo(TransportationType.FLIGHT);
        assertThat(routes.get(0).segments().get(2).type()).isEqualTo(TransportationType.UBER);
    }
}
