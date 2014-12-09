package example.service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.philemonworks.pocogese.IServletConfigurator;
import com.philemonworks.pocogese.InvocationServlet;
import com.philemonworks.pocogese.command.ExampleController;

public class PocogeseConfigurator implements IServletConfigurator {

	public void configure(InvocationServlet servlet, ServletConfig config) throws ServletException {
		servlet.configure("/test", new ExampleController());	
	}

}
