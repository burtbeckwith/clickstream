package com.opensymphony.clickstream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The filter that keeps track of a new entry in the clickstream for <b>every request</b>.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamFilter implements Filter {
    private static final Log log = LogFactory.getLog(ClickstreamFilter.class);

    protected FilterConfig filterConfig;

    /**
     * Attribute name indicating the filter has been applied
     * to a given request.
     */
    private final static String FILTER_APPLIED = "_clickstream_filter_applied";

    /**
     * Processes the given request and/or response.
     *
     * @param request The request
     * @param response The response
     * @param chain The processing chain
     * @throws IOException If an error occurs
     * @throws ServletException If an error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Ensure that filter is only applied once per request.
        if (request.getAttribute(FILTER_APPLIED) == null) {
            if (log.isDebugEnabled()) {
                log.debug("Applying clickstream filter to request.");
            }

            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

            HttpSession session = ((HttpServletRequest) request).getSession();
            Clickstream stream = (Clickstream) session.getAttribute(ClickstreamListener.SESSION_ATTRIBUTE_KEY);
            stream.addRequest((HttpServletRequest) request);
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("Clickstream filter already applied, ignoring it.");
            }
        }

        // pass the request on
        chain.doFilter(request, response);
    }

    /**
     * Initializes this filter.
     *
     * @param filterConfig The filter configuration
     * @throws ServletException If an error occurs
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroys this filter.
     */
    public void destroy() {
    }
}