/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

/**
 * Types of Nodes. Can be extended by an enum/class as required.
 */
public abstract class Type {
    // NOTE: The Inventory Manager and other deserialization code assumes that Types are "unique" and,
    //       same names are not shared across other types.

    public abstract String name();

    protected abstract String getSourceFormat();

    /**
     * Get formatted string from host
     *
     * @param identifier name of the host.
     * @return formatted host name.
     */
    public String asSource(String identifier) {
        return String.format(getSourceFormat(),
                identifier == null ? "unknown" : identifier.replace(".", "-"));
    }
}
