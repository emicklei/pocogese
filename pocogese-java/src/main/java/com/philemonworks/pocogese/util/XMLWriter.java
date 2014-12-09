/*
    Copyright 2004 Ernest Micklei @ PhilemonWorks.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   
*/
package com.philemonworks.pocogese.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * XMLWriter is a generic class that provides common behavior to
 * writers of a tagged language such as XML, WordML and HTML.
 * Subclasses should implement methods that reflect the supported tags.
 * 
 * @author E.M.Micklei
 */
public class XMLWriter {
    /**
     * 
     */
    public static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n";
    /**
     * Controls whether the output of nicely indented. Consider setting this value to false for production.
     */
    public boolean pretty = true;
    /**
     * Indent level of the last tag added.
     */
    protected int indent = 0;
    /**
     * Contains the list of open tags which is used to close them in the right order.
     */
    protected List stack = new ArrayList();
    /**
     * Output stream to which content is written.
     */
    public PrintStream out;

    /**
     * @param out : PrintStream
     */
    public XMLWriter(PrintStream out) {
        super();
        this.out = out;
    }
    /**
     * @param out : OutputStream
     */
    public XMLWriter(OutputStream out) {
        this(new PrintStream(out));
    }	
    /**
     * Inserts the standard XML header and specifies the UTF-8 encoding.
     */
    public void xml() {
        this.raw(XMLHEADER, true);
    }
    /**
     * Insert a reference to a stylesheet
     * @param href
     * @return
     */
    public XMLWriter stylesheet(String href) {
    	this.raw("<?xml-stylesheet type=\"text/xsl\" href=\"");
    	this.print(href);
    	this.raw("\"?>");
    	return this;
    }
    /**
     * Close the current open tag.
     * @return XMLWriter to allow cascading
     */
    public XMLWriter end() {
		return this.end(null); // means unknown
    }
    /**
     * Close the current open tag which is expected to be <code>expectedTag</code>.
     * @param expectedTag String the tag that is closed
     * @return XMLWriter to allow cascading
     */
	public XMLWriter end(String expectedTag){
        int last = stack.size() - 1;
        String top;
        try {
            top = (String) stack.get(last);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new RuntimeException("closing:" + expectedTag + " but no end tags were expected");
        }
		if (expectedTag != null)
			if (!top.equals(expectedTag))
				throw new RuntimeException("closing:" + top + " but expected:" + expectedTag);
        stack.remove(last);
        this.indent();
        out.print("</");
        out.print(top);
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;	
	}
    /**
     * Output a String which will be encoded (e.g. replacing &lt; and &gt;)
     * @param any : String | null
     * @return XMLWriter to allow cascading
     */
    public XMLWriter print(String any) {
        return this.print(any, true);
    }

    /**
     * Output a String which will be encoded (e.g. replacing &lt; and &gt;)
     * @param any : String | null
     * @param appendNewLine : boolean that controls whether a new line must be written
     * @return XMLWriter to allow cascading
     */
    public XMLWriter print(String any, boolean appendNewLine) {
        return this.print(true, any, appendNewLine);
    }

    /**
     * Output a String which will be encoded (e.g. replacing &lt; and &gt;)
     * @param doIndent : boolean that controls whether an indentation must be written
     * @param any : String | null
     * @param appendNewLine : boolean that controls whether a new line must be written
     * @return XMLWriter to allow cascading
     */
    public XMLWriter print(boolean doIndent, String any, boolean appendNewLine) {
        if (null == any)
            return this;
        if (doIndent)
            this.indent();
        XMLWriter.encodeOn(any, out);
        if (pretty && appendNewLine)
            out.print('\n');
        return this;
    }

    /**
     * Output a String without encoding. Use print(String any) otherwise.
     * No indentation is performed.
     * @param any : String
     * @return XMLWriter to allow cascading
     */
    public XMLWriter raw(String any) {
        return this.raw(any, false);
    }

    /**
     * Output a char without encoding.
     * No indentation is performed.
     * @param any : char
     * @return XMLWriter to allow cascading
     */
    public XMLWriter raw(char any) {
        out.print(any);
        return this;
    }    
    
    /**
     * Output a String without encoding. Use print(String any) otherwise.
     * No indentation is performed.
     * @param any : String
     * @param appendNewLine : boolean that controls whether a new line must be written
     * @return XMLWriter to allow cascading
     */
    public XMLWriter raw(String any, boolean appendNewLine) {
        if (null == any)
            return this;
        out.print(any);
        if (pretty && appendNewLine)
            out.print('\n');
        return this;
    }

    /**
     * Close the underlying stream (e.g. a Socket or a FileStream).
     * Because no more output can be written, the stack of open tags is inspected for emptiness.
     */
    public void close() {
        out.flush();
        // also check all tags have been closed
        if (!stack.isEmpty())
            throw new RuntimeException("One or more end-tags are missing: " + stack);
        out.close();
    }

