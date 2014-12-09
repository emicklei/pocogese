package com.philemonworks.flex.net
{
	public class TextMessage
	{
		public var key:String; // can be used to lookup the text using NLS
		public var type:String; // warning,information,error
		public var content:String; // body of the message
		
		public function TextMessage(data:XML){
			super()
			this.key = data.@key
			this.type = data.@type
			this.content = data.text()
		}
	}
}