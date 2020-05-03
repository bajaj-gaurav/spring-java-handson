/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.configServiceVhs;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import com.threading.resolver.CollectorConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Reads configuration from the disk.
 */
@Component
@Slf4j
public class ConfigurationManager {

    @Qualifier("classpathAsSource")
    private final ConfigurationSource configurationSource;
    private List<CollectorConfiguration> lastGoodConfiguration;

    /**
     * Configuration Manager that syncs with an external
     * @param configurationSource Active Configuration Source.
     */
    public ConfigurationManager(@Qualifier("configServiceAsSource") ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
        this.lastGoodConfiguration = Collections.emptyList();
    }

    public List<CollectorConfiguration> getCollectorConfiguration() {
        return lastGoodConfiguration;
    }

    @PostConstruct
    void postConstruct() {
        sync();
    }

    private void sync() {
        Optional<List<CollectorConfiguration>> optionalConfigurations
                = configurationSource.getCollectorConfigurations();

        optionalConfigurations.ifPresent(collectorConfigurations -> {
            lastGoodConfiguration = collectorConfigurations;
        });
    }
}
