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
	/**
	 * Reply encapsulates the result of executing a Command by a service.
	 * It provides status information and possible messages.
	 * The result attribute can be used to return any object from the service
	 * although this should be done using a selection request.
	 *
	 *  &lt;reply status="ok">
	 *		&lt;result>42&lt;/result>
	 *		&lt;message key="msg.why" type="information">Answer to everything&lt;/message>
	 *		&lt;message>for everybody&lt;/message>	
	 *  &lt;/reply>
	 */
	
	public class Reply
	{
		public static var STATUS_RecordNotFound:String = 'RecordNotFound';
		public static var STATUS_RecordSaved:String = 'RecordSaved';
		public static var STATUS_RecordInvalid:String = 'RecordInvalid';
		public static var STATUS_Exception:String = 'Exception';
		public static var STATUS_OK:String = 'ok';
		
		public var status:String;
		public var result:String;
		public var messages:Array = [];
		
		public function Reply(data:XML = null) {
			super()
			if (data != null) {
				this.status = data.@status
				this.result = data.result
				if (data.message != null) {
					for each(var msg:XML in data..message) {
						this.messages.push(msg.text().toString())
					}
				}
			}
		}
		public function hasMessages():Boolean {
			return messages != null && messages.length > 0
		}
		
		public function toString():String {
			return "Reply[status=" + status + ",result=" + result + ",messageText=" + this.getMessageText()+"]"
		}
		/**
		 * Return a composed text message from the collection of messages (if any)
		 * 
		 * @return String
		 */ 
		public function getMessageText():String {
			if (!this.hasMessages()) return ''
			return messages.join('\n')						
		}
		public function traceInfo():String {
			return "status=" + status + ",result=" + result + ",messages=" + this.getMessageText()	
		}
	}
}