package com.yusuf.route.transportation.controller;

import com.yusuf.route.transportation.configuration.TestCacheConfig;
import com.yusuf.route.transportation.security.exceptionhandlers.RestAccessDeniedHandler;
import com.yusuf.route.transportation.security.exceptionhandlers.RestAuthenticationEntryPoint;
import com.yusuf.route.transportation.security.service.DbUserDetailsService;
import com.yusuf.route.transportation.security.service.JwtService;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(TestCacheConfig.class)
public abstract class ControllerTestBase {

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected DbUserDetailsService userDetailsService;

    @MockitoBean
    protected RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockitoBean
    protected RestAccessDeniedHandler restAccessDeniedHandler;
}