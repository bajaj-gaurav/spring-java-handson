/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.data;

import java.util.Objects;


import com.threading.inventory.Type;

import lombok.Getter;
import lombok.Setter;

/**
 * Data class to store information about a node.
 */
@Getter
public class Node {
    private final String id;
    private final String name;
    private final Type type;

    @Setter
    private String address;

    public Node(String id, String name, Type type) {
        this(id, name, type, null);
    }

    /**
     *
     * @param id Node ID.
     * @param name Node Name.
     * @param type Node Type.
     * @param address Node Address.
     */
    public Node(String id, String name, Type type, String address) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public boolean hasManagedObject() {
        return false;
    }

    public boolean hasAddress() {
        return address != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(getId(), node.getId())
                && getType() == node.getType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getType());
    }
}

