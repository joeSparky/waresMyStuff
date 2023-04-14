package com.parts.security;

import com.db.SessionVars;
import com.forms.SmartForm;

public class Logout extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4323131556328139043L;

	public Logout(SessionVars sVars) throws Exception {
		super(sVars, Logout.class.getCanonicalName());
		// see dispatchThis in Dispatch
	}
}
