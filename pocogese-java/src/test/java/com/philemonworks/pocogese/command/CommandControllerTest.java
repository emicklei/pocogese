package com.philemonworks.pocogese.command;

import java.io.ByteArrayInputStream;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import com.philemonworks.pocogese.Invocation;

public class CommandControllerTest extends TestCase {

	public void setUp(){ BasicConfigurator.configure(); }
	
	public void testExample(){
		ExampleController ec = new ExampleController();
		MockHttpRequest request = new MockHttpRequest();
		request.setInputStream(new ByteArrayInputStream("<command name='example' />".getBytes()));
		Invocation invocation = new Invocation(request,new MockHttpResponse());
		invocation.tokens = new String[]{"command"};
		ec.doPost(invocation);
	}
	public void testExampleUnknown(){
		ExampleController ec = new ExampleController();
		MockHttpRequest request = new MockHttpRequest();
		request.setInputStream(new ByteArrayInputStream("<command name='unkown' />".getBytes()));
		Invocation invocation = new Invocation(request,new MockHttpResponse());
		invocation.tokens = new String[]{"command"};
		ec.doPost(invocation);
	}
}
