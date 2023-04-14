package com.parts.forms;

import com.db.SessionVars;
import com.forms.AttachToFormPair;
import com.forms.EndOfInputException;
import com.forms.FormsArray;
import com.parts.location.Location;

public class AttachToFormLocationLocation extends AttachToFormPair {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1860484218986108292L;
	String MOVECONTENTSBUTTON = AttachToFormLocationLocation.class.getCanonicalName() + "a";
	String MOVETONEWPARENTBUTTON = AttachToFormLocationLocation.class.getCanonicalName() + "b";
//	Location leftLocation = null;
//	Location rightLocation = null;

	public AttachToFormLocationLocation(SessionVars sVars) throws Exception {
		super(sVars);
	}

	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();

		LeftAndRight leftAndRight = new LeftAndRight(sVars);
		if (leftAndRight.left.obj.isLoaded() && leftAndRight.right.obj.isLoaded()) {
			ret.submitButton("Move the contents of '" + leftAndRight.left.obj.getInstanceName() + "' to '"
					+ leftAndRight.right.obj.getInstanceName() + "'", MOVECONTENTSBUTTON);

			ret.submitButton("Make '" + leftAndRight.left.obj.getInstanceName() + "' a child of '"
					+ leftAndRight.right.obj.getInstanceName() + "'", MOVETONEWPARENTBUTTON);
		}
		return ret;
	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		LeftAndRight leftAndRight = new LeftAndRight(sVars);
		Location leftLocation = (Location)leftAndRight.left.obj;
		Location rightLocation = (Location)leftAndRight.right.obj;
		if (sVars.hasParameterKey(MOVECONTENTSBUTTON)) {
			leftLocation.moveContents(rightLocation);
			throw new EndOfInputException(ret);
		}

		if (sVars.hasParameterKey(MOVETONEWPARENTBUTTON)) {
			leftLocation.moveToNewParentRecursive(rightLocation);
			throw new EndOfInputException(ret);
		}
		return ret;
	}
}
