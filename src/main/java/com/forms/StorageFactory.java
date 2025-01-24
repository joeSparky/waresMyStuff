package com.forms;

import com.db.SessionVars;

public abstract class StorageFactory {
	protected abstract Object getNew();
	
	public Object get(Object tc, SessionVars sVars, String unique) {
		Object results=null;
		if (sVars.session == null) {
			// using testSessionVariables
			if (sVars.testSessionVariables.containsKey(unique))
				// overwrite input TestClass with stored TestClass
				results = sVars.testSessionVariables.get(unique);
			else {
				// fresh start
				if (tc == null)
					results = getNew();
				sVars.testSessionVariables.put(unique, results);
			}
		} else {
			// using session variables
			if (sVars.session.getAttribute(unique) == null) {
				// not in session
				if (tc == null)
					results = getNew();
				sVars.session.setAttribute(unique, results);
			} else {
				// overwrite input TestClass with stored TestClass
				results =  sVars.session.getAttribute(unique);
			}
		}
		return results;
	}
}
