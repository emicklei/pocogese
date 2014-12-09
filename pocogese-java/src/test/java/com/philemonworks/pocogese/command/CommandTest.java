package com.philemonworks.pocogese.command;

import junit.framework.TestCase;
import com.philemonworks.pocogese.command.Command;
import com.philemonworks.pocogese.command.CommandXmlIO;
import com.philemonworks.pocogese.command.Reply;
import com.philemonworks.pocogese.command.ReplyXmlIO;
import com.philemonworks.pocogese.util.XMLWriter;

public class CommandTest extends TestCase {
	public void testSimple() {
		Command cmd = new Command("login");
		cmd.putParameter("name", "ernest");
		cmd.putParameter("password", "lisa");
		this.dump(cmd);
	}

	public void dump(Command cmd) {
		CommandXmlIO io = new CommandXmlIO();
		io.write(cmd, new XMLWriter(System.out));
	}

	public void testXmlAsParameter() {
		Command cmd = new Command("login");
		Reply param = Reply.info("test");
		cmd.putParameter("reply", ReplyXmlIO.toXml(param));
		this.dump(cmd);
		
		String cmdXml = CommandXmlIO.toXml(cmd);
		Command cmd_clone = CommandXmlIO.fromXml(cmdXml);
		this.dump(cmd_clone);
		String xml = cmd.getParameter("reply", null);
	}
	
	public void testCommandToString() {
		Command cmd = new Command("save");
		cmd.putParameter("test1", "value1");
		cmd.putParameter("test2", "value2");
		System.out.println(cmd.toString());
	}
}
