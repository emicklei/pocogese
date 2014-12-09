package com.philemonworks.pocogese.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BufferedXMLWriter extends XMLWriter {
	public ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);
	
	public BufferedXMLWriter() {
		super(null);
		this.out = new PrintStream(buffer);
	}
	
	public String toXML(){
		return buffer.toString();
	}
}
