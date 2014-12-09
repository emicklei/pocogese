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
import org.apache.log4j.Logger;
import com.philemonworks.pocogese.command.Command;
import com.philemonworks.pocogese.command.CommandXmlIO;
import com.philemonworks.pocogese.command.Selection;
import com.philemonworks.pocogese.util.DispatchUtils;

public abstract class Controller {
	final Logger LOG = Logger.getLogger(this.getClass());
	private static ThreadLocal<Invocation> ActiveInvocation = new ThreadLocal<Invocation>();

	/**
	 * Return the invocation that is being processing by the current Thread
	 * @return
	 */
	protected static Invocation getActiveInvocation(){
		return ActiveInvocation.get();
	}
	
	public void service(Invocation invocation) {
		String method = invocation.getHttpRequest().getMethod();
		invocation.peekFor("service"); // to support Rails style invocation
		try {
			ActiveInvocation.set(invocation);
			// detect method and dispatch accordingly
			if ("GET".equals(method)) {
				this.doSelection(invocation);
			} else if ("POST".equals(method)) {
				this.doCommand(invocation);
			} else
				LOG.debug("did not handle: " + method);
		} finally {
			ActiveInvocation.set(null); // make invocation unavailable
		}
	}

	public void doGet(Invocation invocation) {
		LOG.warn("unhandled doGet:" + invocation);
		try {
			invocation.response.sendError(405, this.toString() + " did not handle doGet:" + invocation);
		} catch (IOException e) {
			throw new RuntimeException("Responding with 405 failed");
		}
	}

	public void doPost(final Invocation invocation) {
		LOG.warn("unhandled doPost:" + invocation);
		try {
			invocation.response.sendError(405, this.toString() + " did not handle doGet:" + invocation);
		} catch (IOException e) {
			throw new RuntimeException("Responding with 405 failed");
		}		
	}

	protected void doSelection(final Invocation invocation) {
		Selection select = new Selection();
		if (!invocation.tokenAvailable()) {
			LOG.error("Missing operation name in request:" + invocation);
			throw new RuntimeException("Missing operation name in request:" + invocation);
		}
		select.name = invocation.nextToken();
		select.parameters = invocation.getParameterMap();
		if (LOG.isDebugEnabled()) {
			LOG.debug(select);
		}
		this.invokeSelection(select, invocation);
	}

	protected void invokeSelection(final Selection selection, Invocation invocation){
		// Try to invoke a method using the name of the selection
		DispatchUtils.execute(this, selection, invocation);
	}	
	
	protected void doCommand(final Invocation invocation) {
		final CommandXmlIO cmdIO = new CommandXmlIO();
		invocation.parseInput(cmdIO);
		if (cmdIO.command == null) {
			throw new RuntimeException("Command as XML expected");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(cmdIO.command);
		}
		this.invokeCommand(cmdIO.command, invocation);
	}
	
	protected void invokeCommand(final Command command, Invocation invocation){
		// Try to invoke a method using the name of the command
		DispatchUtils.execute(this, command, invocation);
	}
	
	/**
	 * I am about to be destroyed by my containing servlet. Do something if
	 * needed.
	 */
	public void destroy() {
		LOG.debug("destroy");
	}
	/**
	 * Return the name of the controller in the API.
	 * If it is an implementor of an Java Interface then answer the full qualified name
	 * Else answer the simple class name.
	 * @return
	 */
	public String getInterfaceName() {
		Class[] interfaces = this.getClass().getInterfaces();
		return interfaces.length == 0 ? 
			this.getClass().getSimpleName() 
			:interfaces[0].getName();
	}
}
