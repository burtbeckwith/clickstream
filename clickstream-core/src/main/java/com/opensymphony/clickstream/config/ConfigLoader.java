package com.opensymphony.clickstream.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Loads up either clickstream.xml or clickstream-default.xml and returns a
 * singleton instance of ClickstreamConfig.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ConfigLoader {

	private static final Log log = LogFactory.getLog(ConfigLoader.class);

	private ClickstreamConfig config;
	private static ConfigLoader singleton = new ConfigLoader();

	public static ConfigLoader getInstance() {
		return singleton;
	}

	private ConfigLoader() {
		// singleton
	}

	public synchronized ClickstreamConfig getConfig() {
		if (config != null) {
			return config;
		}

		InputStream is = getInputStream("clickstream.xml");

		if (is == null) {
			is = getInputStream("/clickstream.xml");
		}
		if (is == null) {
			is = getInputStream("META-INF/clickstream-default.xml");
		}
		if (is == null) {
			is = getInputStream("/META-INF/clickstream-default.xml");
		}

		config = new ClickstreamConfig();

		try {
			log.debug("Loading config");
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, new ConfigHandler());
			return config;
		}
		catch (SAXException e) {
			log.error("Could not parse config XML", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (IOException e) {
			log.error("Could not read config from stream", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (ParserConfigurationException e) {
			log.fatal("Could not obtain SAX parser", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (RuntimeException e) {
			log.fatal("RuntimeException", e);
			throw e;
		}
		catch (Throwable e) {
			log.fatal("Exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * SAX Handler implementation for handling tags in config file and building
	 * config objects.
	 */
	private class ConfigHandler extends DefaultHandler {
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.equals("logger")) {
				config.setLoggerClass(attributes.getValue("class"));
			}
			else if (qName.equals("bot-host")) {
				config.addBotHost(attributes.getValue("name"));
			}
			else if (qName.equals("bot-agent")) {
				config.addBotAgent(attributes.getValue("name"));
			}
		}
	}

	private InputStream getInputStream(String resourceName) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			is = ConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
		}

		return is;
	}
}
