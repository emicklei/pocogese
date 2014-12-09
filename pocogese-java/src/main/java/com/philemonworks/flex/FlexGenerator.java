package com.philemonworks.flex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

public class FlexGenerator {
	private static final StringTemplateGroup FLEXGROUP = new StringTemplateGroup("flex", "src/main/resources");

	public String commandPath;

	public String javaSource;

	public String flexSource;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println(FLEXGROUP.getInstanceOf("explain").toString());
			return;
		}
		try {
			System.out.println("FlexGenerator begin");
			FlexGenerator gen = new FlexGenerator();
			gen.javaSource = args[0];
			gen.flexSource = args[1];
			gen.commandPath = args[2];
			gen.generate();
			System.out.println("FlexGenerator done");
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

	public void generate() throws Exception {
		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSourceTree(new File(javaSource));
		JavaClass cls[] = builder.getClasses();
		for (int i = 0; i < cls.length; i++) {
			JavaClass each = cls[i];
			if (!each.isInterface())
				if (each.getSuperJavaClass().getName().equals("Controller")) {
					File eachFile = new File(flexSource, each.getName() + "Client.as");
					this.processInterface(each, eachFile);
					System.out.println("FlexGenerator created " + eachFile);
				}
		}
	}

	private void processInterface(JavaClass theInterface, File outputFile) throws IOException {
		outputFile.getParentFile().mkdirs();
		FileOutputStream out = new FileOutputStream(outputFile);
		out.write(this.generateFromInterface(theInterface).getBytes());
	}

	private String generateFromInterface(JavaClass each) {
		StringTemplate st = FLEXGROUP.getInstanceOf("client");
		st.setAttribute("timestamp", new Date());
		st.setAttribute("package", each.getPackage());
		st.setAttribute("class", "Remote" + each.getName());
		st.setAttribute("functions", this.generateFunctionsFromInterface(each));
		return st.toString();
	}

	private String generateFunctionsFromInterface(JavaClass each) {
		StringWriter sw = new StringWriter();
		for (int i = 0; i < each.getMethods().length; i++) {
			JavaMethod eachMethod = each.getMethods()[i];
			sw.write(this.generateFlexDoc(eachMethod));
			sw.write("\t\t"); // leading spaces and tabs are removed by
			// StringTemplate
			sw.write(this.generateFunction(eachMethod));
			sw.write("\n");
		}
		return sw.toString();
	}

	private String generateFlexDoc(JavaMethod method) {
		StringWriter sw = new StringWriter();
		sw.write("\t\t/**\n");
		DocletTag[] tags = method.getTags();
		for (int i = 0; i < tags.length; i++) {
			DocletTag each = tags[i];
			if (!"return".equals(each.getName())) { // no return
				sw.write("\t\t * @");
				sw.write(each.getName());
				sw.write(' ');
				sw.write(each.getValue());
				sw.write("\n");
			}
		}
		// always reply parameter
		sw.write("\t\t * @param replyHandler : Function\n");
		sw.write("\t\t */\n");
		return sw.toString();
	}

	private String generateFunction(JavaMethod method) {
		StringTemplate st = FLEXGROUP.getInstanceOf("function");
		st.setAttribute("command_path", commandPath);
		st.setAttribute("parameter_declarations", this.generateParameterDeclarations(method));
		st.setAttribute("parameter_assignments", this.generateParameterAssignments(method));
		st.setAttribute("name", method.getName());
		return st.toString();
	}

	private String generateParameterDeclarations(JavaMethod method) {
		JavaParameter[] params = method.getParameters();
		StringWriter sw = new StringWriter();
		for (int i = 0; i < params.length; i++) {
			if (i > 0)
				sw.write(", ");
			JavaParameter each = params[i];
			sw.write(each.getName());
			sw.write(":String"); // TODO: the generator only accepts String
			// parameters
		}
		return sw.toString();
	}

	private String generateParameterAssignments(JavaMethod method) {
		JavaParameter[] params = method.getParameters();
		StringWriter sw = new StringWriter();
		for (int i = 0; i < params.length; i++) {
			if (i > 0)
				sw.write('\n');
			JavaParameter each = params[i];
			sw.write("cmd.setParameter(");
			sw.write('"');
			sw.write(each.getName());
			sw.write("\",");
			sw.write(each.getName());
			sw.write(")");
		}
		return sw.toString();
	}
}
