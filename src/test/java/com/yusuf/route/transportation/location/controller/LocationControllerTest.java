package com.yusuf.route.transportation.location.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusuf.route.transportation.controller.ControllerTestBase;
import com.yusuf.route.transportation.location.dto.LocationCreateRequest;
import com.yusuf.route.transportation.location.dto.LocationResponse;
import com.yusuf.route.transportation.location.enums.LocationType;
import com.yusuf.route.transportation.location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
class LocationControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocationService locationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201AndBodyWhenValid() throws Exception {
        LocationCreateRequest req = new LocationCreateRequest("OTH", "Other", "Istanbul", "Turkey", LocationType.OTHER);
        LocationResponse response = new LocationResponse(1L, "OTH", "Other", "Istanbul", "Turkey", LocationType.OTHER);
        when(locationService.create(any(LocationCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/locations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.locationCode").value("OTH"));

        verify(locationService).create(any(LocationCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void list_returns200AndList() throws Exception {
        when(locationService.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void get_returns200AndBodyWhenFound() throws Exception {
        LocationResponse response = new LocationResponse(1L, "IST", "Istanbul", "Istanbul", "Turkey", LocationType.AIRPORT);
        when(locationService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/locations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.locationCode").value("IST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/locations/1").with(csrf()))
                .andExpect(status().isNoContent());
        verify(locationService).delete(1L);
    }
}
