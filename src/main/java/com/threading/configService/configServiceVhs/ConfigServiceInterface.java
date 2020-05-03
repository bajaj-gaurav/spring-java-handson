package com.threading.configService.configServiceVhs;

import org.springframework.http.ResponseEntity;

public interface ConfigServiceInterface {
    ResponseEntity<String> getConfig(String x, String y);
    ResponseEntity<String> getConfigSddc(String x, String y);
}
