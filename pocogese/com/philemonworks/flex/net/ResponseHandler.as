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
	import flash.display.Sprite;
	
	import mx.controls.Alert;
	import mx.core.Application;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.utils.ObjectProxy;
	/**
	 * ResponseHandler is for handling the completed process of sending a request using HttpClient.
	 */ 
	public class ResponseHandler implements IResponder
	{
		private var callback:Function;
		public var correlationID:int;
		
		
		public function ResponseHandler(callbackFunction:Function) {
			super()
			callback = callbackFunction
		}
		
		public function result(data:Object):void
		{
			HttpClient.afterReceive()
			this.handleResultReceived(data as ResultEvent)
		}
		
		public function fault(info:Object):void
		{
			HttpClient.afterReceive()
			this.handleFaultReceived(info as FaultEvent)
		}
		/**
		 * This handler is called when the HttpService has received a normal, expected response.
		 * The data is decoded and passed to the callback handler function.
		 * 
		 * @param event ResultEvent contains all information about the response
		 */ 
		private function handleResultReceived(event:ResultEvent):void {					
			if (event.result == null) {
				trace(this.traceInfo() + "<null> response received");	
			} else if (event.result is XML) {
				var xmlresult:XML = XML(event.result) 
				if ("requestfault" == xmlresult.name()) {
					// handle restfault
					var fault:RequestFault = new RequestFault(event.result)
					trace(this.traceInfo() + fault.toString() + " received");
					this.handleRequestFault(fault);	
				} else if ("reply" == xmlresult.name()) {
					// create Reply first before passing the result
					var reply:Reply = new Reply(xmlresult)
					trace(this.traceInfo() + reply.toString() + " received");
					this.invokeCallback(reply)				
				} else if ("nil" == xmlresult.name()){
					trace(this.traceInfo() + "nil/null received");
					this.invokeCallback(null)
				} else {
					// pass result as XML
					trace(this.traceInfo() + "XML <" + xmlresult.name() + ">... received");
					this.invokeCallback(xmlresult);					
				}
			} else if (event.result is ObjectProxy) {
				if (event.result.restfault != null) {		
					// handle restfault
					trace(this.traceInfo() + "RequestFault received");
					this.handleRequestFault(event.result.requestfault);
				} if (event.result.reply != null) {
					// pass result as Reply
					var xreply:Reply = new Reply()
					xreply.status = event.result.reply.status
					xreply.result = event.result.reply.result
					xreply.messages = event.result.reply.message
					trace(this.traceInfo() + xreply.toString() + " received");
					this.invokeCallback(xreply);	
				} else {
					// pass result by event
					trace(this.traceInfo() + "Object received");
					this.invokeCallback(event);				
				}
			} else if (event.result is String) {
				var resultString:String = String(event.result);
				if (resultString == "") {
					trace(this.traceInfo() + "<empty> received");													
				} else {
					trace(this.traceInfo() + "Object received");
					this.invokeCallback(event);
				}				
			}
		}
		private function handleRequestFault(fault:RequestFault):void {
			if (HttpClient.DEBUG) {
				trace(fault)
				RequestFaultDialog.popup(Sprite(Application.application),fault);
			} else // TODO make this an event
				Alert.show("The application reported an error. Please report this incident to the Helpdesk");
		}				
		/**
		 * This handler is called when the HttpService has received a unexcepted, exceptional response.
		 * 
		 * @param event FaultEvent contains all information about the response
		 */ 		
		private function handleFaultReceived(event:FaultEvent):void {
			trace(this.traceInfo() + "FAULT event thrown");
			if ("Client.Error.RequestTimeout" == event.fault.faultCode) {
				this.handleTimeout(event)	
			} else {
				this.defaultHandleFaultReceived(event)
			}			
		}
		/**
		 * This handler is called when the HttpService dispatched a Fault event.
		 * On default, construct a RequestFault object and open a warning dialog.
		 * 
		 * @param event FaultEvent
		 */		
		private function defaultHandleFaultReceived(event:FaultEvent):void {
			var fault:RequestFault = new RequestFault(null)
			fault.code = event.fault.faultCode
			fault.request = event.fault.faultString
			fault.message = event.fault.message.slice(0,30)
			fault.controller = "not applicable"
			fault.method = event.fault.faultString
			fault.stack = event.fault.getStackTrace()
			RequestFaultDialog.popup(Sprite(Application.application),fault);			
		}				
		/**
		 * This handler is called when the HttpService did not return with a response without the specified time.
		 * 
		 * @param event FaultEvent
		 */			
		private function handleTimeout(event:FaultEvent):void {
			// TODO make this an event
			Alert.show("The application server did not respond within 10 seconds. Please try again later or contact the Helpdesk", "Application timeout")
		}
		/**
		 * Safely calls the callback function using some data.
		 * 
		 * @param data Object
		 */					
		private function invokeCallback(data:Object):void {
			if (callback == null) return
			callback.call(this,data);
		}	
		/**
		 * String that provides information about this client.
		 */ 
		private function traceInfo():String {
			return "\t[httpclient:" + String(correlationID) + '] '
		}					
	}
}