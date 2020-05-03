/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.inventory;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import com.threading.inventory.auth.AuthenticationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Globals.
 */
@Component
@Slf4j
public class Globals {
    /**
     * Configures Async HttpClient which can be used by other collectors.
     * @param authenticationManager Authentication Manager to be used by this HttpClient.
     * @return ClosableHttpAsyncClient.
     */
    @Bean
    public CloseableHttpAsyncClient createAsyncHttpClient(AuthenticationManager authenticationManager) {
        HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();

        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, ((chain, authType) -> true))
                    .build();
            clientBuilder.setSSLContext(sslContext);
            clientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Error creating an all-trusting SSLContext. Using default; Self Signed Certs would be rejected.");
        }

        clientBuilder.addInterceptorFirst((HttpRequestInterceptor) authenticationManager);
        clientBuilder.addInterceptorFirst((HttpResponseInterceptor) authenticationManager);
        CloseableHttpAsyncClient client = clientBuilder.build();
        client.start();
        return client;
    }
}
