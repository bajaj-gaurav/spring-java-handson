/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.commons;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

/**
 * Processor Configuration.
 */
@Getter
@Setter
public class ProcessorConfiguration {
    String name;
    JsonNode arguments;
}
