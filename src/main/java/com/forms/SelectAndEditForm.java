package com.forms;

import com.db.SessionVars;
import com.errorLogging.Internals;
import com.parts.security.Dispatch;
import com.parts.security.Login;
import com.security.ExceptionCoding;
import com.security.MyObject;

import jakarta.servlet.annotation.WebServlet;

/**
 * drives the selection and editing forms using an array of MyObjects
 * 
 * @author Joe
 *
 */
@WebServlet("/selectandedit")
public class SelectAndEditForm extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7037700104070839409L;

	public SelectAndEditForm(SessionVars sVars, FormsMatrixDynamic fmd) throws Exception {
		super(sVars, SelectAndEditForm.class.getCanonicalName() + fmd.getRowColumnSuffix());
	}

	public static final String HIDDENFIELD = SelectAndEditForm.class.getCanonicalName() + "a";
//	public void resetForm(SessionVars sVars, FormsMatrixDynamic fmd) throws Exception {
//		for (fmd.row = 0; fmd.row < fmd.getNumberOfRows(); fmd.row++) {
//			for (fmd.column = 0; fmd.column < fmd.getRowSize(); fmd.column++) {
//				if (fmd.row == 0 && fmd.column == 1 && fmd.getRowSize() > 1)
//					fmd.getRow().get(fmd.column).objectSelectedLastTime = true;
//			}
//		}
//	}

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		sVars.fmd.clearAllObjectSelectedLastTime();
		FormsArray ret = new FormsArray();

		if (!sVars.hasParameterKey(HIDDENFIELD)) {
			ret.errorToUser("could not find hidden field " + HIDDENFIELD);
			return ret;
		}
		SearchTargetHtml rc = new SearchTargetHtml(sVars.getParameterValue(HIDDENFIELD));
		if (rc.sanityFails(sVars.fmd)) {
			ret.errorToUser(rc.errorString);
			return ret;
		}

		// move the focus
		sVars.fmd.row = rc.row;
		sVars.fmd.column = rc.column;
		sVars.fmd.direction = rc.direction;
		sVars.fmd.setObjectSelectedLastTime(true);
		ret.addAll(sVars.fmd.getSearchTarget().extractParams(sVars));
		return ret;
	}

	/**
	 * return true if there is a form after this one in the neighborForms list
	 * 
	 * @param i
	 * @return
	 */
	private boolean hasUpperForm(SearchTargets objs, int i) {
		return i < objs.size() - 1;
	}

	/**
	 * return true if this object (i) and the next object (i+1) in objs are selected
	 * 
	 * @param i
	 * @return
	 */
	private boolean bothSelected(SearchTargets objs, int i) {
		// if there is no next form
		if (!hasUpperForm(objs, i))
			return false;
		if (objs.get(i).obj.isLoaded() && objs.get(i + 1).obj.isLoaded())
			return true;
		return false;
	}

	// private boolean showThisForm(ArrayList<MyObjectsArray> objs, int outer,
	// int inner) {
	// return
	// // there is a form below this form
	// hasUpperForm(objs.get(outer), inner)
	// // and this form should be shown use the form below this one as a
	// // parameter
	// && objs.get(outer).get(inner)
	// .showThisScreen(objs.get(outer).get(inner + 1));
	// }

	int findMe(SearchTargets objs, MyObject me) throws Exception {
		for (int i = 0; i < objs.size(); i++) {
			if (me.equals(objs.get(i).obj))
				return i;
		}
		throw new ExceptionCoding("not found");
//		return 0;
	}

	/**
	 * my child and I are both selected.
	 * 
	 * @param objs
	 * @param me
	 * @return
	 * @throws Exception
	 */
	boolean meAndMyChildAreBothSelected(SearchTargets objs, MyObject me) throws Exception {
		return bothSelected(objs, findMe(objs, me));
	}

//	boolean alreadySet = false;

	/**
	 * 
	 * @param outer
	 * @param inner
	 * @param thisPass (assumed to be loaded)
	 * @return
	 * @throws Exception
	 */
	String makeTabName(FormsMatrixDynamic fmd) throws Exception {
		// create a tab name
		String tabName = fmd.getObject().getInstanceName();
		if (fmd.aboveMeIsParent())
			return makeBold(tabName);
		else
			return tabName;
	}

	String makeBold(String s) {
		return "<b>" + s + "</b>";
	}

	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		ret.setReturnTo(MYRETURNTO);
		TabbedDivs tds = new TabbedDivs(sVars, this);
		TabbedDiv td = new TabbedDiv(MYRETURNTO);
		// see if there are multiple objectSelectedLastTime flags set
		boolean clearObjsSelectedLastTime = false;
		try {
			sVars.fmd.oneAndOnlyOneObjectSelectedLastTime();
		} catch (ExceptionCoding e) {
			clearObjsSelectedLastTime = true;
		}
		if (clearObjsSelectedLastTime) {
			sVars.fmd.clearAllObjectSelectedLastTime();
			sVars.fmd.get(0).get(0).objectSelectedLastTime = true;
		}

