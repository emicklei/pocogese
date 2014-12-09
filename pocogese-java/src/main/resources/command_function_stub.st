		/**
		 * Command API function. The _replyReceivedHandler function (optional) will be called with a Reply object
		 *
		 * @param _replyReceivedHandler Function called when the command has been processed and a Reply is received.		 
		 */
		function $name$($parameter_declarations$_replyReceivedHandler:Function = null):void {
			trace('$class$#$name$($parameter_values$)')
			_replyReceivedHandler.call(this, new Reply())
		}