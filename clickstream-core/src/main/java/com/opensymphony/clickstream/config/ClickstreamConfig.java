package com.opensymphony.clickstream.config;

import java.util.List;
import java.util.ArrayList;

/**
 * Clickstream configuration data.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamConfig {
    private String loggerClass;
    private List botAgents = new ArrayList();
    private List botHosts = new ArrayList();

    public String getLoggerClass() {
        return loggerClass;
    }

    public void setLoggerClass(String loggerClass) {
        this.loggerClass = loggerClass;
    }

    public void addBotAgent(String agent) {
        botAgents.add(agent);
    }

    public void addBotHost(String host) {
        botHosts.add(host);
    }

    public List getBotAgents() {
        return botAgents;
    }

    public List getBotHosts() {
        return botHosts;
    }
}
