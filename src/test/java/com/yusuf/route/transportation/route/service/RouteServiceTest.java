package com.yusuf.route.transportation.route.service;

import com.yusuf.route.transportation.common.exception.RouteNotFoundException;
import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.dto.RouteSegmentResponse;
import com.yusuf.route.transportation.transportation.entity.Transportation;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import com.yusuf.route.transportation.transportation.repository.TransportationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private RouteFinder routeFinder;

    @InjectMocks
    private RouteService routeService;

    @Test
    void findRoutes_throwsRouteNotFoundWhenNoRoutesFound() {
        LocalDate date = LocalDate.of(2026, 3, 4);
        when(transportationRepository.findDistinctByOperatingDaysContains(3)).thenReturn(List.of());
        when(routeFinder.findRoutes("IST", "ADB", List.of())).thenReturn(List.of());

        assertThatThrownBy(() -> routeService.findRoutes("IST", "ADB", date))
                .isInstanceOf(RouteNotFoundException.class);
    }

    @Test
    void findRoutes_returnsRoutesFromFinderForGivenDayOfWeek() {
        LocalDate date = LocalDate.of(2026, 3, 5);
        List<Transportation> edges = List.of();
        when(transportationRepository.findDistinctByOperatingDaysContains(4)).thenReturn(edges);
        List<RouteResponse> expected = List.of(
                new RouteResponse(List.of(
                        new RouteSegmentResponse(TransportationType.FLIGHT, "IST", "ADB"))));
        when(routeFinder.findRoutes("IST", "ADB", edges)).thenReturn(expected);

        List<RouteResponse> result = routeService.findRoutes("IST", "ADB", date);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).segments()).hasSize(1);
        assertThat(result.get(0).segments().get(0).originCode()).isEqualTo("IST");
        assertThat(result.get(0).segments().get(0).destinationCode()).isEqualTo("ADB");
        verify(transportationRepository).findDistinctByOperatingDaysContains(4);
        verify(routeFinder).findRoutes("IST", "ADB", edges);
    }
}
