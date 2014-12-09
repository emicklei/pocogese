/*
    Copyright 2007 Ernest Micklei @ PhilemonWorks.com

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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;
/**
 * XSDWriter is a helper class to produce xml schemas in Java.
 * 
 * @author ernest
 */
public class XSDWriter extends XMLWriter {
	private String schemaNamespace = "xs";
	
	public XSDWriter(PrintStream out) {
		super(out);
	}

	public XSDWriter(OutputStream out) {
		super(out);
	}
	
	public XSDWriter openSchema2001(){
		this.raw("<"+schemaNamespace+":schema xmlns:"+schemaNamespace+"=\"http://www.w3.org/2001/XMLSchema\" >\n");
		return this;
	}
	public XSDWriter closeSchema(){
		this.raw("</"+schemaNamespace+":schema>");
		return this;
	}
	
	public XSDWriter complexType(){
		this.tag(schemaNamespace+":complexType",null,false);
		return this;
	}
	
	public XSDWriter element(String name,String type){
		this.tag(schemaNamespace+":element",this.newMap("name", name, "type", type),true);
		return this;
	}	
	
	public XSDWriter element(Map attributes){
		this.tag(schemaNamespace+":element",attributes,true);
		return this;
	}
	
	/**
	 * Needs to be closed with .end()
	 * @param name
	 * @return
	 */
	public XSDWriter element(String name){
		this.tag(schemaNamespace+":element",this.newMap("name", name),false);
		return this;
	}
	
	public XSDWriter sequence(){
		this.tag(schemaNamespace+":sequence");
		return this;
	}
	
	public Map ref_min_max(String ref, String min, String max){
		Map map = this.newMap("ref",ref);
		map.put("maxOccurs",max);
		map.put("minOccurs", min);
		return map;
	}
	
	public XSDWriter requiredStringElement(String name){
		return this.element(this.newMap("name",name,"type","xs:string","minOccurs","1","maxOccurs","1"));
	}
}
