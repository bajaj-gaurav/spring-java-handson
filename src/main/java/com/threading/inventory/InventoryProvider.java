/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

import java.util.Optional;
import java.util.Set;

import com.threading.inventory.data.Inventory;


/**
 * Inventory Provider.
 */
public interface InventoryProvider {
    Set<? extends Type> types();

    Inventory initial();

    Optional<Inventory> synchronize();
}
