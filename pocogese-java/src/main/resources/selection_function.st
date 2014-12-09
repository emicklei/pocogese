		/**
		 * Selection API function.  _xmlReceivedHandler is called with an XML object
		 *
		 * @param _xmlReceivedHandler Function called when the selection is processed and the XML data is received.		 
		 */
		public function $name$($parameter_declarations$_xmlReceivedHandler:Function):void {
			trace('$class$#$name$($parameter_values$)')
			var _vars:URLVariables = new URLVariables()
$setup_urlvars$			
			var _urn:String = "$controller_path$/$name$?" + _vars.toString()
			new HttpClient("e4x").send_get(this._baseURL + _urn,_xmlReceivedHandler)
		}