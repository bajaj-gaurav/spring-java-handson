/*
 * Copyright (c) 2018 VMware, Inc. All rights reserved. VMware Confidential
 */

package com.threading.configService.commons;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Data class to store collector configuration.
 */
@Getter
@Slf4j
public class CollectorConfiguration {
    // TODO: Move this out as a configurable.
    private static final long DEFAULT_INTERVAL = 60L;
    private static final long MINIMUM_INTERVAL = 30L;

    private final String name;
    private final JsonNode arguments;
    private final List<ProcessorConfiguration> processors;
    private final long every;
    private final Expression when;

    /**
     * Create Collector Configuration.
     * @param name Name of Collector.
     * @param arguments Arguments accepted by collector.
     * @param processors Post-processors to apply in the collection pipeline.
     * @param every Scheduling interval.
     * @param when Preconditions Gate which is evaluated before scheduling a collector.
     */
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
                log.error("Collector Configuration Overridden for attribute every: configured={} overridden={}",
                        every,
                        MINIMUM_INTERVAL);

                this.every = MINIMUM_INTERVAL;
            }
        } else {
            // Branch: Collection frequency is "un-set" in Configuration.
            // Collectors with unset frequency default to DEFAULT_INTERVAL.

            log.error("Collector Configuration Overridden for attribute every: configured={} overridden={}",
                    null,
                    DEFAULT_INTERVAL);
            this.every = DEFAULT_INTERVAL;
        }

        if (StringUtils.isNotEmpty(when)) {
            SpelExpressionParser parser = new SpelExpressionParser();
            this.when = parser.parseExpression(when);
        } else {
            this.when = null;
        }
    }

    /**
     * Evaulate Preconditions Gate, a collector is only scheduled in the monitor if result of this call is true.
     * @param context Evaluation Context for Spring Expressions.
     * @return Boolean indicating the same.
     */
    public boolean evaluatePreconditions(EvaluationContext context) {
        if (when != null) {
            try {
                return when.getValue(context, Boolean.class);
            } catch (EvaluationException e) {
                log.error("Evaluation of 'when' condition failed: {} {}", e.getClass().getSimpleName(), e.getMessage());
            }
        } else {
            // Schedule the collector when 'when' precondition is empty.
            return true;
        }

        return false;
    }
}