//		Internals.logWithDate();
		for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
			for (sVars.fmd.column = 0; sVars.fmd.column < sVars.fmd.getRow().size(); sVars.fmd.column++) {
				if (sVars.fmd.getRow().get(sVars.fmd.column).editSelectType.equals(SearchTarget.EDITSELECTTYPE.NEITHER))
					continue;
				if (sVars.fmd.getObject().isLoaded()) {
					// put selected instance in the tab
					td = new TabbedDiv(makeTabName(sVars.fmd), false, sVars.fmd.getObjectSelectedLastTime(),
							MYRETURNTO);
				} else {
					// use the general name of the object for the tab
					td = new TabbedDiv(sVars.fmd.getObject().getLogicalName(), false,
							sVars.fmd.getObjectSelectedLastTime(), MYRETURNTO);
				}

				// get the forms for selecting and editing.
				switch (sVars.fmd.getEditSelectType()) {
				case EDITANDSELECT:
				case SELECT:
					for (FormsMatrixDynamic.PARTNER direction : FormsMatrixDynamic.PARTNER.values()) {
						sVars.fmd.direction = direction;
//					td.addForm(sVars.fmd.getSelector().getForm(sVars), HIDDENFIELD,
//							"outer_" + sVars.fmd.row + "_inner_" + sVars.fmd.column);
						String hmm = new SearchTargetHtml().toHtml(sVars.fmd.getSearchTarget(), sVars.fmd);
						td.addForm(sVars.fmd.getSearchTarget().getForm(sVars), HIDDENFIELD, hmm);
					}
					break;
				case NEITHER:
					break;
				case NOTINITIALIZED:
					throw new ExceptionCoding("editSelectType not initialized. outer: " + sVars.fmd.row + " inner: "
							+ sVars.fmd.column + " name:" + sVars.fmd.getObject().getInstanceName());
				}
				td.sanity();
				tds.add(td);
			}
			// start a new row of tabs
			tds.add(new TabbedDiv(MYRETURNTO));
		}

//		try {
		ret.addAll(tds.dumpDivsIntoForm());
//		} catch (Exception e) {
//			Internals.dumpExceptionExit(e);
//		}

		return ret;
	}

	/**
	 * the selected form has sent us a callback
	 * 
	 */
//	private FormsArray processSelectForm(SessionVars sVars, FormsMatrixDynamic fmd) throws Exception {
//		FormsArray ret = new FormsArray();
////		FilteredList st = formsMatrix.get(formsMatrix.thisRow).get(formsMatrix.thisColumn);
//		switch (sVars.fmd.status) {
//		case BACKTODISPATCH:
//			ret.status = FormsArray.STATUS.BACKTODISPATCH;
//			return ret;
//		case SELECTEDNEW:
////			sVars.fmd.getRow().get(sVars.fmd.column).selector.setNewState(INSTATE.DISABLED);
//			break;
//
//		case NOTINITIALIZED:
//			throw new ExceptionCoding("unknown status of " + sVars.fmd.status);
//		}
//		return ret;
//	}

