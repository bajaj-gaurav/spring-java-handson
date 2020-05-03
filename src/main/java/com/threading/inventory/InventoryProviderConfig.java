/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.threading.inventory.nsx.NsxVInventoryProvider;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for Inventory Provider.
 */
@Component
@Slf4j
public class InventoryProviderConfig {
    private static final String VCENTER_KEY = "VCENTER";
    private static final String NSXV_KEY = "NSXV";
    private static final String NSXT_KEY = "NSXT";

    final PerfAgentConfig config;

    @Autowired
    ApplicationContext context;

    @Autowired
    CloseableHttpAsyncClient client;

    @Getter
    private final Map<String, InventoryProvider> providers;

    /**
     * Constructor for Inventory Provider Configuration.
     * @param config Perf Agent Configuration.
     */
    public InventoryProviderConfig(PerfAgentConfig config) {
        Preconditions.checkNotNull(config);

        this.config = config;
        providers = new ConcurrentHashMap<>();
    }

    @PostConstruct
    void setup() {


        if (StringUtils.isNotEmpty(config.getNsxManagerHost())) {
            try {
                ObjectMapper mapper = new ObjectMapper();

                // Binding to Deployment Type is Dangerous as this variable is injected at PoP (Re)-deployment times.
                // We should target to be compatible with NSX-V to NSX-T Migration with 0 downtime to the agent.
                // TODO: Investigate a way of determining NSX Type and Version at Runtime and fork
                if (NSXV_KEY.equals(config.getNsxDeploymentType())) {
                    providers.put(
                            NSXV_KEY,
                            new NsxVInventoryProvider(
                                    client,
                                    mapper,
                                    config.getNsxManagerHost()));
                }  else {
                    log.error("Inventory Provider Initialization failed for NSX: "
                                + "Incompatible NSX Deployment Type \"{}\"", config.getNsxDeploymentType());
                }
            } catch (Exception e) {
                log.error("Inventory Provider Initialization failed for NSX (type={}): {} {}",
                        config.getNsxDeploymentType(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
            }
        }
    }
}
