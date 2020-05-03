/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.configServiceSddc;


import java.util.Optional;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threading.configService.configServiceVhs.ConfigEntry;
import com.threading.configService.configServiceVhs.ConfigServiceInterface;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration for the Sddc ResourceConfig.
 * This Implementation Auto refreshes itself on a scheduled frequency.
 */
@Slf4j
@Data
@Component
@JsonIgnoreProperties(ignoreUnknown = true)
public class SddcResourceConfig {

    private static final long REFRESH_DELAY_IN_SECS = 180L;

    private static final String CONFIG_KEY = "resourceConfig";



    //private static SddcResourceConfigObserver sddcResourceConfigObserver = new SddcResourceConfigObserver();

    //@Setter
    //private static SddcResourceConfig instance;

    // Instantiates SddcResourceConfig and register as observer for any config-change.
/*    static {
        try {
            instance = loadPopConfig(CONFIG_KEY, new TypeReference<SddcResourceConfig>() {});
            instance.registerForRefresh(REFRESH_DELAY_IN_SECS);
            instance.addObserver(sddcResourceConfigObserver);
        } catch (ClientException e) {
            log.error("Failed to load 'resourceConfig' for Sddc. Returning to Default Config");
            instance = new SddcResourceConfig();
        }
    }*/


    @Autowired
    private ConfigServiceInterface configOps;

    private final ObjectMapper mapper;


    public SddcResourceConfig()
    {
        this.mapper = new ObjectMapper();
    }
    /**
     * Gets an instance of the SddcResourceConfig.
     */
    public Optional<SddcResourceConfigConfiguration> get() {
        ConfigEntry<SddcResourceConfigConfiguration> configEntries;
        ResponseEntity<String> configs;
        JSONParser parser = new JSONParser();
        JsonNode json = null;

        try {
            configs = configOps.getConfigSddc( "00b2c079-06cb-48d2-bb6f-c30b949f481a", "resourceConfig");
            //json = (JsonNode) parser.parse(configs.getBody());
            //JsonNode configResponseNode = (JsonNode) json.get("response");

            JsonNode restResponseNode = mapper.readTree(configs.getBody());
            //}));
            JsonNode configResponseNode = restResponseNode.get("response");
            if (configResponseNode == null) {
                throw new Exception("Response from config service does not have a response key.");
            }

            configEntries =
                    mapper.convertValue(configResponseNode, new TypeReference<ConfigEntry<SddcResourceConfigConfiguration>>() {});

            boolean hello = configEntries.getValue().isNsxt();
            return Optional.of(configEntries.getValue());


            //configEntry =
            //       mapper.convertValue(configResponseNode, ConfigEntry[].class);

            //log.info((String) configEntry.getValue());
            //configEntries =
            //        mapper.convertValue(configResponseNode, new TypeReference<List<ConfigEntry<CollectorConfiguration>>>() {
             //       });
            //List<CollectorConfiguration> collectorConfigurations = configEntries.stream().map(e -> e.getValue()).collect(
             //       Collectors.toList());
            //return Optional.of(configEntries.getValue());

        } catch (Exception e) {
            log.error("Error getting blackbox configuration: {} {}", e.getClass(), e.getMessage());
            return Optional.empty();
        }

    }



/*    @Override
    protected void refresh() {
        try {
            SddcResourceConfig newSddcResourceConfig = loadPopConfig(CONFIG_KEY,
                                                                     new TypeReference<SddcResourceConfig>() {});
            instance.hasChanged(newSddcResourceConfig);
        } catch (ClientException e) {
            log.error("Failed to load 'resourceConfig' for Sddc");
        }
    }*/

/*    *//**
     * Registers a SddcResourceConfig for Refresh.
     * @param delay the delay in Seconds for refresh Schedule.
     *//*
    protected void registerForRefresh(long delay) {
        log.debug("Registering config for Refresh. Config={}", this.getClass().getSimpleName());
        ScheduledFuture scheduledFuture = CONFIG_REFRESH_EXECUTOR_SERVICE
                .safeScheduleWithFixedDelay(this::refresh, 0, delay, TimeUnit.SECONDS);
        // Add to Configuration Manager for clean shutdown.
        GlobalConfig.getInstance().getConfigurationManager().addConfig(this, scheduledFuture);
        log.info("Registered config for refresh. Config={}", this.getClass().getSimpleName());
    }*/

/*
    // Observer for  SddcResourceConfig.
    private static class SddcResourceConfigObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            ApeEvent event = (ApeEvent) arg;

            if ((event.getType().equals(ApeEvent.EventType.CONFIG_CHANGED))) {
                log.debug("Changed Detected in config={}", CONFIG_KEY);
                SddcResourceConfig sddcResourceConfig = (SddcResourceConfig) event.getArg();
                sddcResourceConfig.addObserver(SddcResourceConfig.sddcResourceConfigObserver);
                instance = sddcResourceConfig;
                log.info("Updated config={}", CONFIG_KEY);
            }
        }
    }
*/

}
