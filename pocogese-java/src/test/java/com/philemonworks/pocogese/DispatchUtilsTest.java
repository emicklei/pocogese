package com.philemonworks.pocogese;

import junit.framework.TestCase;
import com.philemonworks.pocogese.command.Command;
import com.philemonworks.pocogese.command.ExampleController;
import com.philemonworks.pocogese.command.MockHttpRequest;
import com.philemonworks.pocogese.command.MockHttpResponse;
import com.philemonworks.pocogese.command.Reply;
import com.philemonworks.pocogese.command.Selection;
import com.philemonworks.pocogese.util.DispatchUtils;

public class DispatchUtilsTest extends TestCase {
	private ExampleController receiver;

	private MockHttpRequest request;

	private MockHttpResponse response;

	private Invocation invocation;

	private Selection selection;

	private Command command;

	public void setUp() {
		receiver = new ExampleController();
		request = new MockHttpRequest();
		response = new MockHttpResponse();
		invocation = new Invocation(request, response);
		selection = new Selection();
		command = new Command();
	}

	public void assertResponseEndsWith(String end) {
		String content = ((MockHttpResponse) invocation.response).getContent();
		assertTrue(content.endsWith(end));
	}

	public void testInvokeZeroArgSelection() {
		selection.name = "selectionNoArg";
		DispatchUtils.execute(receiver, selection, invocation);
		this.assertResponseEndsWith("selectionNoArg");
	}

	public void testInvokeOneArgSelection() {
		selection.name = "selectionOneArg";
		selection.putParameter("arg0", "val0");
		DispatchUtils.execute(receiver, selection, invocation);
		this.assertResponseEndsWith("selectionOneArg:val0");
	}

	public void testInvokeTwoArgSelection() {
		selection.name = "selectionTwoArgs";
		selection.putParameter("arg0", "val0");
		selection.putParameter("arg1", "val1");
		DispatchUtils.execute(receiver, selection, invocation);
		this.assertResponseEndsWith("selectionTwoArgs:val0 and:val1");
	}

	public void testInvokeZeroArgCommand() {
		command.name = "logout";
		DispatchUtils.execute(receiver, command, invocation);
		String xml = new String(invocation.getOutputBytes());
		Reply reply = Reply.fromXML(xml);
		assertEquals("info", reply.status);
	}

	public void testSelectExample() {
		selection.name = "selectExample";
		DispatchUtils.execute(receiver, selection, invocation);
	}

	public void testSelectExampleList() {
		selection.name = "selectExampleList";
		DispatchUtils.execute(receiver, selection, invocation);
	}

	public void testSaveExample() {
		command.name = "saveExample";
		command.putParameter("toSave", "<example/>");
		DispatchUtils.execute(receiver, command, invocation);
		String xml = new String(invocation.getOutputBytes());
		Reply reply = Reply.fromXML(xml);
		assertEquals("info", reply.status);
	}
}
