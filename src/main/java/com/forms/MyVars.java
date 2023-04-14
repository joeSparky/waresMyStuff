package com.forms;

import com.db.SessionVars;

public class MyVars {
	/**
	 * the key into the session variables table
	 */
	String uniqueName = null;
	SessionVars sVars = null;

	public MyVars(SessionVars sVars, String uniqueName) throws Exception {
		this.sVars = sVars;
		this.uniqueName = uniqueName;
//		sVars.putMyVars(uniqueName, this);
//		// if myVars are not in the session space
//		if (sVars.getMyVars(uniqueName) == null)
//			// write a copy
//			sVars.putMyVars(uniqueName, this);
	}

	public void put() throws Exception {
		sVars.putMyVars(uniqueName, this);
	}

	public Object get() throws Exception {
		Object local = sVars.getMyVars(uniqueName);
		// if there's no copy in the session
		if (local == null) {
			sVars.putMyVars(uniqueName, this);
			return this;
		}
		return local;
	}
}
