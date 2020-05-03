/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.configServiceVhs;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;

import com.threading.resolver.CollectorConfiguration;


/**
 * Configuration Source/Backend. Primary User is ConfigurationConfigService.
 * Can be used to externalize configuration in the future.cvh
 */
public interface ConfigurationSource {
    Optional<List<CollectorConfiguration>> getCollectorConfigurations();
}
