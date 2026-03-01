package com.yusuf.route.transportation.route.controller;

import com.yusuf.route.transportation.controller.ControllerTestBase;
import com.yusuf.route.transportation.route.dto.RouteResponse;
import com.yusuf.route.transportation.route.dto.RouteSegmentResponse;
import com.yusuf.route.transportation.route.service.RouteService;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
class RouteControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    @Test
    @WithMockUser(roles = "AGENCY")
    void getRoutes_returns200AndRoutesWhenFound() throws Exception {
        List<RouteResponse> routes = List.of(
                new RouteResponse(List.of(
                        new RouteSegmentResponse(TransportationType.FLIGHT, "IST", "ADB"))));
        when(routeService.findRoutes("IST", "ADB", LocalDate.of(2026, 3, 5))).thenReturn(routes);

        mockMvc.perform(get("/api/routes")
                        .param("originCode", "IST")
                        .param("destinationCode", "ADB")
                        .param("date", "2026-03-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].segments[0].originCode").value("IST"))
                .andExpect(jsonPath("$.data[0].segments[0].destinationCode").value("ADB"));

        verify(routeService).findRoutes("IST", "ADB", LocalDate.of(2026, 3, 5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoutes_returns400WhenOriginCodeBlank() throws Exception {
        mockMvc.perform(get("/api/routes")
                        .param("originCode", " ")
                        .param("destinationCode", "ADB")
                        .param("date", "2026-03-05"))
                .andExpect(status().isBadRequest());
    }
}
