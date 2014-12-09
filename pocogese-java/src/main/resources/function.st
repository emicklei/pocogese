public function $name$($parameter_declarations$, replyHandler:Function):void {
			var restclient:RestClient = new RestClient()
			var cmd:Command = new Command("$name$")
			$parameter_assignments$				
			restclient.remoteExecute(cmd,"$command_path$",replyHandler)
		}