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
package com.philemonworks.pocogese.api;
/**
 * This annotation is used to mark a method as being part of the Pocogese API
 * The value is used to store the comma separated list of method argument names.
 * 
 * Example use:
 * 
 * @SelectionParameterNames("recordID")
 * @SelectionParameterNames("from,to,total,sortkey,sortmethod,searchpattern")
 * 
 * @author Ernest.Micklei
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectionParameterNames {
			
	String value();
}
