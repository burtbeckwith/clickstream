package com.opensymphony.clickstream.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Clickstream configuration data.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamConfig {

	private String loggerClass;
	private List<String> botAgents = new ArrayList<String>();
	private List<String> botHosts = new ArrayList<String>();

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

	public List<String> getBotAgents() {
		return botAgents;
	}

	public List<String> getBotHosts() {
		return botHosts;
	}
}
