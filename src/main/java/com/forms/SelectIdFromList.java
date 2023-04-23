package com.forms;

import com.db.SessionVars;
import com.security.MyObject;

public class SelectIdFromList extends SmartForm {
//	private static final int KEYWORDLENGTH = 50;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2940092260639122087L;

	/**
	 * select from a list using keywords and a list
	 * 
	 * @param master
	 * @param sVars
	 * @param keywordsLabel
	 * @param overallLabel
	 * @param buttons
	 * @throws Exception
	 */
	public SelectIdFromList(FormsMatrixDynamic fmd, SessionVars sVars) throws Exception {
		super(sVars, SelectIdFromList.class.getCanonicalName() + "-" + fmd.row + "-" + fmd.column);
	}

	static final String SEARCH = SelectIdFromList.class.getCanonicalName() + "a";

	class MyVars extends com.forms.MyVars {

		FormsMatrixDynamic fmd = null;

		protected MyVars(SessionVars sVars) throws Exception {
			super(sVars, SelectIdFromList.class.getCanonicalName());
		}

	}
//	class MyVars {
//		
//
//		MyVars(FormsMatrixDynamic fmd) {
//			this.fmd = fmd;
//		}
//	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		MyVars myVars = (MyVars) new MyVars(sVars).get();
		MyObject obj = myVars.fmd.getObject();
		if (sVars.hasParameterKey(SEARCH) && (!sVars.getParameterValue(SEARCH).equals(obj.searchString))) {
			obj.searchString = sVars.getParameterValue(SEARCH).toLowerCase();
			throw new EndOfInputRedoQueries(new FormsArray());
		}
		return myVars.fmd.getRow().get(myVars.fmd.column).extractParams(sVars);
	}

	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		ret.addAll(sVars.fmd.getRow().get(sVars.fmd.column).getForm(sVars));
		return ret;
	}
}
