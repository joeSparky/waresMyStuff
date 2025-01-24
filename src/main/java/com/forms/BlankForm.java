package com.forms;

import com.db.SessionVars;

public class BlankForm extends SmartForm{

	protected BlankForm(SessionVars sVars) {
		super(sVars, BlankForm.class.getCanonicalName());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7128071597923556162L;
//	super(new SessionVars());

}
