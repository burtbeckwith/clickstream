package com.opensymphony.clickstream;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * A small class that captures the most important info from the HttpServletRequest for each "click".
 * See the documentation for HttpServletRequest for more info about each method here.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 */
public class ClickstreamRequest implements Serializable {
    private String protocol;
    private String serverName;
    private int serverPort;
    private String requestURI;
    private String queryString;
    private String remoteUser;
    private Date timestamp;

    public ClickstreamRequest(HttpServletRequest request, Date timestamp) {
        protocol = request.getProtocol();
        serverName = request.getServerName();
        serverPort = request.getServerPort();
        requestURI = request.getRequestURI();
        queryString = request.getQueryString();
        remoteUser = request.getRemoteUser();
        this.timestamp = timestamp;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a string representation of the HTTP request being tracked.
     * Example: <b>www.opensymphony.com/some/path.jsp?arg1=foo&arg2=bar</b>
     *
     * @return  a string representation of the HTTP request being tracked.
     */
    public String toString() {
        return serverName +
                (serverPort != 80 ? ":" + serverPort : "") +
                requestURI +
                (queryString != null ? "?" + queryString : "");
    }
}