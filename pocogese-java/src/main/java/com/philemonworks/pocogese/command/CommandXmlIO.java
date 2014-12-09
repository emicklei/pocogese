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
import java.util.Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.philemonworks.pocogese.util.RequestUtils;
import com.philemonworks.pocogese.util.XMLWriter;

/**
 * 
 * <command name="<method>">
 *   <parameter name="<param-name>" value="<param-value>" />
 *   ....
 * </command>
 * 
 * @author ernest.micklei@philemonworks.com
 */
public class CommandXmlIO extends DefaultHandler {
	public Command command;

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("command".equals(qName)) {
			command = new Command();
			command.name = attributes.getValue("name");
			return;
		}
		if ("parameter".equals(qName)) {
			command.putParameter(attributes.getValue("name"), attributes
					.getValue("value"));
		}
	}

	public void write(Command cmd, XMLWriter xml) {
		xml.opentag("command");
		xml.attribute("name", cmd.name);
		xml.closetag();
		for (Iterator iter = cmd.parameters.keySet().iterator(); iter.hasNext();) {
			final String key = (String) iter.next();
			final String value = ((String[])cmd.parameters.get(key))[0];
			xml.opentag("parameter");
			xml.attribute("name", key);
			xml.attribute("value", value);
			xml.closeemptytag();
		}
		xml.end();
	}

	public static String toXml(Command cmd) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
		XMLWriter xml = new XMLWriter(bos);
		new CommandXmlIO().write(cmd, xml);
		return bos.toString();
	}
	public static Command fromXml(String xmlString){
		CommandXmlIO reader = new CommandXmlIO();
		RequestUtils.parseInput(reader, xmlString);
		return reader.command;
	}
}
