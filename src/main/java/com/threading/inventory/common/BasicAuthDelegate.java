/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.common;

import java.io.IOException;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import com.google.common.base.Charsets;
import com.threading.inventory.auth.AuthenticationDelegate;


/**
 * Authentication Delegate for Basic Auth. Accepts Username and Password and adds the `Authorization` Header.
 */
public class BasicAuthDelegate implements AuthenticationDelegate {
    private final Header basicAuthHeader;

    /**
     * Basic Authentication Interceptor based on Username & Password.
     * Adds the `Authorization` Header.
     * @param username Username.
     * @param password Password.
     */
    public BasicAuthDelegate(String username, String password) {
        String encodedCredentials
                = Base64.getEncoder()
                .encodeToString(
                        (username + ":" + password)
                                .getBytes(Charsets.UTF_8));

        basicAuthHeader = new BasicHeader("Authorization", "Basic " + encodedCredentials);
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        // Add Authorization Header.
        request.addHeader(basicAuthHeader);
    }

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        // No-op.
    }
}
