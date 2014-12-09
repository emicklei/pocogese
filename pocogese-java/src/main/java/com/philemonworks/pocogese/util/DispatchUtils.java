package com.philemonworks.pocogese.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import com.philemonworks.pocogese.Controller;
import com.philemonworks.pocogese.Invocation;
import com.philemonworks.pocogese.RequestFault;
import com.philemonworks.pocogese.command.Command;

public class DispatchUtils {
	static final Logger LOG = Logger.getLogger(DispatchUtils.class);

	public static void execute(final Object receiver, final Command command, final Invocation invocation) {
		try {
			Method method = DispatchUtils.getMethodNamed(receiver.getClass(), command.name);
			Object[] unserializedArguments = DispatchUtils.unserializeArguments(method.getParameterTypes(), command.getParameterValueArray());
			Object result = method.invoke(receiver, unserializedArguments);
			if (result == null) {
				RequestUtils.handleInvocationException(new NullPointerException(), invocation, (Controller)receiver);
			} else if (result.getClass() == byte[].class) { 
				// write bytestream contents directly on outputstream
				// contentType should have been set
				byte[] data = (byte[])result;
				invocation.response.setContentLength(data.length);
				OutputStream out = invocation.getBufferedOutputStream();
				out.write(data);
				out.flush();
			} else if (result.getClass() == ByteArrayOutputStream.class){
				// write bytestream contents directly on outputstream
				// contentType should have been set
				ByteArrayOutputStream baos = (ByteArrayOutputStream)result;
				invocation.response.setContentLength(baos.size());
				ServletOutputStream sos = invocation.response.getOutputStream();
				baos.writeTo(sos);
				sos.flush();
			} else if (result.getClass() == String.class){
				// write String contents directly on outputstream
				// contentType should have been set
				String text = (String)result;
				invocation.response.setContentLength(text.length());
				ServletOutputStream sos = invocation.response.getOutputStream();
				sos.print(text);
				sos.flush();
			} else {
				// apply xml serialization
				String xml = DispatchUtils.serializeResult(result);
				invocation.getPreparedXMLWriter().raw(xml);
			}
		} catch (Exception e) {
			LOG.error("failed to execute [" + command.name + "]", e);
			throw new RuntimeException("Failed to execute method named \"" + command.name + "\"", e);
		}
	}

	public static Object[] unserializeArguments(Class[] types, String[] values) throws Exception {
		Object[] unserialized = new Object[values.length];
		for (int i = 0; i < unserialized.length; i++) {
			unserialized[i] = DispatchUtils.unserializeArgument(types[i], values[i]);
		}
		return unserialized;
	}

	public static Object unserializeArgument(Class clazz, String value) throws Exception {
		if (clazz == String.class)
			return value;
		// do xml unmarshalling
		Method method = clazz.getMethod("fromXML", new Class[] { String.class });
		return method.invoke(null, new Object[] { value });
	}

	public static String serializeResult(final Object result) throws Exception {
		if (result == null)
			return "<nil/>";
		if (result instanceof String)
			return (String) result;
		if (result instanceof List)
			return DispatchUtils.serializeListResult((List) result);
		Method method = result.getClass().getMethod("toXML", new Class[0]);
		return (String) method.invoke(result, new Object[0]);
	}
	
	public static String xmlSerialize(final Object result) {
		try {
			return serializeResult(result);
		} catch (Exception e) {
			LOG.error("Unable to serialize to XML",e);
			throw new RuntimeException(e);
		}
	}

	public static String serializeListResult(final List results) throws Exception {
		BufferedXMLWriter bxw = new BufferedXMLWriter();
		bxw.tag("list");
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			bxw.raw((DispatchUtils.serializeResult(iter.next())), true);
		}
		bxw.end();
		return bxw.toXML();
	}

	public static Method getMethodNamed(Class clazz, String methodName) {
		Method[] all = clazz.getMethods();
		for (int i = 0; i < all.length; i++) {
			Method each = all[i];
			if (methodName.equals(each.getName()))
				return each;
		}
		throw new RuntimeException("No implementation found for method:" + methodName);
	}
}
