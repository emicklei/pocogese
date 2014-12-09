/*
       Copyright 2007 Ernest Micklei, PhilemonWorks.com

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
package com.philemonworks.flex.net
{
	import mx.utils.ObjectProxy;
	
	/**
	 * RequestFault is a parameter object that captures information about a FaultEvent created when communicating to a service.
	 * It can be created from both an ObjectProxy and an XML representation of the RequestFault object.
	 * 
	 * @author ernest.micklei@philemonworks.com, 2007
	 */
	  
	[Bindable]
	public class RequestFault
	{
		public var request:String;
		public var method:String;
		public var controller:String;
		public var stack:String;
		public var message:String;
		public var code:String;
		
		public function RequestFault(input:Object) {
			super()
			if (input is ObjectProxy) {
				var proxy:ObjectProxy = ObjectProxy(input)
				this.code = proxy.code
				this.request = proxy.request
				this.method = proxy.method
				this.controller = proxy.controller
				this.stack = proxy.stack
				this.message = proxy.message				
			} else if (input is XML) {
				var xml:XML = XML(input)
				this.code = xml.@code
				this.request = xml.request
				this.method = xml.method
				this.controller = xml.controller
				this.stack = xml.stack
				this.message = xml.message
			}
		}
		public function toString():String {
			return "RequestFault[code="+this.code+",message="+this.message+"]"
		}
	}
}