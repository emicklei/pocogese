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
	import flash.net.URLVariables;
	
	import mx.managers.CursorManager;
	import mx.rpc.http.HTTPService;
	
	/**
	 * HttpClient is a small wrapper for the standard HTTPService
	 * that handles both ResultEvents and FaultEvents when sending a GET or POST requests.
	 * 
	 * ResultEvents are processed to call the function with a Reply or XML object
	 * FaultEvents are processed by opening a FaultWindow reporting the problem
	 * 
	 * @author ernest.micklei@philemonworks.com, 2007
	 */
	public class HttpClient
	{
		private static var InstanceCount:int = 0;
		private static var BusySemaphore:int = 0;
		public  static var DEBUG:Boolean = true;  // the application that uses this client should set it to false for production stage.
		public  static var DefaultTimeout:int = 10;
		
		private var correlationID:int = InstanceCount++;
		private var http:HTTPService = new HTTPService();
    	/**
		 *  Constructor with other result format.
		 */
	    public function HttpClient(resultFormat:String = "object") {
			super();
			http.useProxy = false
			http.resultFormat = resultFormat;	
			http.requestTimeout = DefaultTimeout // seconds before give up			
	    }	    
	    public function setTimeout(timeout:Number):void {
	    	http.requestTimeout = timeout	    	
	    }
		/**
		 * Called just before the HttpService will send the request.
		 */ 
		internal static function beforeSent():void {
			BusySemaphore++;
			if (BusySemaphore == 1) CursorManager.setBusyCursor();
		}
		/**
		 * Called just after the HttpService has received a response.
		 */ 		
		internal static function afterReceive():void {
			BusySemaphore--;
			if (BusySemaphore == 0) CursorManager.removeBusyCursor();
		}
		/**
		 * String that provides information about this client.
		 */ 
		private function traceInfo():String {
			return "\t[httpclient:" + String(correlationID) + '] '
		}
		/**
		 * Send the data in a POST request to a service at the url.
		 * The callback function is called when the request was succesfull.
		 * This function will get a Reply object as its parameter value.
		 * 
		 * @param data XML
		 * @param url String
		 * @param callback Function one-argument Function with a parameter of type Reply
		 */
		public function send_post(data:XML,url:String,callback:Function):void {
			trace(this.traceInfo() + "POST:" + url + "\n"+data);			
			var handler:ResponseHandler = new ResponseHandler(callback)
			handler.correlationID = correlationID
			this.http.url = url;
			this.http.contentType = "application/xml"
			this.http.method = "POST"		
			HttpClient.beforeSent()
			this.http.send(data).addResponder(handler)			
		}
		/**
		 * Send the GET request to a service at the url.
		 * The callback function is called when the request was succesfull.
		 * This function will get an XML object as its parameter value.
		 * 
		 * @param url String
		 * @param callback Function one-argument Function with a parameter of type XML
		 */	
		public function send_get(url:String,callback:Function):void {
			trace(this.traceInfo() + "GET:" + url);
			var handler:ResponseHandler = new ResponseHandler(callback)
			handler.correlationID = correlationID
			this.http.url = url;
			this.http.method = "GET"
			HttpClient.beforeSent();
			this.http.send().addResponder(handler)						
		}
		/**
		 * Put a key value pair in a URLVariable by only if the key, value != null or empty.
		 * This helper method exists for composing a Selection request within.
		 * 
		 * @param vars URLVariables the container of the key-value pairs
		 * @param key String
		 * @param value String
		 */ 
		public static function storeQueryPair(vars:URLVariables,key:String,value:String):void {
			if (key == null) return
			if (value == null) return
			if (key.length == 0) return
			if (value.length == 0) return
			vars[key]=value
		}
	}
}