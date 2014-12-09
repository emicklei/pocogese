package com.philemonworks.pocogese.command;

import java.util.List;
import com.philemonworks.pocogese.api.IPaginator;

/**
 * @author Ernest.Micklei
 *
 */
public interface IExampleController extends IPaginator{
	/**
	 * @return
	 */
	Reply example();

	/**
	 * @return
	 */
	Object selectionNoArg();

	/**
	 * @param arg0
	 * @return
	 */
	Object selectionOneArg(String arg0);

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	Object selectionTwoArgs(String arg0, String arg1);

	/**
	 * @param z
	 * @param a
	 * @return
	 */
	Object selectionOrderedArgs(String z, String a);

	/**
	 * @return
	 */
	Example selectExample();

	/**
	 * @return
	 */
	List selectExampleList();

	/**
	 * @return
	 */
	Reply logout();

	/**
	 * @param toSave
	 * @return
	 */
	Reply saveExample(Example toSave);
}
