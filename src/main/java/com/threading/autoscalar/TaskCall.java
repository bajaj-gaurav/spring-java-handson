package com.threading.autoscalar;

import static com.threading.variables.Constants.AUTOSCALAR_BASE_FOR_TASK;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threading.wavefrontUploader.WavefrontPayloadUploader;

import lombok.Data;

public class TaskCall {

    public void taskCallActiveTask(String token, String org_id)
    {

        WavefrontPayloadUploader wf = new WavefrontPayloadUploader();
        ObjectMapper mapper = new ObjectMapper();

        //String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ2bXdhcmUuY29tOjg5NmY4NDIzLWE1NzktNGYwYi1hNzI3LWE4OTdkNDMxMmY3MyIsImF6cCI6ImNzcF9wcmRfZ2F6X2ludGVybmFsX2NsaWVudF9pZCIsImRvbWFpbiI6InZtd2FyZS5jb20iLCJjb250ZXh0IjoiYWI5Zjg4ZmItMzUwZi00ZWUxLWJmYTctMGM3NGVhMTA5MmYwIiwiaXNzIjoiaHR0cHM6Ly9nYXouY3NwLXZpZG0tcHJvZC5jb20iLCJwZXJtcyI6WyJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjptb25pdG9yaW5nLXJvIiwiZXh0ZXJuYWwveWJVZG9UQzA1a1lGQzlaRzU2MGtwc24wSThNXy92bWMtb3BlcmF0b3I6ZGEtcm8iLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjpybyIsImV4dGVybmFsL3liVWRvVEMwNWtZRkM5Wkc1NjBrcHNuMEk4TV8vdm1jLW9wZXJhdG9yOnJ0cy1zZW5zaXRpdmUiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjptb25pdG9yaW5nLXJ3IiwiY3NwOm9yZ19tZW1iZXIiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy11c2VyOmZ1bGwiLCJleHRlcm5hbC95YlVkb1RDMDVrWUZDOVpHNTYwa3BzbjBJOE1fL3ZtYy1vcGVyYXRvcjpkYS1ydyJdLCJjb250ZXh0X25hbWUiOiI5MGQ3ODVhOS1mNWM5LTQxY2UtOTJiNi01ZDUwMWFlMDY4MTAiLCJleHAiOjE1NDQ0MjQwODgsImlhdCI6MTU0NDQyMjI4OCwianRpIjoiZmQ1MDU1NzUtNTVmYy00MjU1LTllMGEtYzNjZTNkYzYyMDA1IiwidXNlcm5hbWUiOiJiYWphamcifQ.KouXSo9zbFuaBaoYvduZPoG11iTSzye2aB8j0DoJZJMGBhsP65WZfeXxyQpGmoJZDfbVtA2gPh-ucjb_7bJ3ckYwTJoC7W0AMwK_QbpVoNlxkpcpwmx-dhdt3b32s0FhjR5_vpO5eyWIwawATS8QJPE-lM3bBzKGfCa8YjNt7y9c44c3X-6N1bztLmo_W5L_eJxrwAy9e6VCgZVsup_48LGpWtlLDWoYcj8Lb3w1yuwrDn7nxvZh_Q1Q0NkOaxh-1LdpQOQm-G3HPGayfFKrHtagId_UjjjtZ5tv46KGFX-E7oTM2LFMnhaxpslIIz85SiSNOejufiWBPsNfo5BU2g";
        //String tUrl = CONFIG_BASE + "/apps/{appName}/configs/{configKey}";
        //System.out.println(tUrl);
        //String appName = "health-engine";
        //String configKey = "kpi";

        String path;
        //String path = UriComponentsBuilder.fromPath(tUrl)
        //        .buildAndExpand(appName, configKey).toString();
        String base = "https://vmc.vmware.com";
        //String path = "vmc/api/operator/tasks?$filter=org_id eq b205209d-50ed-4960-ab14-f135592a9186 and created gt 2018-12-31 and status eq 'STARTED'";
        String tail = "?$filter=org_id eq " + org_id + " and created gt 2018-12-31 and status eq \'STARTED\'";
        path = base + AUTOSCALAR_BASE_FOR_TASK + tail ;

        //System.out.println(path);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("csp-auth-token", token );
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<List<SddcOutputInterface>> result = restTemplate.exchange(path, HttpMethod.GET, entity, new ParameterizedTypeReference<List<SddcOutputInterface>>(){});
        //SddcOutputInterface sddc = result.getBody();
        //System.out.println(sddc.getId());
        //To get the type of the object
        System.out.println(result.getBody().getClass());
        for(SddcOutputInterface sddc: result.getBody())
        {
            Map tags = new HashMap();
            System.out.println(sddc);
            System.out.println(sddc.getId());
            tags.put("TaskType", sddc.getTask_type());
            tags.put("SubStatus", sddc.getSub_status());
            tags.put("OrgId", sddc.getOrg_id());
            tags.put("TaskOwner", sddc.getUser_name());
            tags.put("Status", sddc.getStatus());
            String sourceId = "sddc-prod." + sddc.getId() + ".vmc.tasks";
            int value;
            System.out.println(sddc.getStatus());
            if(sddc.getStatus().equals("STARTED"))
            {
                value = 1;
            }
            else if(sddc.getStatus().equals("FINISHED"))
            {
                    value = 0;
            }
            else
            {
                value = -1;
            }

            String metrics = sddc.getId();
            metrics = metrics + ".val";
            wf.sendMetrics(tags, value, sourceId, metrics);




        }
    }


    @Data
    private static class AccessToken {

        @JsonProperty("id")
        private String id;

    }

}
