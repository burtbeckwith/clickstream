package com.opensymphony.clickstream.servlet;

import com.opensymphony.clickstream.Clickstream;
import com.opensymphony.clickstream.ClickstreamListener;
import com.opensymphony.clickstream.ClickstreamRequest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple servlet that shows active streams. Configure it in your web.xml:
 *
 * <pre>
 * &lt;servlet>
 *   &lt;servlet-name>activestreams&lt;/servlet-name>
 *   &lt;servlet-class>com.opensymphony.clickstream.servlet.ActiveStreamsServlet&lt;/servlet-class>
 * &lt;/servlet>
 *
 * &lt;servlet-mapping>
 *   &lt;servlet-name>activestreams&lt;/servlet-name>
 *   &lt;url-pattern>/streams&lt;/url-pattern>
 * &lt;/servlet-mapping>
 * </pre>
 *
 * <p>
 * The <code>fragment</code> parameter can be added if you don't want a full xhtml page in output, but only the
 * content of the body tag, so that it can be used in portlets, Struts Tiles or
 * <a href="http://www.opensymphony.com/sitemesh/">SiteMesh</a>.
 * </p>
 *
 * <pre>
 * &lt;servlet>
 *   &lt;servlet-name>activestreams&lt;/servlet-name>
 *   &lt;servlet-class>com.opensymphony.clickstream.servlet.ActiveStreamsServlet&lt;/servlet-class>
 *   &lt;init-param>
 *     &lt;param-name>fragment&lt;/param-name>
 *     &lt;param-value>true&lt;/param-value>
 *   &lt;/init-param>
 * &lt;/servlet>
 * </pre>
 *
 * @author Fabrizio Giustina
 * @version $Revision: 1.3 $
 */
public class ActiveStreamsServlet extends HttpServlet {
    /**
     * Should not print html head and body?
     */
    private static final String CONFIG_FRAGMENT = "fragment";

    /**
     * Stable <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 3134727194019463520L;

    /**
     * Don't include html head.
     */
    private boolean isFragment;

    /**
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        String fragmentParam = config.getInitParameter(CONFIG_FRAGMENT);
        isFragment = ("true".equalsIgnoreCase(fragmentParam) || "yes".equalsIgnoreCase(fragmentParam));
        super.init(config);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!isFragment) {
            response.setContentType("text/html");
        }

        String sid = request.getParameter("sid");
        PrintWriter out = response.getWriter();

        if (sid == null) {
            printClickstreamList(request, out);
        }
        else {
            printClickstreamDetail(request, out, sid);
        }

        if (!isFragment) {
            out.println("</body></html>");
        }

        out.flush();
    }

    /**
     * Received the "sid" parameter, print out the stream detail.
     *
     * @param request HttpServletRequest
     * @param out PrintWriter
     * @param sid session id
     */
    private void printClickstreamDetail(HttpServletRequest request, PrintWriter out, String sid) {
        Map clickstreams = (Map) getServletContext().getAttribute(ClickstreamListener.CLICKSTREAMS_ATTRIBUTE_KEY);

        Clickstream stream = (Clickstream) clickstreams.get(sid);

        out.println("<p align=\"right\"><a href=\"?");
        if (request.getParameter("showbots") != null) {
            out.print("showbots=");
            out.print(request.getParameter("showbots"));
        }
        out.print("\">All streams</a>");

        if (stream == null) {
            if (!isFragment) {
                printHeader(out, "Clickstream for " + sid);
            }

            out.write("<p>Session for " + sid + " has expired.</p>");
            return;
        }

        if (!isFragment) {
            printHeader(out, "Clickstream for " + stream.getHostname());
        }

        out.println("<ul>");

        if (stream.getInitialReferrer() != null) {
            out.println("<li>");
            out.println("<strong>Initial Referrer</strong>: ");
            out.print("<a href=\"");
            out.print(stream.getInitialReferrer());
            out.print("\">");
            out.print(stream.getInitialReferrer());
            out.println("</a>");
            out.println("</li>");
        }

        out.println("<li>");
        out.println("<strong>Hostname</strong>: ");
        out.println(stream.getHostname());
        out.println("</li>");

        out.println("<li>");
        out.println("<strong>Session ID</strong>: ");
        out.println(sid);
        out.println("</li>");

        out.println("<li>");
        out.println("<strong>Bot</strong>: ");
        out.println(stream.isBot() ? "Yes" : "No");
        out.println("</li>");

        out.println("<li>");
        out.println("<strong>Stream Start</strong>: ");
        out.println(stream.getStart());
        out.println("</li>");

        out.println("<li>");
        out.println("<strong>Last Request</strong>: ");
        out.println(stream.getLastRequest());
        out.println("</li>");

        out.println("<li>");
        out.println("<strong>Session Length</strong>: ");
        long streamLength = stream.getLastRequest().getTime() - stream.getStart().getTime();
        if (streamLength > 3600000) {
            out.print((streamLength / 3600000) + " hours ");
        }
        if (streamLength > 60000) {
            out.print(((streamLength / 60000) % 60) + " minutes ");
        }
        if (streamLength > 1000) {
            out.print(((streamLength / 1000) % 60) + " seconds");
        }
        out.println("</li>");

        out.println("<li>");
        out.println("<strong># of Requests</strong>: ");
        out.println(stream.getStream().size());
        out.println("</li>");

        out.println("</ul>");

        out.println("<h3>Click stream:</h3>");

        synchronized (stream) {
            Iterator clickstreamIt = stream.getStream().iterator();

            out.print("<ol>");

            while (clickstreamIt.hasNext()) {
                String click = ((ClickstreamRequest) clickstreamIt.next()).toString();

                out.write("<li>");
                out.write("<a href=\"http://");
                out.print(click);
                out.write("\">");
                out.print(click);
                out.write("</a>");
                out.write("</li>");

            }
            out.print("</ol>");
        }

    }

