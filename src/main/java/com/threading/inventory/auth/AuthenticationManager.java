/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.auth;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Authentication Manager which can be used as a Request/Response Interceptor in Apache HTTP Clients.
 */
@Component
@Slf4j
public class AuthenticationManager implements HttpRequestInterceptor, HttpResponseInterceptor {
    public static final String CONTEXT_PROP_AUTH_DELEGATE = "extras.auth.delegate";

    private final AuthenticationConfig config;

    /**
     * Constructor for Authentication Manager.
     */
    @Autowired
    public AuthenticationManager(AuthenticationConfig config) {
        this.config = config;
    }

    /**
     * Check if a delegate with the specified name is registered with the authentication manager.
     * @param delegate Delegate Name.
     * @return boolean indicating the same.
     */
    public boolean hasDelegate(String delegate) {
        return config.getDelegates().containsKey(delegate);
    }

    /**
     * Processes a request.
     * On the client side, this step is performed before the request is
     * sent to the server. On the server side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param request the request to preprocess
     * @param context the context for the request
     * @throws HttpException in case of an HTTP protocol violation
     * @throws IOException   in case of an I/O error
     */
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Object delegateIdentifier = context.getAttribute(CONTEXT_PROP_AUTH_DELEGATE);

        if (delegateIdentifier instanceof String) {
            AuthenticationDelegate delegate = config.getDelegates().get(delegateIdentifier);

            if (delegate != null) {
                delegate.process(request, context);
            }
        }
    }

    /**
     * Processes a response.
     * On the server side, this step is performed before the response is
     * sent to the client. On the client side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param response the response to postprocess
     * @param context  the context for the request
     * @throws HttpException in case of an HTTP protocol violation
     * @throws IOException   in case of an I/O error
     */
    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Object delegateIdentifier = context.getAttribute(CONTEXT_PROP_AUTH_DELEGATE);

        if (delegateIdentifier instanceof String) {
            AuthenticationDelegate delegate = config.getDelegates().get(delegateIdentifier);

            if (delegate != null) {
                delegate.process(response, context);
            }
        }
    }
}
