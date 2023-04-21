package com.forms;

import com.db.SessionVars;
import com.security.ExceptionCoding;

public class LeftAndRight {
	public SearchTarget left = null;
	public SearchTarget right = null;

	public LeftAndRight(SessionVars sVars) throws ExceptionCoding {
		switch (sVars.fmd.direction) {
		case PARTNERBOTHSIDES:
		case NONE:
		default:
			break;
		case PARTNERTOTHELEFT:
			left = sVars.fmd.getToMyLeft();
			right = sVars.fmd.getSearchTarget();
			break;
		case PARTNERTOTHERIGHT:
			left = sVars.fmd.getSearchTarget();
			right = sVars.fmd.getToMyRight();
			break;
		}
	}
}
