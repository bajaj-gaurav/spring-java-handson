package com.threading.resolver;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessorConfiguration {
    String name;
    JsonNode arguments;
}
