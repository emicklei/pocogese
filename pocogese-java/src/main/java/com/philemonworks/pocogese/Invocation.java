/*
 Copyright 2006 Ernest Micklei @ PhilemonWorks.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
 */
package com.philemonworks.pocogese;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;
import com.philemonworks.pocogese.command.Reply;
import com.philemonworks.pocogese.command.ReplyXmlIO;
import com.philemonworks.pocogese.util.RequestUtils;
import com.philemonworks.pocogese.util.XMLWriter;

public class Invocation {

	// request part
	public final HttpServletRequest request;
	public int nextIndex = 0;
	public String[] tokens;
	public String action = null;
	public String extension = null;
	
	// response part
	public final HttpServletResponse response;
	
	// context part
	private ByteArrayOutputStream buffer = null;
	private XMLWriter xml = null;
		
	// Constructor
	public Invocation(HttpServletRequest request,HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
		this.init(request.getRequestURI(),request.getPathInfo());
	}
	public String createURI(String extendedPath) {
		return request.getContextPath() + request.getServletPath() + "/" + extendedPath;
	}
	public String getAction() {
		return action;
	}
	public String getExtension(){
		return extension;
	}
	public HttpServletRequest getHttpRequest() {
		return request;
	}
	public HttpServletResponse getHttpResponse() {
		return response;
	}	
	public ByteArrayOutputStream getBufferedOutputStream() {
		if (this.buffer == null) {
			this.buffer = new ByteArrayOutputStream(512);	
		} 
		return this.buffer;
	}
	byte[] getOutputBytes(){
		if (this.buffer == null) return new byte[0];
		return this.buffer.toByteArray();
	}
	/**
	 * Flush the written output to the buffer.
	 * Invalidate any xml or html writer too.
	 */
	public void flushBuffer(){
		this.buffer = null;
		this.xml = null;
	}
	public String getParameter(String key) {
		return request.getParameter(key);
	}
	public String[] getParameterValueArray() {
		Enumeration enummie = request.getParameterNames();
		ArrayList values = new ArrayList();
		while (enummie.hasMoreElements()) {
			String name = (String)enummie.nextElement();
			values.add(request.getParameter(name));
		}
		// convert list to String[]
		String[] result = new String[values.size()];
		System.arraycopy(values, 0, result, 0, result.length);
		return result;
	}
	public Map getParameterMap(){
		return request.getParameterMap();
	}
	/**
	 * Return the String value from a query parameter or the absentValue
	 * @param param
	 * @param absentValue
	 * @return
	 */
	public String getParameter(String param, String absentValue){
		String valueOrNull = this.getParameter(param);
		if (valueOrNull == null) return absentValue;
		return valueOrNull;
	}	
	/**
	 * Return the integer value from a query parameter or the absentValue
	 * @param param
	 * @param absentValue
	 * @return
	 */
	public int getIntParameter(String param, int absentValue){
		String valueOrNull = this.getParameter(param);
		if (valueOrNull == null) return absentValue;
		return Integer.parseInt(valueOrNull);
	}
	/**
	 * Return the boolean value from a query parameter or the absentValue
	 * @param param
	 * @param absentValue
	 * @return
	 */
	public boolean getBooleanParameter(String param, boolean absentValue){
		String valueOrNull = this.getParameter(param);
		if (valueOrNull == null) return absentValue;
		return "true".equalsIgnoreCase(valueOrNull);
	}	
	public XMLWriter getPreparedXMLWriter() {
		XMLWriter xml = this.getXMLWriter();
		xml.xml();
		if (this.hasParameter("xsl")) {
			xml.stylesheet(this.getParameter("xsl"));
		}
		return xml;
	}
	/**
	 * Return a new or existing XMLWriter on the output buffer.
	 * @return XMLWriter
	 */
	public XMLWriter getXMLWriter() {
		if (xml != null)
			return xml;
		this.setOutputXml();
		this.xml = new XMLWriter(this.getBufferedOutputStream());
		return this.xml;
	}
	public boolean hasAction() {
		return action != null;
	}
	public boolean hasExtension() {
		return extension != null;
	}
	public boolean hasExtensionEquals(String ext){
		return this.hasExtension() && extension.equals(ext);
	}
	public boolean hasActionEquals(String act){
		return this.hasAction() && action.equals(act);
	}	
	public boolean hasParameter(String key) {
		return request.getParameterMap().containsKey(key);
	}
	/**
	 * http://host:port/context/controller_route/identifier.extension;action 
	 * uri = /context
	 * pathinfo = /controller_route/identifier.extension;action
	 */
	void init(final String uri, String pathInfo) {
		if (pathInfo == null || pathInfo.length() == 0 || "/".equals(pathInfo)) {
			this.tokens = new String[0];
			return;			
		}
		this.tokens = pathInfo.substring(1).split("/");
		// inspect last for action
		if (tokens.length > 0) {
			nextIndex = 0;
			String last = tokens[tokens.length - 1];
			int comma = last.indexOf(';');
			if (comma > 0) {
				action = last.substring(comma + 1);
				tokens[tokens.length - 1] = last.substring(0, comma);
			}
		}
		// 		inspect last for extension
		if (tokens.length > 0) {
			String last = tokens[tokens.length - 1];
			int dot = last.indexOf('.');
			if (dot > 0) {
				extension = last.substring(dot + 1);
				tokens[tokens.length - 1] = last.substring(0, dot);
			}
		}
	}
	public boolean isIndexAccess() {
		return this.tokenAvailable() && (Character.isDigit(this.peek().charAt(0)));
	}
	public boolean isListAccess() {
		return !this.tokenAvailable();
	}
	public boolean isPropertyAccess() {
		return this.tokenAvailable() && (Character.isLetter(this.peek().charAt(0)));
	}
	public boolean isXMLAccess() {
		return "xml".equals(action) || (this.tokenAvailable() && (this.peek().endsWith(".xml")));
	}
	public int nextInt() {
		return Integer.parseInt(this.nextToken());
	}
	public String nextToken() {
		return tokens[nextIndex++];
	}
	/**
	 * Answer what would be the next token available (null if not present) 
	 * @return String || null
	 */
	public String peek() {
		if (!tokenAvailable())
			return null;
		return tokens[nextIndex];
	}
	/**
	 * Answer if the next token is equals to the parameter.
	 * If true then consume that token.
	 * @param token
	 * @return true if the next is equals to the parameter.
	 */
	public boolean peekFor(String token) {
		if (token.equals(this.peek())) {
			this.nextToken(); // consume
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Prevent the response from being cached.
	 * See www.mnot.net.cache docs.
	 */
	protected final void preventCaching() {
		response.setHeader("Pragma", "No-cache");
		// HTTP 1.0 header
		response.setDateHeader("Expires", 1L);
		// HTTP 1.1 header: "no-cache" is the standard value,
		// "no-store" is necessary to prevent caching on FireFox.
		response.setHeader("Cache-Control", "no-cache");
		response.addHeader(" Cache-Control", "no-store");
	}
	public void setOutputHtml() {
		response.setContentType("text/html");
	}
	public void setOutputXml() {
		response.setContentType("text/xml");
	}
	public boolean tokenAvailable() {
		return nextIndex < tokens.length;
	}
	/**
	 * @return String a debug string for the invocation
	 */
	public String toString() {
		StringWriter sw = new StringWriter();
		sw.write("Invocation[");
		for (int i = 0; i < tokens.length; i++) {
			sw.write("/");
			sw.write(tokens[i]);
		}
		if (extension != null) {
			sw.write('.');
			sw.write(extension);
		}		
		if (action != null) {
			sw.write(';');
			sw.write(action);
		}
		if (request.getParameterMap().isEmpty()) {
			sw.write(']');
			return sw.toString();
		}
		sw.write("?");
		for (Enumeration paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
			String each = (String) paramNames.nextElement();
			sw.write(each);
			sw.write("=");
			sw.write(XMLWriter.encoded(this.getParameter(each) ));
		}
		sw.write(']');
		return sw.toString();
	}
	/**
	 * Process the data available on the input stream using a SAX Handler implementor
	 * @param reader DefaultHandler
	 */
	public void parseInput(DefaultHandler reader){
		try {
			RequestUtils.parseInputStream(reader, this.getHttpRequest().getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("failed to parse xml using:" + reader);
		}
	}
	/**
	 * Send a Reply as a response to the request
	 * @param reply : Reply
	 */
	public void respondWith(final Reply reply){		
		String xmlString = ReplyXmlIO.toXml(reply);
		if (Logger.getLogger(this.getClass()).isDebugEnabled()) {
			Logger.getLogger(this.getClass()).debug(xmlString);
		}
		this.getPreparedXMLWriter().raw(xmlString);
	}
	/**
	 * Send a RequestFault as a response to the request
	 * @param fault : RequestFault
	 */
	public void respondWith(final RequestFault fault){
		if (Logger.getLogger(this.getClass()).isDebugEnabled()) {
			Logger.getLogger(this.getClass()).debug(fault);
		}
		fault.write(this.getPreparedXMLWriter());		
	}
	
	public void respondWithHTML(final String html) {
		this.response.setContentType("text/html");
		this.response.setContentLength(html.length());
		try {
			this.response.getOutputStream().print(html);
			this.response.flushBuffer();
		} catch (IOException e) {
			throw new RuntimeException("failed to write HTML to response");
		}		
	}
}
