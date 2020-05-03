/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.configServiceVhs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threading.resolver.CollectorConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration Source that Reads Blackbox Collector Configuration from a folder on the classpath.
 */
@Component("configServiceAsSource")
@Slf4j
public class ConfigurationConfigService implements ConfigurationSource {
    private final ObjectMapper mapper;
    private final String vhsAppname = "sre-vhs-agent-service";

    @Autowired
    private ConfigServiceInterface configOps;

    /**
     * Configuration Source that Reads Blackbox Collector Configuration from a folder on the classpath.
     */
    public ConfigurationConfigService() {
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
    }

    @Override
    public Optional<List<CollectorConfiguration>> getCollectorConfigurations() {
        List<ConfigEntry<CollectorConfiguration>> configEntries;
        ResponseEntity<String> configs;
        //JSONParser parser = new JSONParser();
        //JsonNode json = null;
        ConfigEntry[] configEntry = null;
        try {
            configs = configOps.getConfig(vhsAppname, "");
            //json = (JsonNode) parser.parse(configs.getBody());
            //JsonNode configResponseNode = (JsonNode) json.get("response");

            JsonNode restResponseNode = mapper.readTree(configs.getBody());
            //}));
            JsonNode configResponseNode = restResponseNode.get("configs");
            if (configResponseNode == null) {
                throw new Exception("Response from config service does not have a response key.");
            }
            //configEntry =
             //       mapper.convertValue(configResponseNode, ConfigEntry[].class);

            //log.info((String) configEntry.getValue());
            configEntries =
                    mapper.convertValue(configResponseNode, new TypeReference<List<ConfigEntry<CollectorConfiguration>>>() {
                    });
            List<CollectorConfiguration> collectorConfigurations = configEntries.stream().map(e -> e.getValue()).collect(Collectors.toList());
            return Optional.of(collectorConfigurations);

        } catch (Exception e) {
            log.error("Error getting blackbox configuration: {} {}", e.getClass(), e.getMessage());
            return Optional.empty();
        }
        //log.info(configs.getBody());
        //List<CollectorConfiguration> collectorConfigurations = new ArrayList<>();
        //log.info(collectorConfigurations);

    }
}
