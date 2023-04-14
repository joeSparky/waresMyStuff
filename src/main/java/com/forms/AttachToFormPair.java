package com.forms;

import com.db.SessionVars;
import com.security.ExceptionCoding;

/**
 * create and manage forms that interact with the SearchTargets to the left and
 * right of the current target in FormsMatrixDynamic. For example, if the
 * SearchTarget to the left of the current target is loaded but there isn't a
 * link to it, offer the user the ability to create a link.
 * 
 * @author joe
 *
 */
public class AttachToFormPair extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2279444124310834216L;
	static final String buttonLinkChild = AttachToFormPair.class.getCanonicalName() + "a";
	String buttonUnlinkChild = AttachToFormPair.class.getCanonicalName() + "b";


	/**
	 * work with the object at row, column and at row, column+1
	 * 
	 * @param fmd
	 * @throws Exception
	 */
	public AttachToFormPair(SessionVars sVars) throws Exception {
		super(sVars, AttachToFormPair.class.getCanonicalName());
	}

	public FormsArray getForm(SessionVars sVars) throws Exception {
		if (forMe(sVars))
			return getMyForms(sVars, new LeftAndRight(sVars));
		else
			return new FormsArray();
	}

	FormsArray getMyForms(SessionVars sVars, LeftAndRight leftAndRight) throws Exception {
		FormsArray ret = new FormsArray();
		if (leftAndRight.left.obj.isLoaded() && leftAndRight.right.obj.isLoaded()
				&& leftAndRight.left.editSelectType == SearchTarget.EDITSELECTTYPE.EDITANDSELECT
				&& leftAndRight.right.editSelectType == SearchTarget.EDITSELECTTYPE.EDITANDSELECT)
			if (leftAndRight.left.obj.linkToChildExists(leftAndRight.right.obj)) {
				ret.addAll(removeLink(sVars, leftAndRight));
				ret.addAll(markAsInventoried(sVars, leftAndRight));
			} else
				ret.addAll(addLink(sVars, leftAndRight));
		return ret;
	}

	public FormsArray removeLink(SessionVars sVars, LeftAndRight leftAndRight) throws Exception {
		FormsArray ret = new FormsArray();
		ret.submitButton(
				"Remove link between " + leftAndRight.left.obj.getLogicalName() + ":"
						+ leftAndRight.left.obj.getInstanceName() + " and " + leftAndRight.right.obj.getLogicalName()
						+ ":" + leftAndRight.right.obj.getInstanceName(),
				buttonUnlinkChild + "_" + leftAndRight.left.obj.id + "_" + leftAndRight.right.obj.id);

		// let the user know what would happen if he chose the above option
		try {
			leftAndRight.left.obj.deleteTest(leftAndRight.right.obj);
		} catch (Exception e) {
			// let the user know
			ret.rawText(e.getLocalizedMessage());
			ret.newLine();
		}
		return ret;
	}

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

	/**
	 * allow parts to override
	 * 
	 * @param upper
	 * @param lower
	 * @return
	 * @throws Exception
	 */
	public FormsArray markAsInventoried(SessionVars sVars, LeftAndRight leftAndRight) throws Exception {
		return new FormsArray();
	}

	// subclass need sVars
	public FormsArray addLink(SessionVars sVars, LeftAndRight leftAndRight) throws Exception {
		FormsArray ret = new FormsArray();
		ret.submitButton(
				"Create link between " + leftAndRight.left.obj.getLogicalName() + ":"
						+ leftAndRight.left.obj.getInstanceName() + " and " + leftAndRight.right.obj.getLogicalName()
						+ ":" + leftAndRight.right.obj.getInstanceName(),
				buttonLinkChild + "_" + leftAndRight.left.obj.id + "_" + leftAndRight.right.obj.id);

		return ret;
	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		if (forMe(sVars)) {
			
			return eachSide(sVars);}
		else
			return new FormsArray();
	}

	FormsArray eachSide(SessionVars sVars) throws Exception {

		FormsArray ret = new FormsArray();
		LeftAndRight leftAndRight = new LeftAndRight(sVars);
		if (sVars.hasParameterKey(buttonLinkChild + "_" + leftAndRight.left.obj.id + "_" + leftAndRight.right.obj.id)) {
			leftAndRight.left.obj.addChild(leftAndRight.right.obj);
			sVars.fmd.resetAllIdAndStrings();
			throw new EndOfInputRedoQueries(ret);
		}

		if (sVars.hasParameterKey(
				buttonUnlinkChild + "_" + leftAndRight.left.obj.id + "_" + leftAndRight.right.obj.id)) {
			leftAndRight.left.obj.deleteLinkUnconditionally(leftAndRight.right.obj);
			sVars.fmd.resetAllIdAndStrings();
			throw new EndOfInputRedoQueries(ret);
		}
		return ret;
	}

	static final String MYNAME = AttachToFormPair.class.getCanonicalName();

	boolean forMe(SessionVars sVars) {
		switch (sVars.fmd.direction) {
		case PARTNERBOTHSIDES:
		case NONE:
		default:
			return false;
		case PARTNERTOTHELEFT:
		case PARTNERTOTHERIGHT:
			return true;
		}
	}
}