    /**
     * Convert a String to an encoded version of that String replacing
     * ML delimiter characters by special entities.
     * @param any
     * @return encoded argument String
     */
    public static String encoded(String any) {
        // Replace <, >, ' and " by special entities
        ByteArrayOutputStream bos = new ByteArrayOutputStream(any.length());
        XMLWriter.encodeOn(any, new PrintStream(bos));
        return bos.toString();
    }
    /**
     * Wrier the argument String any encoded on a stream
     * @param any
     * @param stream
     */
    public static void encodeOn(String any, PrintStream stream) {
        /**
         * See google groups
         * http://groups.google.com/groups?q=java+escape+xml&start=10&hl=en&lr=
         * &ie=UTF-8&selm=JPyaOQm2Fo7O2PeLfDykYBAsjKGw%404ax.com&rnum=12
         */
        if (any == null || any.length() < 1) {
            return;
        }
        for (int i = 0; i < any.length(); i++) {
            char c = any.charAt(i);
            int v = (int) c;
            if (v < 32 || v > 127 || v == 38 || v == 60 || v == 62 || v == 34) {
                // we must escape a character in format &#22;
                // 38 is ampersand &
                // 60 is smaller <
                // 62 is larger >
                // 34 is quote "
                stream.print('&');
                stream.print('#');
                stream.print(v);
                stream.print(';');
            } else {
                stream.print(c);
            }
        }
    }

    /**
     * Write and add a new tag using attributes which are specified as a String.
     * @param tag : String
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tag(String tag) {
        return this.tag(tag, null, false);
    }

    /**
     * Write and add a new tag using attributes which are specified as a String.
     * @param tag : String
     * @param attributesOrNull : String
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tag(String tag, String attributesOrNull) {
        this.indent();
        stack.add(tag);
        out.print('<');
        out.print(stack.get(stack.size() - 1));
        if (attributesOrNull != null) {
            out.print(' ');
            out.print(attributesOrNull);
        }
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }

    /**
     * Write and add a new tag using its attributes (if any)
     * @param tag : String
     * @param attributesOrNull : Map
     * @param isEmpty : boolean that says whether it is an empty tag (no children or content)
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tag(String tag, Map attributesOrNull, boolean isEmpty) {
        this.indent();
        stack.add(tag);
        out.print('<');
        out.print(stack.get(stack.size() - 1));
        this.printAttributes(attributesOrNull);
        if (isEmpty) {
            stack.remove(stack.size() - 1);
            out.print("/");
        }
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }

    private void printAttributes(Map attributesOrNull) {
        if (attributesOrNull != null) {
            Iterator it = attributesOrNull.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                out.print(' '); // separator
                out.print(key);
                out.print("=\"");
                XMLWriter.encodeOn((String) attributesOrNull.get(key), out);
                out.print('\"');
            }
        }
    }

    /**
     * Writes a String enclosed by a start and end tag.
     * @param tag : String the enclosing tag
     * @param any : String the contents
     * @param encode : boolean that controls replacement by special entities
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tagged(String tag, String any, boolean encode) {
    	if (any == null) return this;
        this.indent();
        out.print('<');
        out.print(tag);
        out.print('>');
        if (encode)
            XMLWriter.encodeOn(any, out);
        else
            this.raw(any);
        out.print("</");
        out.print(tag);
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }
    /**
     * Writes an int enclosed by a start and end tag.
     * @param tag : String the enclosing tag
     * @param number : int
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tagged(String tag, int number) {
        this.indent();
        out.print('<');
        out.print(tag);
        out.print('>');
        this.raw(String.valueOf(number));
        out.print("</");
        out.print(tag);
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }
    /**
     * Writes an long enclosed by a start and end tag.
     * @param tag : String the enclosing tag
     * @param number : long
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter tagged(String tag, long number) {
        this.indent();
        out.print('<');
        out.print(tag);
        out.print('>');
        this.raw(String.valueOf(number));
        out.print("</");
        out.print(tag);
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }
    /**
     * Writes a String enclosed by a start and end tag.
     * @param tag : String the enclosing tag
     * @param attributesOrNull : Map for passing any attributes
     * @param any : String the contents
     * @param encode : if true apply entity encoding
     * @return XMLWriter to allow cascading  
     */
    public XMLWriter tagged(String tag, Map attributesOrNull, String any, boolean encode) {
    	if (any == null) return this;
        this.indent();
        out.print('<');
        out.print(tag);
        this.printAttributes(attributesOrNull);
        out.print('>');
        if (encode)
            XMLWriter.encodeOn(any, out);
        else
            this.raw(any);
        out.print("</");
        out.print(tag);
        out.print('>');
        if (pretty)
            out.print('\n');
        return this;
    }

    /**
     * Output an empty without attributes
     * @param tag : String
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter emptytag(String tag) {
        this.indent();
        out.print("<" + tag + "/>");
        if (pretty)
            out.print("\n");
        return this;
    }

    /**
     * Increase the indent level for pretty output of the content.
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter indent() {
        if (!pretty)
            return this;
        for (int i = stack.size(); i > 0; i--){
        	this.doIndent();
        }
        return this;
    }

    /**
     * Flush the contents of the output stream.
     */
    public void flush() {
        out.flush();
    }

