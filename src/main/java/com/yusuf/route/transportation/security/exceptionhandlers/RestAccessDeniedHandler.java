package com.yusuf.route.transportation.security.exceptionhandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yusuf.route.transportation.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        log.warn("Access denied: {}", ex.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ErrorResponse error = ErrorResponse.builder()
                .code("FORBIDDEN")
                .message("You are not allowed to access this resource")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}