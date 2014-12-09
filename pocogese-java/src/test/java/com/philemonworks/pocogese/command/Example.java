package com.philemonworks.pocogese.command;

public class Example {
	
	public String toXML() {
		return "<example/>";
	}

	/** 
	 * @param xml
	 * @return
	 */
	public static Example fromXML(String xml) {
		return new Example();
	}	
}
