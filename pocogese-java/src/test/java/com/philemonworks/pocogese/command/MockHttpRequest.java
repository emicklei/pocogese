package com.philemonworks.pocogese.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class MockHttpRequest implements HttpServletRequest {
	private static final Logger LOG = Logger.getLogger(MockHttpRequest.class);

	private InputStream inputStream;

	public String requestURI;

	public String pathInfo;

	public void setInputStream(InputStream is) {
		inputStream = is;
	}

	public String getAuthType() {

		return null;
	}

	public String getContextPath() {

		return null;
	}

	public Cookie[] getCookies() {

		return null;
	}

	public long getDateHeader(String arg0) {

		return 0;
	}

	public String getHeader(String arg0) {

		return null;
	}

	public Enumeration getHeaderNames() {

		return null;
	}

	public Enumeration getHeaders(String arg0) {

		return null;
	}

	public int getIntHeader(String arg0) {

		return 0;
	}

	public String getMethod() {

		return null;
	}

	public String getPathInfo() {

		return pathInfo;
	}

	public String getPathTranslated() {

		return null;
	}

	public String getQueryString() {

		return null;
	}

	public String getRemoteUser() {

		return null;
	}

	public String getRequestURI() {
		LOG.debug("getRequestURI");
		return requestURI;
	}

	public StringBuffer getRequestURL() {

		return null;
	}

	public String getRequestedSessionId() {

		return null;
	}

	public String getServletPath() {

		return null;
	}

	public HttpSession getSession() {

		return null;
	}

	public HttpSession getSession(boolean arg0) {

		return null;
	}

	public Principal getUserPrincipal() {

		return null;
	}

	public boolean isRequestedSessionIdFromCookie() {

		return false;
	}

	public boolean isRequestedSessionIdFromURL() {

		return false;
	}

	public boolean isRequestedSessionIdFromUrl() {

		return false;
	}

	public boolean isRequestedSessionIdValid() {

		return false;
	}

	public boolean isUserInRole(String arg0) {

		return false;
	}

	public Object getAttribute(String arg0) {

		return null;
	}

	public Enumeration getAttributeNames() {

		return null;
	}

	public String getCharacterEncoding() {

		return null;
	}

	public int getContentLength() {

		return 0;
	}

	public String getContentType() {

		return null;
	}

	public ServletInputStream getInputStream() throws IOException {

		return new MockServletInputStream(inputStream);
	}

	public Locale getLocale() {

		return null;
	}

	public Enumeration getLocales() {

		return null;
	}

	public String getParameter(String arg0) {

		return null;
	}

	public Map getParameterMap() {

		return new HashMap();
	}

	public Enumeration getParameterNames() {

		return null;
	}

	public String[] getParameterValues(String arg0) {

		return null;
	}

	public String getProtocol() {

		return null;
	}

	public BufferedReader getReader() throws IOException {

		return null;
	}

	public String getRealPath(String arg0) {

		return null;
	}

	public String getRemoteAddr() {

		return null;
	}

	public String getRemoteHost() {

		return null;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {

		return null;
	}

	public String getScheme() {

		return null;
	}

	public String getServerName() {

		return null;
	}

	public int getServerPort() {

		return 0;
	}

	public boolean isSecure() {

		return false;
	}

	public void removeAttribute(String arg0) {

	}

	public void setAttribute(String arg0, Object arg1) {

	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {

	}

	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
