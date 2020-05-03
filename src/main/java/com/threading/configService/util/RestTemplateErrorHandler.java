/**
 * Copyright (c) 2017 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.util;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * ErrorHandler to override default behaviour of RestTemplate for non 2XX response codes.
 * Setting this error handler to RestTemplate prevents it from throwing RuntimeExceptions for non 2XX responses.
 */
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

    }
}
