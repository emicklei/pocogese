/*
 * Copyright 2007 Ernest Micklei @ PhilemonWorks.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.philemonworks.pocogese.command;

import java.util.ArrayList;
import java.util.List;
import com.philemonworks.pocogese.Controller;
import com.philemonworks.pocogese.Page;
import com.philemonworks.pocogese.api.CommandParameterNames;
import com.philemonworks.pocogese.api.SelectionParameterNames;

public class ExampleController extends Controller implements IExampleController {
	
	@CommandParameterNames("")
	public Reply example(){
		return Reply.info("example");
	}

	@SelectionParameterNames("")
	public Object selectionNoArg() {
		return "selectionNoArg";
	}
	@SelectionParameterNames("arg0")
	public Object selectionOneArg(String arg0) {
		return "selectionOneArg:"+arg0;
	}
	@SelectionParameterNames("arg0,arg1")
	public Object selectionTwoArgs(String arg0, String arg1) {
		return "selectionTwoArgs:"+arg0+" and:" + arg1;
	}
	@SelectionParameterNames("z,a")
	public Object selectionOrderedArgs(String z, String a) {
		return "selectionOrderedArgs:"+z+" and:" + a;
	}	
	@SelectionParameterNames("")
	public Example selectExample() {
		return new Example();
	}
	@SelectionParameterNames("")
	public List selectExampleList(){ 
		List<Example> list= new ArrayList<Example>();
		list.add(new Example());
		list.add(new Example());
		return list;
	}
	@CommandParameterNames("")
	public Reply logout() {
		return Reply.info("it is");
	}
	@CommandParameterNames("toSave")
	public Reply saveExample(Example toSave){
		return Reply.info("RecordSaved");
	}

	@SelectionParameterNames("from,to,sortkey,sortmethod,searchpattern")
	public Page listPage(String from, String to, String sortkey, String sortmethod, String searchpattern) {
		// TODO Auto-generated method stub
		return null;
	}
}
