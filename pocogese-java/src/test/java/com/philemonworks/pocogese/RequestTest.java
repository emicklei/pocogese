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

import com.philemonworks.pocogese.Invocation;
import com.philemonworks.pocogese.command.MockHttpRequest;
import com.philemonworks.pocogese.command.MockHttpResponse;
import junit.framework.TestCase;

public class RequestTest extends TestCase {
	public void testSpec(){
		MockHttpRequest mockHttpRequest = new MockHttpRequest();
		mockHttpRequest.requestURI = "http://host:port/context/controller_route/identifier.extension;action";
		mockHttpRequest.pathInfo = "/controller_route/identifier.extension;action";
		MockHttpResponse mockHttpResponse = new MockHttpResponse();
		Invocation ri = new Invocation(mockHttpRequest, mockHttpResponse);
		assertEquals("action", ri.getAction());
		assertEquals("extension", ri.getExtension());
		assertEquals("controller_route", ri.peek());
		for (int i = 0; i < ri.tokens.length; i++) {
			System.out.println(ri.tokens[i]);
		}
	}
}
