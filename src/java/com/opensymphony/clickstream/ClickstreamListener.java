package com.opensymphony.clickstream;

import com.opensymphony.clickstream.logger.ClickstreamLoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The listener that keeps track of all clickstreams in the container as well
 * as the creating new Clickstream objects and initiating logging when the
 * clickstream dies (session has been invalidated).
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 * @author <a href="yoavs@apache.org">Yoav Shapira</a>
 */
public class ClickstreamListener implements ServletContextListener, HttpSessionListener {
    private static final Log log = LogFactory.getLog(ClickstreamListener.class);

    /** The servlet context attribute key. */
    public static final String CLICKSTREAMS_ATTRIBUTE_KEY = "clickstreams";

    /**
     * The click stream (individual) attribute key: this is
     * the one inserted into the HttpSession.
     */
    public static final String SESSION_ATTRIBUTE_KEY = "clickstream";

    /** The current clickstreams, keyed by session ID. */
    private Map clickstreams;

    public ClickstreamListener() {
        log.debug("ClickstreamLogger constructed");

        clickstreams = Collections.synchronizedMap(new HashMap());
    }

    /**
     * Notification that the ServletContext has been
     * initialized.
     *
     * @param sce The context event
     */
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("ServletContext initialised");
        sce.getServletContext().setAttribute(CLICKSTREAMS_ATTRIBUTE_KEY, clickstreams);
    }

    /**
     * Notification that the ServletContext has been
     * destroyed.
     *
     * @param sce The context event
     */
    public void contextDestroyed(ServletContextEvent sce) {
        log.debug("ServletContext destroyed");
        // help gc, but should be already clear except when exception was thrown during sessionDestroyed
        clickstreams.clear();
    }

    /**
     * Notification that a Session has been created.
     *
     * @param hse The session event
     */
    public void sessionCreated(HttpSessionEvent hse) {
        final HttpSession session = hse.getSession();
        if (log.isDebugEnabled()) {
            log.debug("Session " + session.getId() + " was created, adding a new clickstream.");
        }

        Object attrValue = session.getAttribute(SESSION_ATTRIBUTE_KEY);
        if (attrValue != null) {
            log.warn("Session " + session.getId() + " already has an attribute named " +
                    SESSION_ATTRIBUTE_KEY + ": " + attrValue);
        }

        final Clickstream clickstream = new Clickstream();
        session.setAttribute(SESSION_ATTRIBUTE_KEY, clickstream);
        clickstreams.put(session.getId(), clickstream);
    }

    /**
     * Notification that a session has been destroyed.
     *
     * @param hse The session event
     */
    public void sessionDestroyed(HttpSessionEvent hse) {
        final HttpSession session = hse.getSession();

        // check if the session is not null (expired)
        if (session != null) {
            if (log.isDebugEnabled()) {
                log.debug("Session " + session.getId() + " was destroyed, logging the clickstream and removing it.");
            }

            final Clickstream stream = (Clickstream) clickstreams.get(session.getId());
            if (stream == null) {
                log.warn("Session " + session.getId() + " doesn't have a clickstream.");
            }
            else {
                try {
                    if (stream.getSession() != null) {
                        ClickstreamLoggerFactory.getLogger().log(stream);
                    }
                }
                catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                finally {
                    clickstreams.remove(session.getId());
                }
            }
        }
    }
}