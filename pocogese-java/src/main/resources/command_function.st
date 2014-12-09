		/**
		 * Command API function. The _replyReceivedHandler function (optional) will be called with a Reply object 
		 *
		 * @param _replyReceivedHandler Function called when the command has been processed and a Reply is received.		 
		 */
		public function $name$($parameter_declarations$_replyReceivedHandler:Function = null):void {
			trace('$class$#$name$($parameter_values$)')
			var _cmd:Command = new Command("$name$")
			$parameter_assignments$				
			new HttpClient("e4x").send_post(_cmd.toXML(),this._baseURL + "$command_path$",_replyReceivedHandler)
		}