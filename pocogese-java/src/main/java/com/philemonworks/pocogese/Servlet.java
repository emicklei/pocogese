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
package com.philemonworks.pocogese;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.philemonworks.pocogese.api.APIReporter;
import com.philemonworks.pocogese.util.RequestUtils;

/**
 * Servlet is a JEE Servlet that routes requests to Controllers passing the
 * request wrapped by a Invocation.
 * 
 * <code><![[CDATA[
 <init-param>
 <param-name>configurator</param-name>
 <param-value>{your com.philemonworks.pocogese.IServletConfigurator implementor}</param-value>
 </init-param>
 ]>></code>
 * 
 * @author ernest.micklei@philemonworks.com
 */
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = -6838446677115011845L;

	private final static Logger LOG = Logger.getLogger(Servlet.class);

	Map controllers = new HashMap();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void service(final HttpServletRequest httpReq, final HttpServletResponse httpResp) throws ServletException, IOException {
		final Invocation invocation = new Invocation(httpReq, httpResp);
		if (LOG.isDebugEnabled())
			LOG.debug(httpReq.getMethod() + " - service:" + invocation);
		if (invocation.tokenAvailable()) {
			Controller rc = this.controllerConfiguredFor(invocation);
			if (rc != null) {
				try {
					// consume token
					invocation.nextToken();
					rc.service(invocation);
				} catch (Exception ex) {
					RequestUtils.handleInvocationException(ex, invocation, rc);
				}
			} else {
				if (LOG.isDebugEnabled()) {
					this.handleUnkownPath(invocation);
				} else {
					super.service(httpReq, httpResp);
					return;
				}
			}
		} else {
			new APIReporter().reportAPI(controllers, invocation.getPreparedXMLWriter());
		}
		// copy the invocation buffer output to the response output
		// unless content was already written directly
		if (!httpResp.isCommitted()) {
			invocation.getBufferedOutputStream().writeTo(httpResp.getOutputStream());
			httpResp.getOutputStream().flush();
			httpResp.getOutputStream().close();
		}		
	}

	/**
	 * Return the controller that is specified for an invocation
	 * 
	 * @param invocation
	 * @return the controller instance or null
	 */
	private Controller controllerConfiguredFor(Invocation invocation) {
		return (Controller) controllers.get(invocation.peek());
	}

	private void handleUnkownPath(Invocation invocation) {
		LOG.warn("Unknown path:" + invocation);
		invocation.flushBuffer();
		RequestFault fault = RequestUtils.createFault(invocation);
		StringWriter sw = new StringWriter();
		sw.write("[debug] Unable to route the request to a controller. Check your Servlet configuration. \n");
		List keys = new ArrayList(controllers.keySet());
		Collections.sort(keys);
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			String path = (String) iter.next();
			sw.write("[");
			sw.write(path);
			sw.write("=>");
			sw.write(controllers.get(path).getClass().getName());
			sw.write("] ");
		}
		fault.message = sw.toString();
		fault.write(invocation.getPreparedXMLWriter());
	}
	/**
	 * Performs initialization of the Servlet. It reads the [configurator]
	 * servlet parameter and resolves it to a Class. From this Class, a new
	 * IServletConfigurator is created. The IServletConfigurator is asked to
	 * register Controller instances.
	 */
	public void init(ServletConfig config) throws ServletException {
		IServletConfigurator configurator;
		String configClassName = config.getInitParameter("configurator");
		if (configClassName == null)
			throw new RuntimeException("Missing servlet parameter value for [configurator]. This must be an implementor of the com.philemonworks.pocogese.IServletConfigurator interface");
		else
			try {
				configurator = (IServletConfigurator) RequestUtils.safeInstanceFromClassName(configClassName);
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("Invalid class name for the [configurator] parameter of the Servlet. Check your web.xml", cnfe);
			}
		LOG.debug("Configuring using [" + configurator + "]");
		try {
			configurator.configure(this, config);
		} catch (Exception ex) {
			LOG.error("Failed to configure Servlet:" + ex.getLocalizedMessage());
			throw new ServletException("Servlet configuration failed", ex);
		}
		super.init(config);
	}

	/**
	 * Register a Controller for a path.
	 * 
	 * @param path
	 *            part of the url that routes to the Controller
	 * @param rc
	 *            the Controller
	 */
	public void configure(String path, Controller rc) {
		if (controllers.containsKey(path)) {
			LOG.warn("Overriding controller for request token [" + path + "]");
		}
		LOG.debug("Requests starting at [" + path + "] are dispatched to controller [" + rc + "]");
		controllers.put(path, rc);
	}

	/**
	 * I am about to be destroyed. Notify all controllers so they can release
	 * any resources claimed.
	 */
	public void destroy() {
		for (Iterator iter = controllers.values().iterator(); iter.hasNext();) {
			Controller each = (Controller) iter.next();
			each.destroy();
		}
	}

	Map getControllers() {
		return controllers;
	}
}
