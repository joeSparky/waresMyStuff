package comTest.forms;

import com.db.SessionVars;
import com.forms.SmartForm;

public class BlankForm extends SmartForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1957276428129604197L;

	public BlankForm(SessionVars sVars) throws Exception {
		super(sVars, BlankForm.class.getCanonicalName());
	}
}
