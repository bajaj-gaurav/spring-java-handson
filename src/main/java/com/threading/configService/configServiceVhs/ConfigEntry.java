package com.threading.configService.configServiceVhs;

/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */


import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a config entry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
@Data
@Slf4j
public class ConfigEntry<T> {

    private UUID id;

    //private UUID userId;

    private String username;

    @NotEmpty(message = "The field `key`(String) is either missing or null.")
    private String key;

    @NotNull
    private T value;

    private long version;

    private boolean isDelete;

    public ConfigEntry() {
    }

    public ConfigEntry(String key, T value) {
        this.key = key;
        this.value = value;
    }

}
