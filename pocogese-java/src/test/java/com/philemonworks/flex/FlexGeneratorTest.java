package com.philemonworks.flex;

import junit.framework.TestCase;

public class FlexGeneratorTest extends TestCase {
	
	public void testGenerator() throws Exception {
		FlexGenerator gen = new FlexGenerator();
		gen.javaSource = ".";
		gen.flexSource = "c:/temp";
		gen.commandPath = "compath";
		gen.generate();
	}
}
