package com.opensymphony.clickstream.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.clickstream.config.ConfigLoader;

/**
 * Simple factory that creates ClickstreamLogger instances.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamLoggerFactory {

	private static final Log log = LogFactory.getLog(ClickstreamLoggerFactory.class);

	/**
	 * Returns a new logging instance.
	 * 
	 * @return a new logging instance
	 */
	public static ClickstreamLogger getLogger() {
		String loggerClass = ConfigLoader.getInstance().getConfig().getLoggerClass();

		if (loggerClass == null || "".equals(loggerClass)) {
			return new NullClickstreamLogger();
		}

		try {
			Class<?> clazz = Class.forName(loggerClass, true, Thread.currentThread().getContextClassLoader());
			return (ClickstreamLogger)clazz.newInstance();
		}
		catch (Exception e) {
			log.fatal("Error instantiating specified ClickstreamLogger: " + loggerClass, e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
