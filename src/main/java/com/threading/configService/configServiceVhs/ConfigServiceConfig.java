package com.threading.configService.configServiceVhs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class ConfigServiceConfig {

    @Value("${internal.console.url}")
    private String internalConsoleUrl;

    @Value("${config.service.config}")
    private String configServiceSuffix;

    @Value("${config.service.configSddc}")
    private String configServiceSuffixSddc;

    @Value("${config.service.url}")
    private String configServiceUrl;
}
