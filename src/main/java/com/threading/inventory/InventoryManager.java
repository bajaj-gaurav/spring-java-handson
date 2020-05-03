/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.threading.inventory.data.Inventory;
import com.threading.inventory.data.Node;

import lombok.extern.slf4j.Slf4j;

/**
 * Inventory Manager.
 */
@Component
@Slf4j
public class InventoryManager {
    private final InventoryProviderConfig config;

    private final Map<String, Inventory> inventoryMap;

    private Stopwatch stopwatch = Stopwatch.createUnstarted();

    /**
     * Search Node Types in inventory.
     * @param type Type of Node to search for.
     * @return Set of Nodes matching the criteria.
     */
    public Set<Node> search(Type type) {
        Collection<Inventory> inventories;

        synchronized (inventoryMap) {
            inventories = new ArrayList<>(inventoryMap.values());
        }

        return inventories
                .stream()
                .flatMap(inventory -> inventory.nodes(type).stream())
                .collect(Collectors.toSet());
    }

    /**
     * Constructor for Inventory Manager.
     *
     */
    @Autowired
    public InventoryManager(InventoryProviderConfig config) {
        Preconditions.checkNotNull(config);

        this.config = config;
        this.inventoryMap = new ConcurrentHashMap<>();
        this.config.getProviders()
            .forEach((name, provider) -> {
                // It is the initial value
                //nsxManager = new Node("nsx_manager", "NSX Manager", NsxVType.NSX_MANAGER, host);
                //initial has the value value of the nsxManager above
                inventoryMap.put(name, provider.initial());
            });
    }

    @Scheduled(initialDelay = 30000L, fixedDelay = 300000L)
    void inventorySync() {
        stopwatch.reset();

        log.info("Inventory synchronization started");
        config
                .getProviders()
                .forEach((name, provider) -> {
                    log.info("Inventory Synchronization for \"{}\" started", name);
                    stopwatch.reset();
                    stopwatch.start();
                    log.info(name);

                    Optional<Inventory> optionalInventory = provider.synchronize();

                    stopwatch.stop();
                    log.info("Inventory Synchronization for \"{}\" finished as {} in {}s",
                            name,
                            optionalInventory.isPresent() ? "SUCCESSFUL" : "UNSUCCESSFUL",
                            stopwatch.elapsed(TimeUnit.SECONDS));

                    optionalInventory.ifPresent(inventory -> {
                        synchronized (inventoryMap) {
                            /*
                            id:            nsx_manager, edge-1,   edge-2,         edge-3,          controller-5,          controller-4,          controller-6,
                            name:          NSX Manager, sddc-mgw, SDDC-CGW-1-esg, SDDC-CGW-1-dlr,  restored-controller-2  restored-controller-1, restored-controller-3
                            type:
                            name:          NSX_MANAGER  NSX_EDGE,  NSX_EDGE,      NSX_EDGE,       NSX_CONTROLLER
                            sourceFormat:  nsx.manager, nsx.%s,    nsx.%s,        nsx.%s,         nsx.%s
                            address:       10.56.126.5, null,null
                             */
                            inventoryMap.put(name, inventory);
                        }
                    });
                });
        log.info("Inventory synchronization finished");
    }
}

