package com.forms;

import com.db.SessionVars;

public class MyVars {
	/**
	 * the key into the session variables table
	 */
//	String uniqueName = null;
//	// for testing only
//	public int testNumber = 7;

//	SessionVars sVars = null;
	public MyVars() {

	}

	private MyVars(Object obj, SessionVars sVars, String uniqueName) throws Exception {
		if (sVars.session == null) {
			// using testSessionVariables to store objects
			
			sVars.testSessionVariables.put(uniqueName, new Object());
			if (sVars.testSessionVariables.containsKey(uniqueName)) {
				obj = sVars.testSessionVariables.get(uniqueName);
			} else {
				sVars.testSessionVariables.put(uniqueName, new Object());
			}
		} else {
			// using session to store MyVars
			// if this instance is not in there
			if (sVars.session.getAttribute(uniqueName) == null) {
				// put it in
				sVars.session.setAttribute(uniqueName, new Object());
			} else {
				obj = sVars.session.getAttribute(uniqueName);
				obj = new Object();
			}
		}
	}

	public static void get(Object obj, SessionVars sVars, String uniqueName) throws Exception {
		if (uniqueName == null)
			throw new Exception("null unique name");
		// if this is a test case
		if (sVars.session == null) {
			if (sVars.testSessionVariables.containsKey(uniqueName)) {
				obj = sVars.testSessionVariables.get(uniqueName);
			} else {
				new MyVars(obj, sVars, uniqueName);
			}
		} else {
			// using session to store MyVars
			// if this instance is not in there
			if (sVars.session.getAttribute(uniqueName) == null) {
				// put it in
				new MyVars(obj, sVars, uniqueName);
			} else {
				// already in memory
				obj = sVars.session.getAttribute(uniqueName);
			}
		}
	}
}
