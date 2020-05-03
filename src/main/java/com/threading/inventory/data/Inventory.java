/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.data;

import java.util.HashSet;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import com.threading.inventory.Type;


/**
 * Stores inventory.
 */
public class Inventory {
    private final Set<Node> nodes;
    private final LoadingCache<Type, Set<Node>> typeLookup;
    private long lastUpdateTime;

    /**
     * Instantiate inventory.
     */
    public Inventory() {
        this.nodes = new HashSet<>();
        this.typeLookup
                = CacheBuilder
                    .newBuilder()
                    .build(CacheLoader.from(
                        (Type type) ->
                                Sets.filter(nodes,
                                    input -> type != null && type.equals(input.getType()))));
    }

    /**
     * Add a node to the inventory.
     * @param node Node to add.
     */
    public void putNode(Node node) {
        synchronized (nodes) {
            nodes.add(node);
            typeLookup.invalidate(node.getType());
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Remove a node from the inventory.
     * @param node Node to remove.
     */
    public void removeNode(Node node) {
        synchronized (nodes) {
            nodes.remove(node);
            typeLookup.invalidate(node.getType());
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    /**
     * Get all nodes in inventory matching the types specified by types.
     * @param types Node Types to lookup for.
     * @return Set of nodes.
     */
    public Set<Node> nodes(Type... types) {
        Set<Node> view = new HashSet<>();
        synchronized (nodes) {
            for (Type type: types) {
                view.addAll(typeLookup.getUnchecked(type));
            }
        }
        return view;
    }

    /**
     * Get seconds since last touch to the inventory.
     * @return Age in seconds.
     */
    public long age() {
        return (System.currentTimeMillis() - lastUpdateTime) / 1000;
    }
}
