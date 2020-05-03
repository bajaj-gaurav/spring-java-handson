package com.threading.configService;

import static com.threading.variables.Constants.CONFIG_BASE;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class configCalls{

    @Value("${internal.console.url}")
    String base;

    public void getCall(String token)
    {

        System.out.println(base);
        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ2bXdhcmUuY29tOjg5NmY4NDIzLWE1NzktNGYwYi1hNzI3LWE4OTdkNDMxMmY3MyIsImF6cCI6ImNzcF9wcmRfZ2F6X2ludGVybmFsX2NsaWVudF9pZCIsImRvbWFpbiI6InZtd2FyZS5jb20iLCJjb250ZXh0IjoiYWI5Zjg4ZmItMzUwZi00ZWUxLWJmYTctMGM3NGVhMTA5MmYwIiwiaXNzIjoiaHR0cHM6Ly9nYXouY3NwLXZpZG0tcHJvZC5jb20iLCJwZXJtcyI6WyJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjptb25pdG9yaW5nLXJvIiwiZXh0ZXJuYWwveWJVZG9UQzA1a1lGQzlaRzU2MGtwc24wSThNXy92bWMtb3BlcmF0b3I6ZGEtcm8iLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjpybyIsImV4dGVybmFsL3liVWRvVEMwNWtZRkM5Wkc1NjBrcHNuMEk4TV8vdm1jLW9wZXJhdG9yOnJ0cy1zZW5zaXRpdmUiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjptb25pdG9yaW5nLXJ3IiwiY3NwOm9yZ19tZW1iZXIiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy11c2VyOmZ1bGwiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjpkYS1ydyJdLCJjb250ZXh0X25hbWUiOiI5MGQ3ODVhOS1mNWM5LTQxY2UtOTJiNi01ZDUwMWFlMDY4MTAiLCJleHAiOjE1NDQ0MjQwODgsImlhdCI6MTU0NDQyMjI4OCwianRpIjoiZmQ1MDU1NzUtNTVmYy00MjU1LTllMGEtYzNjZTNkYzYyMDA1IiwidXNlcm5hbWUiOiJiYWphamcifQ.KouXSo9zbFuaBaoYvduZPoG11iTSzye2aB8j0DoJZJMGBhsP65WZfeXxyQpGmoJZDfbVtA2gPh-ucjb_7bJ3ckYwTJoC7W0AMwK_QbpVoNlxkpcpwmx-dhdt3b32s0FhjR5_vpO5eyWIwawATS8QJPE-lM3bBzKGfCa8YjNt7y9c44c3X-6N1bztLmo_W5L_eJxrwAy9e6VCgZVsup_48LGpWtlLDWoYcj8Lb3w1yuwrDn7nxvZh_Q1Q0NkOaxh-1LdpQOQm-G3HPGayfFKrHtagId_UjjjtZ5tv46KGFX-E7oTM2LFMnhaxpslIIz85SiSNOejufiWBPsNfo5BU2g";
        String tUrl = CONFIG_BASE + "/apps/{appName}/configs/{configKey}";
        System.out.println(tUrl);
        String appName = "health-engine";
        String configKey = "kpi";


        String path = UriComponentsBuilder.fromPath(tUrl)
                .buildAndExpand(appName, configKey).toString();
        //String base = "https://internal.vmc.vmware.com";

        path = base + path ;

        System.out.println(path);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("csp-auth-token", token );
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<String> result = restTemplate.exchange(path, HttpMethod.GET, entity, String.class);

        System.out.println(result);
    }
}
