package com.threading.configService;

/*
 * Copyright (c) 2017 VMware, Inc. All rights reserved. VMware Confidential
 */


import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration required by services api.
 */
@Slf4j
@Data
@Component
@ConfigurationProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceConfiguration {

    @Value("${ssbackend.csp.url}")
    private String cspUrl;

    @Value("${ssbackend.csp.refreshToken}")
    private String cspRefreshToken;

    @Bean
    private CspAuthInterceptor createCspAuthInterceptor() {
        return new CspAuthInterceptor(this.cspUrl, this.cspRefreshToken);
    }

}
