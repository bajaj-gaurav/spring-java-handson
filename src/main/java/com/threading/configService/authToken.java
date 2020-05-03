package com.threading.configService;

import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class authToken {

    public String getAuthToken(){

        String path = "https://console.cloud.vmware.com/csp/gateway/am/api/auth/api-tokens/authorize?refresh_token=db133907-a397-4f93-bd73-7a672c6851ca";
        RestTemplate restTemplate = new RestTemplate();

        JSONObject json = null;
        HttpHeaders headers = new HttpHeaders();
        JSONParser parser = new JSONParser();

        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<String> result = restTemplate.exchange(path, HttpMethod.POST, entity, String.class);
        String responseBody = result.getBody();
        try {
             json = (JSONObject) parser.parse(responseBody);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return (String)json.get("access_token");

    }
}
