/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.nsx;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.net.InternetDomainName;
import com.threading.inventory.InventoryProvider;
import com.threading.inventory.Type;
import com.threading.inventory.auth.AuthenticationConfig;
import com.threading.inventory.auth.AuthenticationManager;
import com.threading.inventory.data.Inventory;
import com.threading.inventory.data.Node;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Inventory Client which refreshes and populates inventory from NSX Manager for NSX-V.
 */
@Slf4j
public class NsxVInventoryProvider implements InventoryProvider {
    private static final String NSX_MANAGER_BASE_TEMPLATE = "https://%s";
    private final ObjectMapper mapper;

    private final CloseableHttpAsyncClient client;
    private final HttpContext context;

    private final String nsxManagerBase;

    private final Inventory initial;
    private final Node nsxManager;

    /**
     * Constructs NSX Inventory Client.
     */
    public NsxVInventoryProvider(
            CloseableHttpAsyncClient client,
            ObjectMapper mapper,
            String host) {
        Preconditions.checkArgument(!StringUtils.isEmpty(host));
/*        Preconditions.checkArgument(
                InetAddressValidator.getInstance().isValid(host)
                || InternetDomainName.isValid(host));

        if (InetAddressValidator.getInstance().isValid(host)) {
            try {
                InetAddress nsxManagerHostAddress = InetAddress.getByName(host);
                host = nsxManagerHostAddress.getCanonicalHostName();
            } catch (UnknownHostException e) {
                log.error(
                        "UnknownHostException while resolving the hostname for NSX Manager [\"{}\"], exception: \"{}\"",
                        host, e.getMessage());
            }
        }*/

        log.info("Using host \"{}\" to connect to NSX Manager", host);
        nsxManagerBase = String.format(NSX_MANAGER_BASE_TEMPLATE, host);

        initial = new Inventory();
        nsxManager = new Node("nsx_manager", "NSX Manager", NsxVType.NSX_MANAGER, host);
        initial.putNode(nsxManager);

        this.client = client;
        this.context = new HttpCoreContext();
        this.context.setAttribute(
                AuthenticationManager.CONTEXT_PROP_AUTH_DELEGATE, AuthenticationConfig.DELEGATE_NSX_MANAGER_BASICAUTH);

        this.mapper = mapper;
    }

    private Optional<List<NsxEdge>> getEdges() {
        try {
            HttpUriRequest request = new HttpGet(nsxManagerBase + Endpoints.GET_ALL_EDGES);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");

            HttpResponse response = client.execute(request,
                                                   context, new FutureCallback<HttpResponse>() {
                        @Override
                        public void completed(HttpResponse result) { }

                        @Override
                        public void failed(Exception ex) { }

                        @Override
                        public void cancelled() { }
                    }).get(5, TimeUnit.SECONDS);

            byte[] content = EntityUtils.toByteArray(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JsonNode node = mapper.readValue(content, JsonNode.class);
                List<NsxEdge> edges = mapper.convertValue(node.get("edgePage").get("data"),
                        new TypeReference<List<NsxEdge>>() {});

                return Optional.of(edges);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Getting edges failed with status code: {}", e.getStatusCode());
        } catch (Exception e) {
            log.error("Getting edges failed with Exception: {}", e.getMessage());
        }

        return Optional.empty();
    }

    private Optional<List<NsxController>> getControllers() {
        try {
            HttpUriRequest request = new HttpGet(nsxManagerBase + Endpoints.GET_ALL_CONTROLLERS);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");

            HttpResponse response = client.execute(request,
                                                   context, new FutureCallback<HttpResponse>() {
                        @Override
                        public void completed(HttpResponse result) { }

                        @Override
                        public void failed(Exception ex) { }

                        @Override
                        public void cancelled() { }
                    }).get(5, TimeUnit.SECONDS);

            byte[] content = EntityUtils.toByteArray(response.getEntity());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                JsonNode node = mapper.readValue(content, JsonNode.class);
                List<NsxController> controllers = mapper.convertValue(node.get("controllers"),
                        new TypeReference<List<NsxController>>() {});

                return Optional.of(controllers);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Getting edges failed with status code: {}", e.getStatusCode());
        } catch (Exception e) {
            log.error("Getting edges failed with Exception: {}", e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Set<? extends Type> types() {
        return NsxVType.names();
    }

    @Override
    public Inventory initial() {
        return initial;
    }

    /**
     * Synchronizes inventory from NSX Manager for NSX-V.
     */
    @Override
    public synchronized Optional<Inventory> synchronize() {
        Optional<List<NsxEdge>> optionalEdges = getEdges();
        Optional<List<NsxController>> optionalControllers = getControllers();
        if (optionalEdges.isPresent() && optionalControllers.isPresent()) {
            Inventory inventory = new Inventory();
            optionalEdges.ifPresent(edges -> {
                edges.forEach(edge -> {
                    inventory.putNode(new Node(edge.getId(), edge.getName(), NsxVType.NSX_EDGE));
                });
            });
            optionalControllers.ifPresent(controllers -> {
                controllers.forEach(controller -> {
                    inventory.putNode(new Node(controller.getId(), controller.getName(), NsxVType.NSX_CONTROLLER));
                });
            });

            inventory.putNode(nsxManager);
            return Optional.of(inventory);
        }

        return Optional.empty();
    }

    private static class Endpoints {
        private static String GET_ALL_EDGES = "/api/4.0/edges";
        private static String GET_ALL_CONTROLLERS = "/api/2.0/vdn/controller";
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NsxEdge {
        String id;
        String name;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NsxController {
        String id;
        String name;
    }

}
