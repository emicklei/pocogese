package com.philemonworks.pocogese.command;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import com.philemonworks.pocogese.Controller;
import com.philemonworks.pocogese.api.APIReporter;
import com.philemonworks.pocogese.util.BufferedXMLWriter;

public class APIReporterTest extends TestCase {
	
	public void testReportAPI(){
		Map<String, Controller> controllers = new HashMap<String, Controller>();
		controllers.put("test", new ExampleController());
		BufferedXMLWriter xml = new BufferedXMLWriter();
		new APIReporter().reportAPI(controllers, xml);
		System.out.println(xml.toXML());
	}
}
