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
package com.philemonworks.pocogese.api;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import com.philemonworks.pocogese.Controller;
import com.philemonworks.pocogese.util.XMLWriter;

public class APIReporter {
	/**
	 * Report the API of a set of controllers using the XMLWriter.
	 * 
	 * @param controllers
	 * @param xml
	 */
	public void reportAPI(Map controllers, XMLWriter xml) {
		xml.tag("pocogese");
		for (Iterator iter = controllers.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Controller pc = (Controller) controllers.get(key);
			this.reportAPI(pc, key, xml);
		}
		xml.end(); // pocogese
	}

	/**
	 * Report the API (Selection and Command) of a controller using the
	 * XMLWriter.
	 * 
	 * @param controller
	 * @param path
	 * @param xml
	 */
	public void reportAPI(Controller controller, String path, XMLWriter xml) {
		xml.tag("controller", xml.newMap("path", path , "name", controller.getInterfaceName()),false);
		Method[] myMethods = controller.getClass().getMethods();
		for (int i = 0; i < myMethods.length; i++) {
			Method each = myMethods[i];
			SelectionParameterNames sannot = each.getAnnotation(SelectionParameterNames.class);
			if (sannot != null) {
				this.reportAPI(each, "selection", sannot.value(), xml);
			}
			CommandParameterNames cannot = each.getAnnotation(CommandParameterNames.class);
			if (cannot != null) {
				this.reportAPI(each, "command", cannot.value(), xml);
			}
		}
		xml.end();
	}

	/**
	 * Report the method from the CommandParameterNames or SelectionParameterNames annotationValue
	 * instance using an XMLWriter.
	 * 
	 * @param annot
	 * @param xml
	 */
	public void reportAPI(Method method, String selectionOrCommand, String annotationValue, XMLWriter xml) {
		Class[] argumentTypes = method.getParameterTypes();
		String[] parts;
		if (annotationValue.trim().length() == 0){
			parts = new String[0];
		} else {
			parts = annotationValue.split(",");
		}
		if (argumentTypes.length != parts.length) {
			throw new RuntimeException("API specifies different arguments than method implementation:" + method);
		}
		xml.tag(selectionOrCommand, xml.newMap("name", method.getName(), "return", method.getReturnType().getSimpleName()), false);
		for (int i = 0; i < parts.length; i++) {
			xml.tag("parameter", xml.newMap("name", parts[i], "type", method.getParameterTypes()[i].getSimpleName()), true);
		}
		xml.end();
	}
}
