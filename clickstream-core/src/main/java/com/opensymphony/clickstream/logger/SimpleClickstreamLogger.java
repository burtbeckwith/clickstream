package com.opensymphony.clickstream.logger;

import com.opensymphony.clickstream.Clickstream;
import com.opensymphony.clickstream.ClickstreamRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;

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
        
        StringBuffer output = new StringBuffer();

        String hostname = clickstream.getHostname();
        HttpSession session = clickstream.getSession();
        String initialReferrer = clickstream.getInitialReferrer();
        Date start = clickstream.getStart();
        Date lastRequest = clickstream.getLastRequest();

        output.append("Clickstream for: " + hostname + "\n");
        output.append("Session ID: " + (session != null ? session.getId() + "" : "") + "\n");
        output.append("Initial Referrer: " + initialReferrer + "\n");
        output.append("Stream started: " + start + "\n");
        output.append("Last request: " + lastRequest + "\n");

        long streamLength = lastRequest.getTime() - start.getTime();

        output.append("Stream length:" +
                (streamLength > 3600000 ?
                " " + (streamLength / 3600000) + " hours" : "") +
                (streamLength > 60000 ?
                " " + ((streamLength / 60000) % 60) + " minutes" : "") +
                (streamLength > 1000 ?
                " " + ((streamLength / 1000) % 60) + " seconds" : "") +
                "\n");

        int count = 0;
        for (Iterator iterator = clickstream.getStream().iterator(); iterator.hasNext();) {
            ClickstreamRequest request = (ClickstreamRequest) iterator.next();
            count++;
            output.append(count + ": " + request + (iterator.hasNext() ? "\n" : ""));
        }

        log.info(output);
    }
}
