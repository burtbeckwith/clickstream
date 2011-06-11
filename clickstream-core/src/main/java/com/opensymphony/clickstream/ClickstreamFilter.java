package com.opensymphony.clickstream;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Keeps track of a new entry in the clickstream for <b>every request</b>.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamFilter implements Filter {

	private static final Log log = LogFactory.getLog(ClickstreamFilter.class);

	protected FilterConfig filterConfig;

	/**
	 * Attribute name indicating the filter has been applied to a given request.
	 */
	private final static String FILTER_APPLIED = "_clickstream_filter_applied";

	/**
	 * Processes the given request and/or response.
	 *
	 * @param req The request
	 * @param res The response
	 * @param chain The processing chain
	 * @throws IOException If an error occurs
	 * @throws ServletException If an error occurs
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		// Ensure that filter is only applied once per request.
		if (req.getAttribute(FILTER_APPLIED) == null) {
			log.debug("Applying clickstream filter to request.");

			req.setAttribute(FILTER_APPLIED, true);

			HttpServletRequest request = (HttpServletRequest)req;

			HttpSession session = request.getSession();
			Clickstream stream = (Clickstream) session.getAttribute(ClickstreamListener.SESSION_ATTRIBUTE_KEY);
			stream.addRequest(request);
		}
		else {
			log.debug("Clickstream filter already applied, ignoring it.");
		}

		// pass the request on
		chain.doFilter(req, res);
	}

	/**
	 * Initializes this filter.
	 *
	 * @param filterConfig The filter configuration
	 * @throws ServletException If an error occurs
	 */
	public void init(FilterConfig config) throws ServletException {
		filterConfig = config;
	}

	/**
	 * Destroys this filter.
	 */
	public void destroy() {
		// nothing to do
	}
}
