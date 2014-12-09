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
package com.philemonworks.flex.test
{
	import flexunit.framework.TestCase;
	import flash.events.Event;
	import flash.system.System;
	
	/**
	 * ControllerTestCase is an extension to the base class TestCase.
	 * It provides methods to setup a timeout for testing asynchronous calls.
	 * ControllerClient implementation use asynchronous message sends through HttpService.
	 * Subclasses should use the following pattern:
	 * 
	 * <pre>public function testSomething():void {
	 * 			this.setupTimeout()  // DO NOT move this to setUp() !!
	 * 			...invoke the client method...
	 * 		}
	 * 		public function handleSomething(data:Object):void {
	 * 			this.abortTimeout()
	 * 			assert (data is expected)
	 * 				
	 * 		}
	 * </pre>
	 * 
	 * The processing time of each test is traced.
	 * 
	 * @author Ernest.Micklei
	 */
	public class ControllerTestCase extends TestCase
	{		
		private var _timeout:Function;
		private var _startTime:Date;
		
		
		public function ControllerTestCase(methodName:String=null)
		{
			super(methodName);
		}		
		/**
		 * Mark the time before running the test
		 */
		override public function setUp():void {
			super.setUp()
			_startTime = new Date()
		}
		/**
		 * Report the time of running the test
		 */
		override public function tearDown():void {
			var now:Date = new Date()
			this.locationTrace("Finished in "+(now.milliseconds - _startTime.milliseconds) + " [ms]")
			super.tearDown()
		}				
		/**
		 * Setup a Asyn method that calls handleTimeout unless abortTimeout is called before the timeout milliseconds.
		 */
		public function setupTimeout(timeout:int = 5000):void {
			_timeout = addAsync(handleTimeout,timeout)
		}
		/**
		 * Abort the timeout notification by dispatching an Event.
		 * This function can also be used as a xmlReceivedHandler if a test is not interested in the exact result 
		 */
		public function abortTimeout(data:Object = null):void {
			if (_timeout == null) {
				this.locationTrace('No timer was set. You forgot to call setupTimeout() first or called abortTimeout() mistakenly.')
				fail('see console')
				return
			}
			_timeout.call(this,new Event("ControllerTestCase.timeout"))
		}
		/**
		 * Stub function that is called when a timeout occurred before it was aborted.
		 */
		public function handleTimeout(event:Event):void {
			_timeout = null
		}
		protected function locationTrace(what:String):void {
			trace(this.toString() + ' : ' + what)
		}		
	}
}