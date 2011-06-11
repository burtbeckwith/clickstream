package com.opensymphony.clickstream;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * The actual stream of clicks tracked during a user's navigation through a site.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class Clickstream implements Serializable {

	private static final long serialVersionUID = 1;

	/** The stream itself: a list of click events. */
	private List<ClickstreamRequest> clickstream = new CopyOnWriteArrayList<ClickstreamRequest>();

	/** The attributes. */
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/** The host name. */
	private String hostname;

	/** The original referer URL, if any. */
	private String initialReferrer;

	/**  The stream start time. */
	private Date start = new Date();

	/** The time of the last request made on this stream. */
	private Date lastRequest = new Date();

	/** Flag indicating this is a bot surfing the site. */
	private boolean bot = false;

	/**
	 * The session itself.
	 *
	 * Marked as transient so that it does not get serialized when the stream is serialized.
	 * See JIRA issue CLK-14 for details.
	 */
	private transient HttpSession session;

	/**
	 * Adds a new request to the stream of clicks. The HttpServletRequest is converted
	 * to a ClickstreamRequest object and added to the clickstream.
	 *
	 * @param request The serlvet request to be added to the clickstream
	 */
	public void addRequest(HttpServletRequest request) {
		lastRequest = new Date();

		if (hostname == null) {
			hostname = request.getRemoteHost();
			session = request.getSession();
		}

		// if this is the first request in the click stream
		if (clickstream.isEmpty()) {
			// setup initial referrer
			if (request.getHeader("REFERER") != null) {
				initialReferrer = request.getHeader("REFERER");
			}
			else {
				initialReferrer = "";
			}

			// decide whether this is a bot
			bot = BotChecker.isBot(request);
		}

		clickstream.add(new ClickstreamRequest(request, lastRequest));
	}

	/**
	 * Gets an attribute for this clickstream.
	 *
	 * @param name
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Gets the attribute names for this clickstream.
	 */
	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	/**
	 * Sets an attribute for this clickstream.
	 *
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	/**
	 * Returns the host name that this clickstream relates to.
	 *
	 * @return the host name that the user clicked through
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns the bot status.
	 *
	 * @return true if the client is bot or spider
	 */
	public boolean isBot() {
		return bot;
	}

	/**
	 * Returns the HttpSession associated with this clickstream.
	 *
	 * @return the HttpSession associated with this clickstream
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * The URL of the initial referer. This is useful for determining
	 * how the user entered the site.
	 *
	 * @return the URL of the initial referer
	 */
	public String getInitialReferrer() {
		return initialReferrer;
	}

	/**
	 * Returns the Date when the clickstream began.
	 *
	 * @return the Date when the clickstream began
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Returns the last Date that the clickstream was modified.
	 *
	 * @return the last Date that the clickstream was modified
	 */
	public Date getLastRequest() {
		return lastRequest;
	}

	/**
	 * Returns the actual List of ClickstreamRequest objects.
	 *
	 * @return the actual List of ClickstreamRequest objects
	 */
	public List<ClickstreamRequest> getStream() {
		return clickstream;
	}
}
