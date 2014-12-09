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
package com.philemonworks.pocogese.command;

import java.io.StringWriter;
import com.philemonworks.pocogese.util.XMLWriter;

public class Reply {
	public static final String STATUS_OK = "ok";

	public static final String STATUS_WARN = "warn";

	public static final String STATUS_ERROR = "error";

	public static final String STATUS_INFO = "info";

	public String status = STATUS_OK;

	public String xml = "";

	public void setXml(String xmlString) {
		xml = xmlString;
	}

	public String toString() {
		StringWriter sw = new StringWriter();
		sw.write("Reply[");
		sw.write(status);
		sw.write(",");
		sw.write(xml);
		sw.write("]");
		return sw.toString();
	}

	public static Reply info(String reason) {
		Reply reply = new Reply();
		reply.setInfo(reason);
		return reply;
	}

	public static Reply error(String reason) {
		Reply reply = new Reply();
		reply.setError(reason);
		return reply;
	}

	public static Reply warn(String reason) {
		Reply reply = new Reply();
		reply.setWarning(reason);
		return reply;
	}

	public void setInfo(String reason) {
		status = STATUS_INFO;
		this.setXmlMessage(reason);
	}

	public void setWarning(String reason) {
		status = STATUS_WARN;
		this.setXmlMessage(reason);
	}

	public void setError(String reason) {
		status = STATUS_ERROR;
		this.setXmlMessage(reason);
	}

	private void setXmlMessage(String reason) {
		xml = "<message text='";
		xml += XMLWriter.encoded(reason);
		xml += "'/>\n";
	}

	public String toXML() {
		return ReplyXmlIO.toXml(this);
	}

	public static Reply fromXML(String xml) {
		return ReplyXmlIO.fromXml(xml);
	}
}
