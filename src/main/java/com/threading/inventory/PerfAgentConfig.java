/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 *
 */
@Data
@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "perfAgent")
public class PerfAgentConfig {

    /* Configuration collector-map constants */
    public static final String METRIC_PREFIX = "metricPrefix";
    public static final String INTERVAL = "interval";

    private boolean vmcSddc;
    private String agentId;
    private String wavefrontProxy;
    private String vcHost;
    private String vcUsername;
    @JsonIgnore
    private String vcPassword;      // exclude from REST response
    private String vcThumbprint;
    private String nsxDeploymentType;
    private String nsxManagerHost;
    private String nsxManagerUsername;
    @JsonIgnore
    private String nsxManagerPassword;
    //this overwrites the SDDC SLA TYPE
    private boolean forceProbe;
    private List<String> allowedSlaTypes = new ArrayList<String>();
    private String cmcManifestLocation;

    private Map<String, Map<String, String>> collectors;
    private boolean logMetrics;

    private LoadGates vcLoadGates;

    /**
     *
     */
    @Data
    public static class LoadGates {
        private boolean enabled;
        private Map<String, Double> gates;
    }

}
