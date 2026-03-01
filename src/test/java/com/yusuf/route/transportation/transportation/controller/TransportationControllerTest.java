package com.yusuf.route.transportation.transportation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusuf.route.transportation.controller.ControllerTestBase;
import com.yusuf.route.transportation.transportation.dto.TransportationCreateRequest;
import com.yusuf.route.transportation.transportation.dto.TransportationResponse;
import com.yusuf.route.transportation.transportation.enums.TransportationType;
import com.yusuf.route.transportation.transportation.service.TransportationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransportationController.class)
class TransportationControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransportationService transportationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_returns201AndBodyWhenValid() throws Exception {
        TransportationCreateRequest req = new TransportationCreateRequest(
                "IST", "ADB", TransportationType.FLIGHT, Set.of(1, 2, 3));
        TransportationResponse response = new TransportationResponse(
                1L, "IST", "ADB", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(transportationService.create(any(TransportationCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transportations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.originCode").value("IST"))
                .andExpect(jsonPath("$.data.destinationCode").value("ADB"));

        verify(transportationService).create(any(TransportationCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void list_returns200AndList() throws Exception {
        when(transportationService.list()).thenReturn(List.of());

        mockMvc.perform(get("/api/transportations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void get_returns200WhenFound() throws Exception {
        TransportationResponse response = new TransportationResponse(
                1L, "IST", "ADB", TransportationType.FLIGHT, Set.of(1, 2, 3));
        when(transportationService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/transportations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/transportations/1").with(csrf()))
                .andExpect(status().isNoContent());
        verify(transportationService).delete(1L);
    }
}
