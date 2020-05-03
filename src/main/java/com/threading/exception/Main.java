package com.threading.exception;

import java.util.Arrays;

public class Main {

    public static void main(String[] args)
    {
        final String[] supportedAgentPrefix = {"perf-agent-", "vhs-agent-pop-", "vhs-agent-saas-"};
        if(!Arrays.asList(supportedAgentPrefix).contains("perf-agent-"))
        {
            throw new IllegalArgumentException("Incorrect AgentPrefix. Please correct the agent prefix");
        }
        else
        {
            supportedAgentPrefix[0] = "gaurav";
            System.out.println("perf-agent-");
            System.out.println(supportedAgentPrefix[0]);

        }
    }
}
