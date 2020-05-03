package com.threading.configService;

import static com.threading.variables.Constants.CONFIG_BASE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class configMain implements configCallsInterface{

    @Autowired
    CspAuthInterceptor cspAuthInterceptor;

    @Value("${internal.console.url}")
    String base;

    @Value("${config.service.config}")
    String CONFIG_SERVICE_SUFFIX;

    @Value("${config.service.appName}")
    String appName;

    @Value("${config.service.configKey}")
    String configKey;

  public static void main(String[] args)
  {

      String path = "https://console.cloud.vmware.com/csp/gateway/am/api/auth/api-tokens/authorize?refresh_token=db133907-a397-4f93-bd73-7a672c6851ca";
      CspAuthInterceptor1 authInterceptor = new CspAuthInterceptor1("https://console.cloud.vmware.com", "db133907-a397-4f93-bd73-7a672c6851ca");

      configCalls c = new configCalls();
      String base = "https://internal.vmc.vmware.com";
      //
      authToken a = new authToken();
      String auth = a.getAuthToken();
      System.out.println(auth);
      c.getCall(auth);
  }

  public void testConfigold()
  {
      configCalls c = new configCalls();
      String base = "https://internal.vmc.vmware.com";
      //
      authToken a = new authToken();
      String auth = a.getAuthToken();
      c.getCall(auth);
  }



    public String testConfig()
    {

        String path = UriComponentsBuilder.fromPath(CONFIG_SERVICE_SUFFIX)
                .buildAndExpand(this.appName, this.configKey).toString();

        path = base + CONFIG_BASE + '/' + path;
        URIBuilder builder = null;

        RestTemplate client = new RestTemplate();
        client.setInterceptors(Collections.singletonList(cspAuthInterceptor));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<String> requestHs = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = null;
        try {
            builder = new URIBuilder(path);
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

        log.info(responseEntity.getBody());
        return "success";
    }

}
