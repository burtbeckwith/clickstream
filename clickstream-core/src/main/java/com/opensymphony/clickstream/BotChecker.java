package com.opensymphony.clickstream;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.clickstream.config.ClickstreamConfig;
import com.opensymphony.clickstream.config.ConfigLoader;

/**
 * Determines if a request is actually a bot or spider.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class BotChecker {

	public static boolean isBot(HttpServletRequest request) {
		if (request.getRequestURI().indexOf("robots.txt") != -1) {
			// there is a specific request for the robots.txt file, so we assume
			// it must be a robot (only robots request robots.txt)
			return true;
		}

		ClickstreamConfig config = ConfigLoader.getInstance().getConfig();

		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null) {
			for (String agent : config.getBotAgents()) {
				if (userAgent.indexOf(agent) != -1) {
					return true;
				}
			}
		}
		String remoteHost = request.getRemoteHost(); // requires a DNS lookup
		if (remoteHost != null && remoteHost.length() > 0 && remoteHost.charAt(remoteHost.length() - 1) > 64) {
			for (String host : config.getBotHosts()) {
				if (remoteHost.indexOf(host) != -1) {
					return true;
				}
			}
		}

		return false;
	}
}
