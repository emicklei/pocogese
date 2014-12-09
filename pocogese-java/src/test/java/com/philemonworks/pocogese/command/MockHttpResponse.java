package com.philemonworks.pocogese.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpResponse implements HttpServletResponse {

	private ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
	private Map<String, String> settings = new HashMap<String, String>();
	
	public void addCookie(Cookie arg0) {

	}

	public void addDateHeader(String arg0, long arg1) {

	}

	public void addHeader(String arg0, String arg1) {

	}

	public void addIntHeader(String arg0, int arg1) {

	}

	public boolean containsHeader(String arg0) {

		return false;
	}

	public String encodeRedirectURL(String arg0) {

		return null;
	}

	public String encodeRedirectUrl(String arg0) {

		return null;
	}

	public String encodeURL(String arg0) {

		return null;
	}

	public String encodeUrl(String arg0) {

		return null;
	}

	public void sendError(int arg0) throws IOException {

	}

	public void sendError(int arg0, String arg1) throws IOException {

	}

	public void sendRedirect(String arg0) throws IOException {

	}

	public void setDateHeader(String arg0, long arg1) {

	}

	public void setHeader(String arg0, String arg1) {

	}

	public void setIntHeader(String arg0, int arg1) {

	}

	public void setStatus(int arg0) {

	}

	public void setStatus(int arg0, String arg1) {

	}

	public void flushBuffer() throws IOException {

	}

	public int getBufferSize() {

		return 0;
	}

	public String getCharacterEncoding() {

		return null;
	}

	public Locale getLocale() {

		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {

		return new MockServletOutputStream(bos);
	}

	public PrintWriter getWriter() throws IOException {

		return new PrintWriter(this.getOutputStream());
	}

	public boolean isCommitted() {

		return false;
	}

	public void reset() {

	}

	public void resetBuffer() {

	}

	public void setBufferSize(int arg0) {

	}

	public void setContentLength(int arg0) {

	}

	public void setContentType(String arg0) {
		settings.put("contentType",arg0);
	}

	public void setLocale(Locale arg0) {

	}

	public String getContentType() {
		return settings.get("contentType");
	}

	public void setCharacterEncoding(String charset) {
		// TODO Auto-generated method stub
		
	}
	
	public String getContent(){
		return new String(bos.toByteArray());
	}

}
