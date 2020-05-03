/**
 * Copyright (c) 2017 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.exception;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;


/**
 * Exception for non 2XX response from REST calls.
 */
public class HttpResponseException extends Exception {

    @Getter
    private HttpStatus httpStatus;

    @Getter
    private String body;

    /**
     * Builds the HttpResponseException from the http status
     * @param httpStatus The Http Status.
     */
    public HttpResponseException(HttpStatus httpStatus) {
        super(MessageFormat.format("HttpStatus: {0}", httpStatus.toString()));
        this.httpStatus = httpStatus;
    }

    /**
     * Builds the HttpResponseException from the http status and body.
     * @param httpStatus the Http Status
     * @param body the Http Body
     */
    public HttpResponseException(HttpStatus httpStatus, String body) {
        super(MessageFormat.format("HttpStatus: {0}, body: {1}", httpStatus.toString(), body));
        this.httpStatus = httpStatus;
        this.body = body;
    }

    /**
     * Builds HttpResponseException from the given ResponseEntity
     * @param responseEntity any ResponseEntity.
     */
    public HttpResponseException(ResponseEntity responseEntity) {
        super(MessageFormat.format("HttpStatus: {0}, body: {1}", responseEntity.getStatusCode().toString(),
                responseEntity.getBody()));
        this.httpStatus = responseEntity.getStatusCode();
        if (responseEntity.hasBody()) {
            this.body = responseEntity.getBody().toString();
        }
    }

    /**
     * Builds HttpResponseException from the given ResponseEntity and url
     * @param responseEntity any ResponseEntity.
     * @param url The original URL requested.
     */
    public HttpResponseException(ResponseEntity responseEntity, String url) {
        super(MessageFormat.format("Url: {0}, HttpStatus: {1}, body: {2}", url,
                responseEntity.getStatusCode().toString(), responseEntity.getBody()));
        this.httpStatus = responseEntity.getStatusCode();
        if (responseEntity.hasBody()) {
            this.body = responseEntity.getBody().toString();
        }
    }
}
