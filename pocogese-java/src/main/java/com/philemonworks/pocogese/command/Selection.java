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

import com.philemonworks.pocogese.Invocation;
/**
 * Class Selection encapsulates the data for processing a Selection.
 * It stores the operation name and the argument values.
 * 
 * @author Ernest.Micklei
 */
public class Selection extends Command {

	public Selection() {
		super();
	}

	public Selection(String name, String parameterName, String argumentValue) {
		super(name, parameterName, argumentValue);
	}

	public Selection(String name) {
		super(name);
	}

	public Object[] getParameterValueArrayWith(Invocation invocation) {
		String[] params = super.getParameterValueArray();
		Object[] paramsPlusOne = new Object[params.length+1];
		System.arraycopy(params, 0, paramsPlusOne, 0, params.length);
		paramsPlusOne[params.length] = invocation;
		return paramsPlusOne;
	}
}
