package com.threading.autoscalar;

import java.util.TimeZone;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.http.ResponseEntity;

import com.threading.configService.authToken;
import com.threading.configService.configCalls;
import com.threading.wavefrontUploader.WavefrontPayloadUploader;

public class Main {

    public static void main(String[] main) {

        String org_id = "57f962ad-1995-4985-b902-aa952ff0d4a7";
        ResponseEntity<String> result;
        TaskCall task = new TaskCall();
        String base = "https://internal.vmc.vmware.com";

        authToken a = new authToken();
        String auth = a.getAuthToken();
        System.out.println(auth);
        task.taskCallActiveTask(auth, org_id);
        //System.out.println(result);
        //System.out.println(result.getBody());

        //WavefrontPayloadUploader wf= new WavefrontPayloadUploader();
        //System.out.println(wf.Date1());
    }
}
