/**
 * Copyright (c) 2017 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.threading.configService.exception.HttpResponseException;
import com.threading.configService.util.RestTemplateErrorHandler;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Base client.
 */
@Slf4j
@Component
public class RestClient {

    @Getter
    protected RestTemplate restTemplate;
    @Getter
    protected RetryTemplate retryTemplate;
    protected ObjectMapper objectMapper;

    @Autowired
    CspAuthInterceptor cspAuthInterceptor;

    /**
     * Creates client instance.
     */
    public RestClient() {
        this.restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());

        configureObjectMapper();
        configureRetryTemplate();
    }

    /**
     * Create rest client with basic authorization.
     * @param username username for basic auth
     * @param password password for basic auth
     */
    public RestClient(String username, String password) {
        this.restTemplate = new RestTemplateBuilder().errorHandler(new RestTemplateErrorHandler())
                .basicAuthorization(username, password).build();
        configureObjectMapper();
        configureRetryTemplate();
    }

    private void configureRetryTemplate() {
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(100);
        backOffPolicy.setMaxInterval(4000);
        RetryPolicy retryPolicy =
                new SimpleRetryPolicy(3, Collections.singletonMap(HttpResponseException.class, true));
        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setRetryPolicy(retryPolicy);
        this.retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    private void configureObjectMapper() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    /**
     * Executes a rest API call for the passed in HTTP method type.
     * @param uri endpoint URL
     * @param httpMethod HTTP Method [GET, POST, PUT, DELETE, OPTIONS]
     * @param httpEntity the request body as an httpEntity
     * @param responseType the response type expected to returned from the request.
     * @param <T> any response type.
     * @return ResponseEntity.
     * @throws HttpResponseException any HTTP response Exception.
     */
    public <T> ResponseEntity<T> exchange(URI uri, HttpMethod httpMethod, HttpEntity httpEntity,
                                             ParameterizedTypeReference<T> responseType)
            throws HttpResponseException {

        ResponseEntity<T> responseEntity = retryTemplate.execute(context -> {
            log.debug("Making rest call using restTemplate.exchange, attempt : {}", context.getRetryCount());

            this.restTemplate.setInterceptors(Collections.singletonList(cspAuthInterceptor));
            ResponseEntity<T> response = restTemplate.exchange(uri, httpMethod, httpEntity, responseType);
            if (response.getStatusCode().is5xxServerError()) {
                log.warn("Server error from the api: {}, code: {}", response.getBody(), response.getStatusCode());
                throw new HttpResponseException(response);
            }
            log.debug("Response from rest call : {}, code: {}", response.getBody(), response.getStatusCode());
            return response;
        });

        return responseEntity;
    }
}
