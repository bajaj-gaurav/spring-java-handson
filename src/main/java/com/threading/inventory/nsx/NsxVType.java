/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.nsx;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.threading.inventory.Type;


/**
 * Inventory Node Types exposed by NSX Type.
 */
public class NsxVType extends Type {
    public static final NsxVType NSX_MANAGER = new NsxVType("NSX_MANAGER", "nsx.manager");
    public static final NsxVType NSX_EDGE = new NsxVType("NSX_EDGE","nsx.%s");
    public static final NsxVType NSX_CONTROLLER = new NsxVType("NSX_CONTROLLER", "nsx.%s");

    private static Set<NsxVType> names = ImmutableSet.of(NSX_MANAGER, NSX_EDGE, NSX_CONTROLLER);

    public static Set<NsxVType> names() {
        return names;
    }

    private String name;
    private String sourceFormat;

    protected NsxVType(String name, String sourceFormat) {
        this.name = name;
        this.sourceFormat = sourceFormat;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String getSourceFormat() {
        return sourceFormat;
    }
}