//	@Override
//	public FormsArray callBack(SessionVars sVars, FormsMatrixDynamic fm) throws Exception {
//		checkRowColumn(sVars, fm);
//		return processSelectForm(sVars, fm);
////		}
//	}

	void checkRowColumn(SessionVars sVars, FormsMatrixDynamic fmd) throws Exception {
		String tmp = sVars.getParameterValue(HIDDENFIELD);
		if (tmp == null)
			throw new ExceptionCoding("hidden field not found");
		String[] split = tmp.split("_");
		int outer = Integer.parseInt(split[1]);
		int inner = Integer.parseInt(split[3]);
		if (sVars.fmd.row != outer)
			throw new Exception("outer:" + outer + " thisRow:" + sVars.fmd.row);
		if (sVars.fmd.column != inner)
			throw new Exception("inner:" + outer + " thisColumn:" + sVars.fmd.column);
	}

	int getColumn(SessionVars sVars) throws Exception {
		String tmp = sVars.getParameterValue(HIDDENFIELD);
		if (tmp == null)
			throw new ExceptionCoding("hidden field not found");
		String[] split = tmp.split("_");
		return Integer.parseInt(split[3]);
	}

	int getRow(SessionVars sVars) throws Exception {
		String tmp = sVars.getParameterValue(HIDDENFIELD);
		if (tmp == null)
			throw new ExceptionCoding("hidden field not found");
		String[] split = tmp.split("_");
		return Integer.parseInt(split[1]);
	}

	void checkDynamic(SessionVars sVars) throws Exception {
		int row = getRow(sVars);
		int column = getColumn(sVars);
		if (row > sVars.fmd.getNumberOfRows())
			throw new Exception("row of " + row);
		if (row < 0)
			throw new Exception("row of " + row);
		if (column > sVars.fmd.get(row).size())
			throw new Exception("column of " + column);
		if (column < 0)
			throw new Exception("column of " + column);
//		sVars.fmd.row = row;
//		sVars.fmd.column = column;
//		if (sVars.fmd.row != row)
//			throw new Exception("stored row of " + sVars.fmd.row + " received row of " + row);
//		if (sVars.fmd.column != column)
//			throw new Exception("stored column of " + sVars.fmd.column + " received column of " + column);
	}

	@Override
	public FormsArray processButtons(SessionVars sVars) {
		FormsArray ret = new FormsArray();

		if (ret.cancelClicked(sVars)) {
			// get the dispatch form
			try {
				ret.addAll(new Dispatch().getForm(sVars));
			} catch (Exception e) {
				ret.errorToUser(e);
			}
			ret.standardForm();
			return ret;
		}
//TODO redundant
		boolean checkFailed = false;
		try {
			checkDynamic(sVars);
		} catch (Exception e) {
			checkFailed = true;
			ret.errorToUser(e);
		}

		if (checkFailed) {
			try {
				ret.addAll(getForm(sVars));
			} catch (Exception e) {
				ret.errorToUser(e);
			}
			return ret;
		}
		boolean returnedWithoutException = false;
		FormsArray retSoFar = null;
		// process the request, report the errors to the user
		try {
			ret.addAll(extractParams(sVars));
			returnedWithoutException = true;
			// ret.errorToUser("input not found");
		} catch (EndOfInputException e) {
			// normal end of input processing
			retSoFar = new FormsArray();
			retSoFar = e.getForm();
		} catch (EndOfInputRedoQueries e) {
//			sVars.fmd.resetAllIdAndStrings();
			retSoFar = e.ret;
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				ret.rawText(ste.toString() + "<br>");
			ret.errorToUser(e);
		}

		boolean addAllFailed = false;

		if (retSoFar != null)
			try {
				ret.addAll(retSoFar);
			} catch (Exception e) {
				ret.errorToUser(e);
				addAllFailed = true;
			}
		if (addAllFailed) {
			try {
				ret.addAll(getForm(sVars));
			} catch (Exception e) {
				ret.errorToUser(e);
			}
			return ret;
		}

		// keep the unread returnedWithoutException from causing an
		// unused variable warning. Keep for breakpoint
		if (returnedWithoutException)
			returnedWithoutException = true;

		// if one of the methods that processed the request want to go back to the
		// dispatch screen
		if (ret.status == FormsArray.STATUS.BACKTODISPATCH) {
//			Internals.logWithDate();
			try {
				ret.addAll(sVars.getDispatch().getForm(sVars));
			} catch (Exception e) {
				ret.errorToUser(e);
			}
			ret.standardForm();
			return ret;
		}

		// get the form for the next screen
		try {
//			Internals.logWithDate();
			ret.addAll(getForm(sVars));
//			Internals.logWithDate();
		} catch (Exception e) {
			ret.errorToUser(e);
		}

		// if getForm didn't get far enough to set RETURNTO
		if (ret.returnToString == null) {
//			Internals.logWithDate();
			SmartForm login = null;
			try {
				login = new Login(sVars);
				sVars.backToOrigin("starting over");
				// go back to the login form
				ret.addAll(login.getForm(sVars));
			} catch (Exception e) {
				// if we can't get a login form, we're broken.
				// try to log the error
				Internals.logStartupError(e);
			}
			// no standard form
			return ret;
		}
		// add the standard ending to the form generated by extractParams
		ret.standardForm();

		try {
			ret.validInternalForm();
		} catch (Exception e) {
			ret.errorToUser(e);
		}
//		Internals.logWithDate();
		return ret;
	}

	// must match @WebServlet
	private static String MYRETURNTO = "selectandedit";

//	 from HttpServlet
	public SelectAndEditForm() throws Exception {
		super(null, SelectAndEditForm.class.getCanonicalName());
	}

	@Override
	public void init() {
	}
}