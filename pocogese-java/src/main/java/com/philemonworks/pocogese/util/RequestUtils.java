/*
 Copyright 2007 Ernest Micklei @ PhilemonWorks.com

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
package com.philemonworks.pocogese.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

import com.philemonworks.pocogese.Controller;
import com.philemonworks.pocogese.Invocation;
import com.philemonworks.pocogese.RequestFault;

public class RequestUtils {
	private final static Logger LOG = Logger.getLogger(RequestUtils.class);
	/**
	 * Return a new instance from the class whose name is [qualifiedName].
	 * @param qualifiedName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Object safeInstanceFromClassName(String qualifiedName) throws ClassNotFoundException {
		try {
			Class cls = Thread.currentThread().getContextClassLoader().loadClass(qualifiedName);
			return cls.newInstance();
		} catch (ClassNotFoundException cfne) {
			Logger.getLogger(RequestUtils.class).error("safeInstanceFromClassName:" + qualifiedName, cfne);
			// let caller do proper exception handling
			throw cfne;
		} catch (Exception e) {
			Logger.getLogger(RequestUtils.class).error("safeInstanceFromClassName:" + qualifiedName, e);
			throw new RuntimeException("safeInstanceFromClassName:" + qualifiedName, e);
		}
	}

	/**
	 * Return a new List that contains [theone] element.
	 * @param theone
	 * @return
	 */
	public static List asList(Object theone) {
		ArrayList single = new ArrayList(1);
		single.add(theone);
		return single;
	}

	/**
	 * Return a new URI String from the [uri] parameter extended by a new [key,value] pair. 
	 * This method URL encodes (utf8) the value.
	 * @param uri
	 * @param key
	 * @param value
	 * @return
	 */
	public static String appendQueryPair(String uri, String key, String value) {
		String path = uri;
		if (uri.indexOf('?') != -1)
			path += '&';
		else
			path += '?';
		try {
			path += key + "=" + URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("query parameter value encoding failed", e);
		}
		return path;
	}

	/**
	 * Return a new String from [input] stripped from the [suffix]
	 * @param input
	 * @param suffix
	 * @return
	 */
	public static String withoutSuffix(String input, String suffix) {
		if (!input.endsWith(suffix))
			return input;
		return input.substring(0, input.lastIndexOf(suffix));
	}

	/**
	 * Process the data available on the input stream using a SAX Handler
	 * implementor
	 * 
	 * @param reader
	 *            DefaultHandler
	 * @param inputStream
	 *            InputStream
	 */
	public static void parseInputStream(DefaultHandler reader, InputStream inputStream) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(inputStream, reader);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse input", e);
		}
	}

	/**
	 * Use the reader (a SAX Handler) to parse the input xml.
	 * 
	 * @param reader
	 *            DefaultHandler
	 * @param xmlString
	 *            String
	 */
	public static void parseInput(DefaultHandler reader, String xmlString) {
		RequestUtils.parseInputStream(reader, new ByteArrayInputStream(xmlString.getBytes()));
	}
	
	/**
	 * Request processing has failed resulting in an Exception. Create a
	 * RequestFault and return its xml representation.
	 * 
	 * @param ex
	 * @param invocation
	 * @param rc
	 */
	public static void handleInvocationException(Exception ex, Invocation invocation, Controller rc) {
		LOG.error("controller [" + rc + "] failed to service [" + invocation + "]", ex);
		invocation.flushBuffer();
		XMLWriter xml = invocation.getPreparedXMLWriter();
		RequestFault fault = RequestUtils.createFault(invocation);
		fault.initMessage(ex);
		// stack only available in DEBUG mode
		if (LOG.isDebugEnabled()) {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			fault.stackTrace = sw.toString();
			fault.controller = rc.toString();
		}
		fault.write(xml);
	}	
	/**
	 * Create a new RequestFault from the Invocation
	 * which has information about the request uri and http method.
	 * @param invocation
	 * @return a new RequestFault
	 */
	public static RequestFault createFault(Invocation invocation) {
		RequestFault fault = new RequestFault();
		fault.request = invocation.getHttpRequest().getRequestURI();
		fault.method = invocation.getHttpRequest().getMethod();
		return fault;
	}	
	/**
	 * Format a Date using the standard ISO8601 used in XML.
	 * @param date
	 * @return
	 */
	public static String getDateAsISO8601String(Date date) {
		SimpleDateFormat format_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
		return format_ISO8601.format(date);
	}	
}
