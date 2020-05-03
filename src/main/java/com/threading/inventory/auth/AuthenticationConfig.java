/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.auth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import com.threading.inventory.PerfAgentConfig;
import com.threading.inventory.common.BasicAuthDelegate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Config for Authentication Manager.
 */
@Component
@Slf4j
public class AuthenticationConfig {
    public static final String DELEGATE_VCENTER_CIS = "vcenter-cis";
    public static final String DELEGATE_NSX_MANAGER_BASICAUTH = "nsx-manager-basic-auth";

    @Getter
    private final Map<String, AuthenticationDelegate> delegates;

    @Autowired
    private PerfAgentConfig config;
    @Autowired
    @Lazy
    private CloseableHttpAsyncClient httpAsyncClient;

    AuthenticationConfig() {
        delegates = new HashMap<>();
    }

    @PostConstruct
    void setup() {
        // TODO: Make this Configuration Driven & keep a single exception handler.

        try {
            if (StringUtils.isNotEmpty(config.getNsxManagerUsername())
                    && StringUtils.isNotEmpty(config.getNsxManagerPassword())) {
                delegates.put(DELEGATE_NSX_MANAGER_BASICAUTH,
                        new BasicAuthDelegate(
                                config.getNsxManagerUsername(),
                                config.getNsxManagerPassword()));
            } else {
                log.error("NSX Credential Validation Failed");
            }
        } catch (Exception e) {
            log.error("Exception thown while creating Authentication Delegate for vCenter: {} {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }
    }
}
