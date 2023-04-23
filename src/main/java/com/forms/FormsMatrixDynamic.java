package com.forms;

import java.util.ArrayList;

import com.db.SessionVars;
import com.security.ExceptionCoding;
import com.security.MyLinkObject;
import com.security.MyObject;

public class FormsMatrixDynamic extends ArrayList<SearchTargets> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -525470898913706628L;
	SessionVars sVars = null;

	public FormsMatrixDynamic(SessionVars sVars) throws Exception {
		super();
		this.sVars = sVars;
	}

	public enum PARTNER {
//		NOT_INITIALIZED,
		/**
		 * single SearchTarget in the form
		 */
		NONE,
		/**
		 * the form uses a pair of SearchTargets, the neighbor to the left and the focus
		 * SearchTarget.
		 */
		PARTNERTOTHELEFT,
		/**
		 * the form uses a pair of SearchTargets, the focus SearchTarget and the
		 * neighbor to the right
		 */
		PARTNERTOTHERIGHT,
		/**
		 * consider the focus SearchTarget and the SearchTargets on either side
		 */
		PARTNERBOTHSIDES
	}

	/**
	 * evaluate the direction while getting a form or extracting a parameter
	 */
	public PARTNER direction = PARTNER.NONE;

	// force an out of range error if not initialized
	/**
	 * the index of the row in FormsMatrix
	 */
	public int row = -1;
	/**
	 * the index of the column within the row in FormsMatrix
	 */
	public int column = -1;
	public boolean objectSelectedLastTime = false;
	public FormsArray.STATUS status = FormsArray.STATUS.NOTINITIALIZED;

	public boolean add(SearchTargets targets) {
		for (SearchTarget st : targets) {
			st.row = size();
		}
		boolean returnFlag = super.add(targets);
		row = size() - 1;
		for (int myColumn = 0; myColumn < targets.size(); myColumn++) {
			SearchTarget tmpTarget = targets.get(myColumn);
			tmpTarget.fmd = this;
//			tmpTarget.myColumn = myColumn;
//			tmpTarget.myRow = row;
//			for (SEARCHTYPES s : SEARCHTYPES.values())
//				tmpTarget.idAndStrings.get(s).fm = this;
		}
		return returnFlag;
	}

	public MyObject getObject() {
		return get(row).get(column).obj;
	}

	public MyObject getPreviousObjectInRow() throws Exception {
		if (column < 1)
			throw new Exception("no previous object in row");
		return get(row).get(column - 1).obj;
	}

	public int getRowSize() {
		return getRow().size();
	}

	public SearchTargets getRow() {
		return get(row);
	}

	public SearchTarget getSearchTarget() {
		return getRow().get(column);
	}

	public void setObjectSelectedLastTime(boolean selected) {
		getRow().get(column).objectSelectedLastTime = selected;
	}

	public void clearAllObjectSelectedLastTime() {
		for (int myRow = 0; myRow < size(); myRow++) {
			SearchTargets sts = get(myRow);
			for (int myColumn = 0; myColumn < sts.size(); myColumn++) {
				sts.get(myColumn).objectSelectedLastTime = false;
			}
		}
	}

//	public SmartForm getSelector() {
//		return getRow().get(column).selector;
//	}

	public MyObject getObjectAboveMeInRow() throws Exception {
		if (column < 1)
			throw new Exception("no object above me");
		return getRow().get(column - 1).obj;
	}

	public MyObject getObjectBelowMeInRow() throws Exception {
		if (column > get(row).size() - 1)
			throw new Exception("no object below me");
		return get(row).get(column + 1).obj;
	}

	public boolean isObjectBelowMeInRow() {
		return column < getRow().size() - 1;
	}

	public boolean isObjectAboveMeInRow() {
		return column > 0;
	}

	public int getNumberOfRows() {
		return size();
	}

	public boolean getObjectSelectedLastTime() {
		return getRow().get(column).objectSelectedLastTime;
	}

	public SearchTarget.EDITSELECTTYPE getEditSelectType() {
		return get(row).get(column).editSelectType;
	}

//	public SearchTargets get(int row) {
//		return get(row);
//	}

	public void oneAndOnlyOneObjectSelectedLastTime() throws ExceptionCoding {
		boolean foundOne = false;
		MyObject obj = null;
		for (int outer = 0; outer < size(); outer++)
			for (int inner = 0; inner < get(outer).size(); inner++) {
				if (get(outer).get(inner).objectSelectedLastTime) {
					// if this is the first objectSelectedLastTime flags we've seen
					if (!foundOne) {
						foundOne = true;
						obj = get(outer).get(inner).obj;
					} else {
						throw new ExceptionCoding(
								"multiple objectSelectedLastTime: '" + get(outer).get(inner).obj.getInstanceName()
										+ "' and '" + obj.getInstanceName() + "'");
					}
				}
			}
		if (!foundOne)
			throw new ExceptionCoding("no objectSelectedLastTime was found");
	}

	/**
	 * return true if there's a tab above me and it's my parent
	 * 
	 * @return
	 * @throws Exception
	 * @throws ExceptionCoding
	 */
	public boolean aboveMeIsParent() throws ExceptionCoding, Exception {
		// if i'm the leftmost tab
		if (column < 1)
			return false;
		return new MyLinkObject(get(row).get(column - 1).obj, getObject(), sVars).isParentChild();
//		return false;
	}

