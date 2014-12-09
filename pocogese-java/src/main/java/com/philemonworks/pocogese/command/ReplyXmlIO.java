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

import java.io.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.philemonworks.pocogese.util.RequestUtils;
import com.philemonworks.pocogese.util.XMLWriter;

public class ReplyXmlIO extends DefaultHandler {
	public Reply reply;

	public static Reply fromXml(String xmlString) {
		ReplyXmlIO reader = new ReplyXmlIO();
		RequestUtils.parseInput(reader, xmlString);
		return reader.reply;
	}

	public void write(Reply reply, XMLWriter xml) {
		xml.tag("reply", "status", reply.status);
		xml.raw(reply.xml);
		xml.end();
	}

	public static String toXml(Reply reply) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
		XMLWriter xml = new XMLWriter(bos);
		xml.pretty = Logger.getLogger(ReplyXmlIO.class).isDebugEnabled();
		new ReplyXmlIO().write(reply, xml);
		return bos.toString();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("reply".equals(qName)) {
			reply = new Reply();
			reply.status = attributes.getValue("status");
			return;
		}
	}
}