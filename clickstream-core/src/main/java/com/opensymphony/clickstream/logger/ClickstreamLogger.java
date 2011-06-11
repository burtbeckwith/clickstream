package com.opensymphony.clickstream.logger;

import com.opensymphony.clickstream.Clickstream;

/**
 * Called when a session is invalidated the clickstream is finished.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public interface ClickstreamLogger {

	/**
	 * Initiates logging on a clickstream that just recently finished or was invalidated.
	 *
	 * @param clickstream the clickstream that has just finished
	 */
	void log(Clickstream clickstream);
}
