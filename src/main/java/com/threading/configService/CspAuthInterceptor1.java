package com.threading.configService;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
//import org.springframework.retry.support.RetryTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CspAuthInterceptor1 {

    private static final UriTemplate CSP_AUTHORIZE_URI_TEMPLATE =
            new UriTemplate("{baseUrl}/csp/gateway/am/api/auth/api-tokens/authorize?refresh_token={refreshToken}");

    private static final String CSP_AUTH_TOKEN_HEADER = "csp-auth-token";

    //private RetryTemplate retryTemplate;
    private int maxRetryAttempts = 5;

    /** Current request URI. */
    private URI requestUri;

    @VisibleForTesting
    RestTemplate restTemplate = new RestTemplate();

    /** The current HTTP headers instance.  This field is exposed for testing purposes. */
    @VisibleForTesting
    HttpHeaders httpHeaders;

    /** The current retry context.  This field is exposed for testing purposes. */

    /**
     * The CSP base URL, e.g. https://dev.csp.vmware.com.
     */
    private final String cspBaseUrl;

    /**
     * UUID representing the CSP refresh token.
     */
    private final String cspRefreshToken;

    /**
     * Fetches a CSP access token given a refresh token.
     *
     * @param cspRefreshToken refresh token
     * @return a CSP access token
     */


    /**
     * Constructor.
     *
     * @param cspBaseUrl      CSP base URL, for example: {@code https://dev.csp.vmware.com}
     * @param cspRefreshToken a CSP refresh token (UUID)
     */
    public CspAuthInterceptor1(String cspBaseUrl, String cspRefreshToken) {
        this.cspBaseUrl = requireNonNull(cspBaseUrl, "cspBaseUrl");
        this.cspRefreshToken = requireNonNull(cspRefreshToken, "cspRefreshToken");

    }

    final LoadingCache<String, String> cspAccessTokenCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new CacheLoader<String, String>() {
                @SuppressWarnings("NullableProblems")
                public String load(String cspRefreshToken) throws Exception {
                    return getCspAccessToken(cspRefreshToken);
                }
            });

    @VisibleForTesting
    private String getCspAccessToken(String cspRefreshToken) {
        URI url = CSP_AUTHORIZE_URI_TEMPLATE.expand(cspBaseUrl, cspRefreshToken);
        this.requestUri = url;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

        HttpEntity request = new HttpEntity<>(httpHeaders);
        try {
            log.info("Getting CSP access token for refresh token: {}", (cspRefreshToken));
            ResponseEntity<AccessToken> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, AccessToken.class);
            return response.getBody().getAccessToken();
        } catch (RestClientException e) {
            log.error("Couldn't get access token for refresh token: {}", (cspRefreshToken));
            throw e;
        }
    }

    @Data
    private static class AccessToken {

        @JsonProperty("access_token")
        private String accessToken;
    }

/*    @Override
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
*/

}
