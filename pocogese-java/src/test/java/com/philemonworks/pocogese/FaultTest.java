package com.philemonworks.pocogese;

import com.philemonworks.pocogese.RequestFault;
import com.philemonworks.pocogese.util.XMLWriter;
import com.philemonworks.pocogese.util.XSDWriter;
import junit.framework.TestCase;

public class FaultTest extends TestCase {
	
	public void testFaultSchema(){
		XSDWriter xsd = new XSDWriter(System.out);
		xsd.openSchema2001();
		RequestFault.write(xsd);
		xsd.closeSchema();
	}
	
	public void testFault(){
		RequestFault fault = new RequestFault();
		fault.request = "/here";
		fault.method = "PUT";
		fault.code = "987";
		fault.message = "<testmessage>";
		fault.stackTrace = "?";
		fault.write(new XMLWriter(System.out));
	}
}
