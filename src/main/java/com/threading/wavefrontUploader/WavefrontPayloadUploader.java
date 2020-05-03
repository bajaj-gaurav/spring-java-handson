package com.threading.wavefrontUploader;


import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.FastDateFormat;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableMap;
import com.wavefront.integrations.Wavefront;
import com.wavefront.integrations.WavefrontSender;
import com.wavefront.integrations.metrics.WavefrontReporter;
import com.wavefront.sdk.direct.ingestion.WavefrontDirectIngestionClient;
import com.wavefront.integrations.WavefrontDirectSender;


public class WavefrontPayloadUploader {

    Wavefront wf;
    WavefrontDirectSender wavefrontSender1;
    private Wavefront wavefront;
    Map tags = null;
    private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                                TimeZone.getTimeZone("UTC"));
    /*public void WavefrontPayloadUploader1()
    {

        tags.put("hello", "gaurav");
        WavefrontDirectIngestionClient.Builder wfDirectIngestionClientBuilder =
                new WavefrontDirectIngestionClient.Builder(wavefrontURL, token);

        WavefrontSender wavefrontSender = wfDirectIngestionClientBuilder.build();
        wavefrontSender.sendMetric("availability.vmc.host.status", 1, ,
                                   "", ImmutableMap.<String, String>builder().put("datacenter", "dc1").build());

        String metric = "availability.vmc.host.status";
        int value = 1;
        Date now = Calendar.getInstance().getTime();
        String uploadTime = dateFormat.format(now);
        Long ts = Instant.parse(uploadTime).getEpochSecond();
        String sourceId = "gaurav";

        try {
            wavefront.send(metric, value, ts, sourceId, tags);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static Long Date1()
    {
        Date now = Calendar.getInstance().getTime();
        String uploadTime = dateFormat.format(now);
        Long ts = Instant.parse(uploadTime).getEpochSecond();
        return ts;
    }

    //MetricRegistry registry = new MetricRegistry();
    public void sendMetrics(Map tags, double value, String sourceId, String metrics)
    {
        String server = "https://longboard.wavefront.com/api";
        String token = "a6d658d4-354d-46ce-84e5-812ac61275a4";
        wavefrontSender1 = new WavefrontDirectSender(server, token);

        try{
        //WavefrontSender wfSend = wavefrontSender1.send(metrics, value, Date1(), sourceId, tags);


            wavefrontSender1.send(metrics, value, Date1(), sourceId, tags);
            System.out.println(metrics);
            System.out.println(value);
            System.out.println(Date1());
            System.out.println(sourceId);
            System.out.println(tags);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