//	String NAMEBOX;

//	public FormsArray editOrAddForm() throws Exception {
//		FormsArray ret = new FormsArray();
//		ret.newLine();
//		ret.startTable();
//		MyObject obj = getObject();
//		if (obj.isLoaded())
//			ret.rawText("Edit the name of " + obj.getLogicalName());
//		else
//			ret.rawText("Add a new " + obj.getLogicalName());
//		NAMEBOX = Utils.getNextString();
//		ret.addAll(obj.getNameForm(NAMEBOX, obj.isLoaded()));
//		ret.endTable();
//		return ret;
//	}

	String SEARCHBOX;

	public FormsArray getSearchForm() throws Exception {
		FormsArray ret = new FormsArray();
		ret.newLine();
		ret.startRow();
		SEARCHBOX = Utils.getNextString();
		ret.textBox(SEARCHBOX, SearchTarget.KEYWORDLENGTH, "Search for a " + getObject().getLogicalName(),
				getObject().searchString, false, false);
		ret.endRow();
		return ret;
	}

	public FormsArray extractSearchFormParams(SessionVars sVars, SmartForm callBackVar)
			throws EndOfInputRedoQueries, Exception {
		FormsArray ret = new FormsArray();
		if (sVars.hasParameterKey(SEARCHBOX)
				&& (!sVars.getParameterValue(SEARCHBOX).equals(getObject().searchString))) {
			getObject().searchString = sVars.getParameterValue(SEARCHBOX).toLowerCase();
			throw new EndOfInputRedoQueries(ret);
		}
		return ret;
	}

//	public FormsArray extractEditAddFormParams(SessionVars sVars) throws Exception {
//		FormsArray ret = new FormsArray();
//		MyObject obj = getObject();
//
//		if (getObject().nameChanged(sVars, NAMEBOX)) {
//			obj.extractName(sVars, NAMEBOX);
//			MyObject tmp = obj.getNew();
//			boolean nameFound = false;
//			try {
//				tmp.find(obj.getInstanceName());
//				nameFound = true;
//			} catch (Exception e) {
//			}
//			if (nameFound) {
//				ret.errorToUser(obj.getInstanceName() + " already exists.");
//				// clear the name from the instance
//				obj.clear();
//				throw new EndOfInputException(ret);
//			}
//			ret.addAll(obj.doSanityUpdateAddTryAgain(this));
//			throw new EndOfInputException(ret);
//		}
//		return ret;
//	}

	public RowColumn findInstanceOf(MyObject myObject) throws ExceptionCoding {
//		myObject.getClass()
		RowColumn rc = new RowColumn();
		for (rc.row = 0; rc.row < size(); rc.row++) {
			for (rc.column = 0; rc.column < get(rc.row).size(); rc.column++) {
				if (get(rc.row).get(rc.column).obj.getLogicalName().equals(myObject.getLogicalName())) {
					return rc;
				}
			}
		}
		throw new ExceptionCoding(myObject.getLogicalName() + " not found in " + this.getClass().getCanonicalName());
	}

	/**
	 * are all of the SearchTargets to the left of the current target (inclusive)
	 * linked and loaded with their neighbors to the left
	 * 
	 * @return
	 * @throws Exception
	 * @throws ExceptionCoding
	 */
	public boolean isLinkedAndLoadedToTheTop() throws ExceptionCoding, Exception {
		// if the current searchTarget has no neighbor to the left
		// or the guy at the top is not loaded
		if (column == 0 || !get(row).get(0).obj.isLoaded())
			return false;
		for (int i = 0; i < column; i++) {
			if (!get(row).get(i + 1).obj.isLoaded()
					|| new MyLinkObject(get(row).get(i).obj, get(row).get(i + 1).obj, sVars).linkExists())
				return false;
		}
		return true;
	}

	public boolean isSomethingToMyRight() {
		return column + 1 < get(row).size();
	}

	public boolean isSomethingToMyLeft() {
		return column > 0;
	}

	public SearchTarget getToMyLeft() throws ExceptionCoding {
		if (column == 0)
			throw new ExceptionCoding("nothing to the left");
		return get(row).get(column - 1);
	}

	public SearchTarget getToMyRight() throws ExceptionCoding {
		if (column + 1 > get(row).size())
			throw new ExceptionCoding("nothing to the right");
		return get(row).get(column + 1);
	}

	public boolean isTheFocus(SearchTarget st) {
		return (st.row == row && st.column == column);
	}

	/**
	 * for create a unique string to separate myVars in the session
	 * @return
	 */
	public String getRowColumnSuffix() {
		return "-" + row + "-" + column;
	}
}