    /**
     * Print out the active clickstream list.
     *
     * @param request HttpServletRequest
     * @param out PrintWriter
     */
    private void printClickstreamList(HttpServletRequest request, PrintWriter out) {
        if (!isFragment) {
            printHeader(out, "Active Clickstreams");
        }

        Map clickstreams = (Map) getServletContext().getAttribute(ClickstreamListener.CLICKSTREAMS_ATTRIBUTE_KEY);

        String showbots = "false";
        if ("true".equalsIgnoreCase(request.getParameter("showbots"))) {
            showbots = "true";
        }
        else if ("both".equalsIgnoreCase(request.getParameter("showbots"))) {
            showbots = "both";
        }

        out.println("<p>");

        if ("true".equals(showbots)) {
            out.println("<a href=\"?showbots=false\">User Streams</a>");
            out.println(" | ");
            out.println("<strong>Bot Streams</strong>");
        }
        else if ("both".equalsIgnoreCase(showbots)) {
            out.println("<a href=\"?showbots=false\">User Streams</a>");
            out.println(" | ");
            out.println("<a href=\"?showbots=true\">Bot Streams</a>");
        }
        else {
            out.println("<strong>User Streams</strong>");
            out.println(" | ");
            out.println("<a href=\"?showbots=true\">Bot Streams</a>");
        }

        out.println(" | ");

        // showBots is TRUE or FALSE
        if (!"both".equalsIgnoreCase(showbots)) {
            out.println("<a href=\"?showbots=both\">Both</a>");
        }
        else {
            out.println("<strong>Both</strong>");
        }

        out.println("</p>");

        if (clickstreams.isEmpty()) {
            out.println("<p>No clickstreams in progress.</p>");
        }
        else {
            synchronized (clickstreams) {
                Iterator it = clickstreams.keySet().iterator();
                out.print("<ol>");
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Clickstream stream = (Clickstream) clickstreams.get(key);

                    if (showbots.equals("false") && stream.isBot()) {
                        continue;
                    }
                    else if (showbots.equals("true") && !stream.isBot()) {
                        continue;
                    }

                    String hostname = (stream.getHostname() != null && !"".equals(stream.getHostname()) ? stream
                            .getHostname() : "Stream");

                    out.print("<li>");

                    out.print("<a href=\"?sid=");
                    out.print(key);
                    out.print("&showbots=");
                    out.write(showbots);
                    out.write("\">");
                    out.write("<strong>");
                    out.print(hostname);
                    out.print("</strong>");
                    out.print("</a> ");
                    out.print("<small>[");
                    out.print(stream.getStream().size());
                    out.print(" reqs]</small>");

                    out.print("</li>");
                }
                out.print("</ol>");
            }
        }
    }

    /**
     * Prints the page header.
     * @param out output writer
     * @param title page title
     */
    private void printHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
        out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");

        out.println("<head><title>");
        out.println(title);
        out.println("</title></head>");

        out.println("<body>");
        out.print("<h3>");
        out.print(title);
        out.println("</h3>");
    }
}
