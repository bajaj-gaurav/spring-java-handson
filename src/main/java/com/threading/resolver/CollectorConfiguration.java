package com.threading.resolver;

import java.util.List;


import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;


@Getter
public class CollectorConfiguration {

    // TODO: Move this out as a configurable.
    private static final long DEFAULT_INTERVAL = 60L;
    private static final long MINIMUM_INTERVAL = 30L;

    private final String name;
    private final JsonNode arguments;
    private final List<ProcessorConfiguration> processors;
    private final long every;
    private final Expression when;

    @JsonCreator
    public CollectorConfiguration(
            @JsonProperty("name") String name,
            @JsonProperty("arguments") JsonNode arguments,
            @JsonProperty("processors") List<ProcessorConfiguration> processors,
            @JsonProperty("every") Long every,
            @JsonProperty("when") String when) {
        this.name = name;
        this.arguments = arguments;
        this.processors = processors;

        if (every != null) {
            // Branch: Collection frequency is "set" in Configuration.
            // Collectors with frequency less than MINIMUM_INTERVAL get throttled to MINIMUM_INTERVAL.
            if (every > MINIMUM_INTERVAL) {
                this.every = every;
            } else {
                System.out.println("hello");

                this.every = MINIMUM_INTERVAL;
            }
        } else {
            // Branch: Collection frequency is "un-set" in Configuration.
            // Collectors with unset frequency default to DEFAULT_INTERVAL.


            this.every = DEFAULT_INTERVAL;
        }

        if (when != null) {
            SpelExpressionParser parser = new SpelExpressionParser();
            this.when = parser.parseExpression(when);
        } else {
            this.when = null;
        }
    }

}
