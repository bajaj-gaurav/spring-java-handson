/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory.auth;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;

/**
 * Authentication Delegate intercepts both Requests and Responses and takes appropriate action.
 * Implementations should be thread-safe if required.
 */
public interface AuthenticationDelegate extends HttpRequestInterceptor, HttpResponseInterceptor {
}
