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

import com.philemonworks.pocogese.util.XMLWriter;
import com.philemonworks.pocogese.util.XSDWriter;

/**
 * RequestFault is used to indicate that handling of a RestInvocation has failed.
 * 
 * @author ernest.micklei@philemonworks.com
 */
public class RequestFault {
	public String request;
	public String method;
	public String code;
	public String message;
	public String stackTrace;
	public String controller;
	
	public void initMessage(Exception ex) {
		StringBuffer buffy = new StringBuffer();
		buffy.append("Caught exception:");
		buffy.append("\n\tat ");
		buffy.append(ex.getStackTrace()[0]);
		Throwable here = ex;
		while (here != null) {
			buffy.append(here);
			here = here.getCause() == here ? null : here.getCause();
			if (here != null) {
				buffy.append("\nCaused by:");
				buffy.append("\n\tat ");
				buffy.append(here.getStackTrace()[0]);				
			}
		}
		message = buffy.toString();
	}
	
	public void write(XMLWriter xml){
		xml.tag("requestfault");
		xml.tagged("request", request, true);
		xml.tagged("method", method, true);
		xml.tagged("code" , code , true);
		xml.tagged("message",message, true);
		xml.tagged("stack", stackTrace, true);
		xml.tagged("controller", controller, true);
		xml.end();
	}
	
	public static void write(XSDWriter xsd) {
		xsd.element("requestfault");
			xsd.complexType();
			xsd.sequence();
				xsd.element("request","xs:string");
				xsd.element("method","xs:string");
				xsd.element(xsd.newMap("name", "code", "type", "xs:string", "minOccurs" , "0"));
				xsd.element("message","xs:string");
				xsd.element(xsd.newMap("name", "stack", "type", "xs:string", "minOccurs" , "0"));
				xsd.element(xsd.newMap("name", "controller", "type", "xs:string", "minOccurs" , "0"));
			xsd.end();
			xsd.end();
		xsd.end();
	}
}
