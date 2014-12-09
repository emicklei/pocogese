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
package com.philemonworks.pocogese;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.philemonworks.pocogese.util.BufferedXMLWriter;
import com.philemonworks.pocogese.util.DispatchUtils;
/**
 * Page represents a subset of the result of a query for objects.
 * The subset is defined by indices @from and @to.
 * Attribute @total indicates the size of the result of which this Page is a subset.
 * Page entries are stored in @elements.
 * To support sorted results and to inspect what sort criteria was used for the query,
 * the attributes @sortkey and @sortmethod are also part of the Page.
 * 
 * @author Ernest.Micklei
 */
public class Page {
	public int from, to, total = 0;

	public String sortkey, sortmethod, searchpattern;

	public List elements = new ArrayList();

	public Page(String from, String to, String sortkey, String sortmethod, String searchpattern) {
		this.from = Integer.parseInt(from);
		this.to = Integer.parseInt(to);
		this.sortkey = sortkey;
		this.sortmethod = sortmethod;
		this.searchpattern = searchpattern;
	}

	public Map attributesMap() {
		HashMap map = new HashMap();
		map.put("to", String.valueOf(to));
		map.put("from", String.valueOf(from));
		map.put("total", String.valueOf(total));
		map.put("sortkey", sortkey);
		map.put("sortmethod", sortmethod);
		return map;
	}

	public String toXML() {
		BufferedXMLWriter bxw = new BufferedXMLWriter();
		bxw.tag("list", this.attributesMap(), false);
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			bxw.raw(DispatchUtils.xmlSerialize(iter.next()), true);
		}
		bxw.end();
		return bxw.toXML();
	}
	
	public boolean isAscending(){
		return "ascending".equals(sortmethod);
	}	
	
	public void add(Object element) {
		elements.add(element);
	}
}
