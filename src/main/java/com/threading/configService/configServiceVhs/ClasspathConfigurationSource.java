/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.configServiceVhs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.threading.resolver.CollectorConfiguration;


import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Source that Reads Blackbox Collector Configuration from a folder on the classpath.
 */
@Component("classpathAsSource")
@Slf4j
public class ClasspathConfigurationSource implements ConfigurationSource {
    private final ResourcePatternResolver resolver;
    private final ObjectMapper mapper;

    private static final String LOCATION_PATTERN_FORMAT = "classpath:%s/*.json";
    private final String locationPattern;

    /**
     * Configuration Source that Reads Blackbox Collector Configuration from a folder on the classpath.
     * @param prefix Relative Path from Classpath.
     */
    public ClasspathConfigurationSource(@Value("collectors") String prefix) {
        Preconditions.checkArgument(!StringUtils.isEmpty(prefix));
        this.locationPattern = String.format(LOCATION_PATTERN_FORMAT, prefix);

        this.resolver = new PathMatchingResourcePatternResolver();
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    public Optional<List<CollectorConfiguration>> getCollectorConfigurations() {
        Resource[] files;
        try {
            files = resolver.getResources(locationPattern);
        } catch (Exception e) {
            log.error("Error getting blackbox configuration: {} {}", e.getClass(), e.getMessage());
            return Optional.empty();
        }

        List<CollectorConfiguration> collectorConfigurations = new ArrayList<>();
        for (Resource file: files) {
            try {
                CollectorConfiguration configuration = mapper.readValue(
                        file.getInputStream(),
                        new TypeReference<CollectorConfiguration>() {
                        }
                );

                log.info("Loaded blackbox configuration: {}", file.getFilename());
                collectorConfigurations.add(configuration);
            } catch (IOException e) {
                log.error("Error reading blackbox configuration: {}", file.getFilename());
            }
        }

        return Optional.of(collectorConfigurations);
    }
}
