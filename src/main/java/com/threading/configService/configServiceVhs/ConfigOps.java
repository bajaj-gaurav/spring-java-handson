package com.threading.configService.configServiceVhs;



import static com.threading.variables.Constants.CONFIG_BASE;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.core.Response;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import com.threading.configService.CspAuthInterceptor;
import com.threading.configService.RestClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ConfigOps implements ConfigServiceInterface{

    @Autowired
    CspAuthInterceptor cspAuthInterceptor;

    @Autowired
    ConfigServiceConfig configServiceConfig;

    @Autowired
    RestClient restClient;

    private URIBuilder builder;
    private final RestTemplate client;

    public ConfigOps() {
        this.client = new RestTemplate();
    }

/*    public ResponseEntity<String> getConfig(String appName, String configKey)
    {
        String path = UriComponentsBuilder.fromPath(configServiceConfig.getConfigServiceSuffix())
                .buildAndExpand(appName, configKey).toString();

        path = configServiceConfig.getInternalConsoleUrl() + CONFIG_BASE + '/' + path;
        this.client.setInterceptors(Collections.singletonList(cspAuthInterceptor));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> requestHs = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        try {
            this.builder = new URIBuilder(path);
            URI uri = builder.build();
            log.info("Path: " + uri);

            responseEntity = client.exchange(uri, HttpMethod.GET, requestHs, String.class);

        } catch (HttpStatusCodeException e) {
            log.error("Non-success status code while hitting url: " + e.getResponseBodyAsString());
            responseEntity = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            log.error("Exception while hitting url: " + e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }*/


    public ResponseEntity<String> getConfig(String appName, String configKey)
    {
        String path = UriComponentsBuilder.fromPath(configServiceConfig.getConfigServiceSuffix())
                .buildAndExpand(appName, configKey).toString();

        path = configServiceConfig.getInternalConsoleUrl() + CONFIG_BASE + '/' + path;
        //this.client.setInterceptors(Collections.singletonList(cspAuthInterceptor));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> requestHs = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        try {
            this.builder = new URIBuilder(path);
            URI uri = builder.build();
            log.info("Path: " + uri);

            //responseEntity = client.exchange(uri, HttpMethod.GET, requestHs, String.class);
            responseEntity = restClient.exchange(uri, HttpMethod.GET,  requestHs, new ParameterizedTypeReference<String>(){});

        } catch (HttpStatusCodeException e) {
            log.error("Non-success status code while hitting url: " + e.getResponseBodyAsString());
            responseEntity = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            log.error("Exception while hitting url: " + e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }


    public ResponseEntity<String> getConfigSddc(String sddcName, String configKey)
    {
        String path = UriComponentsBuilder.fromPath(configServiceConfig.getConfigServiceSuffixSddc())
                .buildAndExpand(sddcName, configKey).toString();

        path = configServiceConfig.getConfigServiceUrl() + CONFIG_BASE + '/' + path;
        this.client.setInterceptors(Collections.singletonList(cspAuthInterceptor));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> requestHs = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        try {
            this.builder = new URIBuilder(path);
            URI uri = builder.build();
            log.info("Path: " + uri);
            responseEntity = client.exchange(uri, HttpMethod.GET, requestHs, String.class);

        } catch (HttpStatusCodeException e) {
            log.error("Non-success status code while hitting url: " + e.getResponseBodyAsString());
            responseEntity = new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            log.error("Exception while hitting url: " + e);
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
