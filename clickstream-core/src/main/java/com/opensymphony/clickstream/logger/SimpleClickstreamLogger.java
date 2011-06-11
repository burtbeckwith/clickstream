package com.opensymphony.clickstream.logger;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.clickstream.Clickstream;
import com.opensymphony.clickstream.ClickstreamRequest;

/**
 * A simple ClickstreamLogger that outputs the entire clickstream to 
 * the <a href="http://jakarta.apache.org/commons/logging/">Jakarta Commons Logging component</a>.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class SimpleClickstreamLogger implements ClickstreamLogger {

	private static final Log log = LogFactory.getLog(SimpleClickstreamLogger.class);

	public void log(Clickstream clickstream) {
		if (clickstream == null) return;

		StringBuilder output = new StringBuilder();

		String hostname = clickstream.getHostname();
		HttpSession session = clickstream.getSession();
		String initialReferrer = clickstream.getInitialReferrer();
		Date start = clickstream.getStart();
		Date lastRequest = clickstream.getLastRequest();

		output.append("Clickstream for: ").append(hostname).append("\n");
		output.append("Session ID: ").append((session == null ? "" : session.getId())).append("\n");
		output.append("Initial Referrer: ").append(initialReferrer).append("\n");
		output.append("Stream started: ").append(start).append("\n");
		output.append("Last request: ").append(lastRequest).append("\n");

		long streamLength = lastRequest.getTime() - start.getTime();

		output.append("Stream length:");
		if (streamLength > 3600000) {
			output.append(" ").append(streamLength / 3600000).append(" hours");
		}
		else if (streamLength > 60000) {
			output.append(" ").append((streamLength / 60000) % 60).append(" minutes");
		}
		else if (streamLength > 1000) {
			output.append(" ").append((streamLength / 1000) % 60).append(" seconds");
		}
		output.append("\n");

		int index = 0;
		for (Iterator<ClickstreamRequest> iterator = clickstream.getStream().iterator(); iterator.hasNext();) {
			output.append(++index).append(": ");
			output.append(iterator.next());
			if (iterator.hasNext()) {
				output.append("\n");
			}
		}

		log.info(output);
	}
}