    /**
     * Start writing a tag with attributes. (&lt;tag)
     * This tag will be added to the stack so an end(); is required to close it.
     * @param tag : String
     * @return XMLWriter to allow cascading 
     */
    public XMLWriter opentag(String tag) {
        this.indent();
        out.print('<');
        out.print(tag);        
        stack.add(tag);
        return this;
    }

    /**
     * Write a single attribute which is a key-value pair unless the value is empty or null.
     * @param key : String will not be encoded
     * @param value : String will be encoded
     * @return XMLWriter to allow cascading
     */
    public XMLWriter attribute(String key, String value) {
        if (value == null || value.length() == 0)
            return this;
        out.print(' ');
        out.print(key);
        out.print("=\"");
        XMLWriter.encodeOn(value, out);
        out.print("\"");
        return this;
    }

    /**
     * Write a single attribute which is a key-value pair.
     * @param key : String will not be encoded
     * @param value : boolean
     * @return XMLWriter to allow cascading
     */
    public XMLWriter attribute(String key, boolean value) {
        return this.attribute(key, String.valueOf(value));
    }

    /**
     * Write a single attribute which is a key-value pair.
     * @param key : String will not be encoded
     * @param value : int
     * @return XMLWriter to allow cascading
     */
    public XMLWriter attribute(String key, int value) {
        return this.attribute(key, String.valueOf(value));
    }
    
    /**
     * Write a single attribute which is a key-value pair.
     * @param key : String will not be encoded
     * @param value : int
     * @return XMLWriter to allow cascading
     */
    public XMLWriter attribute(String key, long value) {
        return this.attribute(key, String.valueOf(value));
    }
    
    /**
     * Write a single attribute which is a key-value pair. Uses xsd:dateTime format
     * @param key : String will not be encoded
     * @param value : Date || null
     * @return XMLWriter to allow cascading
     */
    public XMLWriter attribute(String key, Date value) {
    	if (value == null) return this;
    	String formatted  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(value);    	
        return this.attribute(key, formatted);
    }    

    /**
	 * Close the opening tag with attributes. (&gt;)
	 * @return the writer
	 */
	public XMLWriter closetag() {
	    out.print('>');
	    if (pretty)
	        out.print("\n");
	    return this;
	}

    /**
     * Close the opening tag with attributes as an empty tag (no child elements or content).
     * Example:
     * 		&lt;tag name="value" /&gt;
     * @return XMLWriter this
     * 
     */
    public XMLWriter closeemptytag() {
        out.print("/>");
        if (pretty)
            out.print("\n");
        stack.remove(stack.get(stack.size() - 1));
        return this;
    }
    /**
     * Shallow copies the argument and returns a new Map
     * @param otherMap : Map
     * @return Map : the copy
     */
    public static Map copyMap(Map otherMap){
		Map copied = new HashMap(otherMap.size());
		for (Iterator iter = otherMap.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			copied.put(key,otherMap.get(key));
		}
		return copied;
    }
    /**
     * Create a new Map using the key-value pair.
     * This is a convenient way to create an attributes map.
     * @param key 
     * @param value 
     * @return Map
     */
    public Map newMap(String key, String value) {
        Map map = new HashMap(1);
        map.put(key, value);
        return map;
    }

    /**
     * Create a new Map using the two key-value pairs.
     * This is a convenient way to create an attributes map.
     * @param key1 
     * @param value1 
     * @param key2 
     * @param value2 
     * @return Map
     */
    public Map newMap(String key1, String value1, String key2, String value2) {
        Map map = new HashMap(2);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    /**
     * Create a new Map using the three key-value pairs.
     * This is a convenient way to create an attributes map.
     * @param key1 
     * @param value1 
     * @param key2 
     * @param value2 
     * @param key3 
     * @param value3 
     * @return Map
     */
    public Map newMap(String key1, String value1, String key2, String value2, String key3, String value3) {
        Map map = new HashMap(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    /**
     * Create a new Map using the three key-value pairs.
     * This is a convenient way to create an attributes map.
     * @param key1 
     * @param value1 
     * @param key2 
     * @param value2 
     * @param key3 
     * @param value3 
     * @param key4 
     * @param value4 
     * @return Map
     */
    public Map newMap(String key1, String value1, String key2, String value2, String key3, String value3, String key4, String value4) {
        Map map = new HashMap(3);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }    
    
    /**
     * Convenience method for tag(String,Map,boolean) for a single entry Map and no elements;
     * @param tag : String
     * @param attributeKey : String 
     * @param attributeValue : String
     * @return XMLWriter this
     */
    public XMLWriter tag(String tag, String attributeKey, String attributeValue) {
        return this.tag(tag, newMap(attributeKey, attributeValue), false);
    }
    
    private void doIndent(){
    	out.print(' ');
        out.print(' ');
    }
}