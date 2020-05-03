package com.threading.configService.configServiceSddc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SddcResourceConfigConfiguration {

    private boolean nsxt;

    private String sla;

    @JsonProperty("org_type")
    private String orgType;

    @JsonProperty("sddc_state")
    private String sddcState;

    @JsonProperty("deployment_type")
    private String deploymentType;

    private String sddc_id;

    private String region;
}
