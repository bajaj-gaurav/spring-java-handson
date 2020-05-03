/*
 * Copyright (c) 2017 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.UncheckedExecutionException;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Client HTTP request interceptor that retrieves a CSP access token given a refresh token.  It uses this to
 * automatically set the {@code csp-auth-token} HTTP header for all requests.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CspAuthInterceptor extends RetryListenerSupport implements ClientHttpRequestInterceptor,
                                                                        HttpRequestInterceptor, ServiceUnavailableRetryStrategy {

    private static final UriTemplate CSP_AUTHORIZE_URI_TEMPLATE =
            new UriTemplate("{baseUrl}/csp/gateway/am/api/auth/api-tokens/authorize?refresh_token={refreshToken}");

    private static final String CSP_AUTH_TOKEN_HEADER = "csp-auth-token";

    private RetryTemplate retryTemplate;
    private int maxRetryAttempts = 5;

    /** Current request URI. */
    private URI requestUri;

    /**
     * The CSP base URL, e.g. https://dev.csp.vmware.com.
     */
    private final String cspBaseUrl;

    /**
     * UUID representing the CSP refresh token.
     */
    private final String cspRefreshToken;

    /** Used for intercepting the request to get an access token from CSP. */
    @VisibleForTesting
    RestTemplate restTemplate = new RestTemplate();

    /** The current HTTP headers instance.  This field is exposed for testing purposes. */
    @VisibleForTesting
    HttpHeaders httpHeaders;

    /** The current retry context.  This field is exposed for testing purposes. */
    @VisibleForTesting
    RetryContext retryContext;

    @VisibleForTesting
    final LoadingCache<String, String> cspAccessTokenCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<String, String>() {
                @SuppressWarnings("NullableProblems")
                public String load(String cspRefreshToken) throws Exception {
                    return getCspAccessToken(cspRefreshToken);
                }
            });

    /**
     * Constructor.
     *
     * @param cspBaseUrl      CSP base URL, for example: {@code https://dev.csp.vmware.com}
     * @param cspRefreshToken a CSP refresh token (UUID)
     */
    public CspAuthInterceptor(String cspBaseUrl, String cspRefreshToken) {
        this.cspBaseUrl = requireNonNull(cspBaseUrl, "cspBaseUrl");
        this.cspRefreshToken = requireNonNull(cspRefreshToken, "cspRefreshToken");
        createRetryTemplate();
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        return retryTemplate.execute(context -> {
            this.httpHeaders = request.getHeaders();

            // Adds "csp-auth-token" HTTP header.
            addAuthTokenHeader(request.getHeaders());

            // Executes the original request.
            this.requestUri = request.getURI();
            ClientHttpResponse response = execution.execute(request, body);
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.info("401 received for refresh token: {}", mask(cspRefreshToken));
                throw new UnauthorizedException();
            }
            return response;
        });
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                                                 Throwable throwable) {
        // Original request failed.
        log.info("Request to host {} was unsuccessful: {}", requestUri.getHost(), throwable.getMessage());
        log.debug("requestUri={}", requestUri);
        this.retryContext = context;
        if (throwable instanceof UnauthorizedException) {
            // Original request failed with a 401.  Invalidate the existing access token.
            invalidateAccessToken();
        }
    }

    /**
     * Fetches a CSP access token given a refresh token.
     *
     * @param cspRefreshToken refresh token
     * @return a CSP access token
     */
    @VisibleForTesting
    private String getCspAccessToken(String cspRefreshToken) {
        URI url = CSP_AUTHORIZE_URI_TEMPLATE.expand(cspBaseUrl, cspRefreshToken);
        this.requestUri = url;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity request = new HttpEntity<>(httpHeaders);
        try {
            log.info("Getting CSP access token for refresh token: {}", mask(cspRefreshToken));
            ResponseEntity<AccessToken> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, AccessToken.class);
            return response.getBody().getAccessToken();
        } catch (RestClientException e) {
            log.error("Couldn't get access token for refresh token: {}", mask(cspRefreshToken));
            throw e;
        }
    }

    private void addAuthTokenHeader(HttpHeaders headers) {
        try {
            headers.set(CSP_AUTH_TOKEN_HEADER, cspAccessTokenCache.get(cspRefreshToken));
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (e.getCause() instanceof HttpClientErrorException) {
                HttpClientErrorException e2 = (HttpClientErrorException) e.getCause();
                if (e2.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    log.info("Unauthorized CSP refresh token: {}", mask(cspRefreshToken));
                }
            }
            throw Throwables.propagate(e);
        }
    }

    private void invalidateAccessToken() {
        log.info("Invalidating CSP access token for refresh token: {}", mask(cspRefreshToken));
        cspAccessTokenCache.invalidate(cspRefreshToken);
    }

    private void createRetryTemplate() {
        this.retryTemplate = new RetryTemplate();

        // Retry at most 5 times if a 401 or IOException occurs.
        ImmutableMap<Class<? extends Throwable>, Boolean> retryableExceptions =
                ImmutableMap.of(UnauthorizedException.class, true, UncheckedExecutionException.class, true);
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(maxRetryAttempts, retryableExceptions);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);

        // Wait 1 second between retries.
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Registers this class as an error listener in order to invalidate existing tokens.
        retryTemplate.registerListener(this);
    }

    private static String mask(String original) {
        original = StringUtils.defaultString(original);
        return "...****" + StringUtils.leftPad(original, 36, '*').substring(32);
    }

    @Override
    public void process(org.apache.http.HttpRequest request, HttpContext context) throws HttpException, IOException {
        try {
            request.addHeader(CSP_AUTH_TOKEN_HEADER, cspAccessTokenCache.get(cspRefreshToken));
        } catch (ExecutionException e) {
            log.error("Exception when adding csp-auth-token to headers:", e);
        }
    }

    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        if (response.getStatusLine().getStatusCode() == 401 && executionCount <= maxRetryAttempts) {
            log.info("401 received for refresh token: \"{}\", retry {}", mask(cspRefreshToken), executionCount);
            invalidateAccessToken();
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            org.apache.http.HttpRequest request = clientContext.getRequest();
            try {
                request.setHeader(CSP_AUTH_TOKEN_HEADER, cspAccessTokenCache.get(cspRefreshToken));
                return true;
            } catch (ExecutionException e) {
                log.error("Exception when adding csp-auth-token to headers:", e);
            }
        }
        return false;
    }

    @Override
    public long getRetryInterval() {
        return 1000;
    }

    @Data
    private static class AccessToken {

        @JsonProperty("access_token")
        private String accessToken;
    }

    /** Exception thrown if the original request results in HTTP 401 Unauthorized. */
    public static class UnauthorizedException extends RuntimeException {

        private UnauthorizedException() {
            super("Received 401 Unauthorized response using CSP access token");
        }
    }
}
