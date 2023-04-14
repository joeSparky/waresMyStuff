package com.parts.security;

import com.db.SessionVars;
import com.forms.SmartForm;

public class FormLogout extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2952266081093470853L;

	public FormLogout(com.forms.FormsMatrixDynamic fmd, SessionVars sVars) throws Exception {
		super(sVars, FormLogout.class.getCanonicalName());
		// see dispatchThis in Dispatch
	}
}
