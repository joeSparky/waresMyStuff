package com.forms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.db.MyStatement;
import com.db.SessionVars;
import com.forms.SearchTarget.SEARCHTYPES;
import com.parts.security.InventoryDate;
import com.parts.security.PartLink;
import com.security.MyObject;

public class IdAndStrings extends ArrayList<IdAndString> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8594003232659591066L;

	public SearchOffset so = null;

	public enum OPERATORINPUT {
		NEXT, PREVIOUS, WANTSALIST, CANCELLIST
	}

	SessionVars sVars = null;

	public enum DIRECTION {
		UNKNOWN, FORWARD, REVERSE
	};

	/**
	 * the user is moving the search window. update the search offset when true.
	 */
	boolean moveSearch = false;

	// public for testing
//	public DISPLAYSTATE displayState = DISPLAYSTATE.SHOWGIVEMEALISTBUTTON;
	/**
	 * direction of the last submit key. NEXT -> forward, PREVIOUS -> reverse
	 */
	public DIRECTION direction = DIRECTION.FORWARD;

	/**
	 * The number of records to display with each request.
	 */
	public static int DISPLAYSIZE = 12;

	FormsMatrixDynamic fm = null;
	SEARCHTYPES searchType = null;

	public IdAndStrings(FormsMatrixDynamic fm, SEARCHTYPES s, SessionVars sVars) {
		this.fm = fm;
		this.sVars = sVars;
		this.searchType = s;
		clear();
	}

	public class MyVars extends com.forms.MyVars {
		public String CANCELLIST = IdAndStrings.class.getCanonicalName() + searchType.toString() + "a";
		public String SELECTFROMLIST = IdAndStrings.class.getCanonicalName() + searchType.toString() + "b";
		public String NEXT = IdAndStrings.class.getCanonicalName() + searchType.toString() + "c";
		public String PREVIOUS = IdAndStrings.class.getCanonicalName() + searchType.toString() + "d";
		public String REQUESTALIST = IdAndStrings.class.getCanonicalName() + searchType.toString() + "e";
		public String INVENTORYFROMLIST = IdAndStrings.class.getCanonicalName() + searchType.toString() + "f";
		public String SELECTFROMINVENTORYLIST = IdAndStrings.class.getCanonicalName() + searchType.toString() + "g";

		public MyVars(SessionVars sVars) throws Exception {
			super(sVars, IdAndStrings.class.getCanonicalName() + "-" + searchType.toString());
		}
//		public String getMyVarsName(SessionVars sVars) {
//			return super.getMyVarsName(sVars)+"_"+searchType.toString();
//		}
	}

	@Override
	public void clear() {
		moveSearch = false;
		direction = DIRECTION.FORWARD;
		if (so == null)
			so = new SearchOffset();
		else
			so.clear();
		super.clear();
	}

	public boolean contains(IdAndString idAndString) {
		for (int i = 0; i < size(); i++) {
			if (get(i).id == idAndString.id && get(i).string.equals(idAndString.string))
				return true;
		}
		return false;
	}

	public boolean contains(MyObject obj) {
		for (int i = 0; i < size(); i++) {
			if (get(i).id == obj.id && get(i).string.equals(obj.getInstanceName()))
				return true;
		}
		return false;
	}

	public IdAndStrings doQuery(boolean moveAfterSearch) throws Exception {
		super.clear();
		String myQuery = fm.get(fm.row).get(fm.column).getQuery(searchType, so.getOffset(direction, moveAfterSearch));
		if (myQuery.isEmpty())
			return this;
		Connection conn = null;
		MyStatement st = null;
		ResultSet rs = null;
		int recordsFound = 0;
		try {
			conn = sVars.connection.getConnection();
			st = new MyStatement(conn);
			rs = st.executeQuery(myQuery);
			IdAndString tmp;
			while (rs.next()) {
				tmp = new IdAndString();
				tmp.string = rs.getString("name");
				tmp.id = rs.getInt("id");
				add(tmp);
				recordsFound++;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();
		}
		so.storeLastSearchResults(direction, recordsFound);
		return this;
	}

	public void sortByCountsDelete(String searchString) {
		/**
		 * id of the object, count of the hits in name from searchString
		 */
		HashMap<Integer, Integer> idToCounts = new HashMap<Integer, Integer>();
		/**
		 * id of the object, name of the object, used to rebuid idToString after sorting
		 */
		HashMap<Integer, String> idToName = new HashMap<Integer, String>();
		for (String thisWord : searchString.split(" ")) {
			for (IdAndString idAndString : this) {
				if (idAndString.string.contains(thisWord)) {
					if (!idToName.containsKey(idAndString.id))
						idToName.put(idAndString.id, idAndString.string);
					if (!idToCounts.containsKey(idAndString.id))
						idToCounts.put(idAndString.id, 1);
					else
						idToCounts.put(idAndString.id, idToCounts.get(idAndString.id) + 1);
				}
			}
		}

		// build a list of ids for each count
		HashMap<Integer, ArrayList<Integer>> countsToIds = new HashMap<Integer, ArrayList<Integer>>();
		for (HashMap.Entry<Integer, Integer> entry : idToCounts.entrySet()) {
			// getValue from idToCounts is the count of ids. use as a key for countsToIds
			if (countsToIds.containsKey(entry.getValue())) {
				// append this id to the array of ids for this key
				countsToIds.get(entry.getValue()).add(entry.getKey());
			} else {
				// add a new array with this id to the hashmap
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ids.add(entry.getKey());
				countsToIds.put(entry.getValue(), ids);
			}
		}
		// rebuild
		clear();

		// unique counts
		Set<Integer> uniqueCounts = new HashSet<Integer>();
		for (int count : idToCounts.values()) {
			uniqueCounts.add(count);
		}

		// unique and sorted counts
		ArrayList<Integer> uniqueCountsSorted = new ArrayList<Integer>();
		uniqueCounts.forEach((count) -> {
			uniqueCountsSorted.add(count);
		});
		Collections.sort(uniqueCountsSorted);

		for (int countIndex = uniqueCountsSorted.size() - 1; countIndex >= 0; countIndex--) {
			int count = uniqueCountsSorted.get(countIndex);
			// for each id for this count
			for (int id : countsToIds.get(count)) {
				IdAndString tmp = new IdAndString();
				tmp.id = id;
				tmp.string = idToName.get(id);
				add(tmp);
			}
		}
	}

	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyVars myVars = (MyVars) new MyVars(sVars).get();
		if (sVars.hasParameterKey(myVars.CANCELLIST)) {
			processOperatorInput(OPERATORINPUT.CANCELLIST);
			throw new EndOfInputRedoQueries(ret);
		}
		if (sVars.hasParameterKey(myVars.NEXT + searchType)) {
			processOperatorInput(OPERATORINPUT.NEXT);
			throw new EndOfInputRedoQueries(ret);
		}
		if (sVars.hasParameterKey(myVars.PREVIOUS + searchType)) {
			processOperatorInput(OPERATORINPUT.PREVIOUS);
			throw new EndOfInputRedoQueries(ret);
		}
		if (sVars.hasParameterKey(myVars.REQUESTALIST)) {
			processOperatorInput(OPERATORINPUT.WANTSALIST);
			throw new EndOfInputRedoQueries(ret);
		}
		if (sVars.getParameterKeys().contains(myVars.SELECTFROMLIST)) {
			int id = -1;
			try {
				id = Integer.parseInt(sVars.getParameterValue(myVars.SELECTFROMLIST));
			} catch (Exception e) {
				ret.errorToUser("Please make a selection before clicking the Select button");
				throw new EndOfInputException(ret);
			}
			MyObject obj = fm.getObject();
			obj.find(id);
			fm.resetAllIdAndStrings();
			throw new EndOfInputRedoQueries(ret);
		}

		if (sVars.getParameterKeys().contains(myVars.SELECTFROMINVENTORYLIST)) {
			int id = -1;
			try {
				// get the id of the inventory link
				id = Integer.parseInt(sVars.getParameterValue(myVars.SELECTFROMINVENTORYLIST));
			} catch (Exception e) {
				ret.errorToUser("Please make a selection before clicking the Select button");
				throw new EndOfInputException(ret);
			}
			MyObject parent = fm.getObject().getNew();
			MyObject child = fm.getObjectBelowMeInRow().getNew();
			InventoryDate inventoryDate = new InventoryDate(parent, child, sVars);
			inventoryDate.find(id);
			inventoryDate.setInventoried(true);
			inventoryDate.update();
//			fm.resetAllIdAndStrings();
			throw new EndOfInputRedoQueries(ret);
		}

		return ret;
	}

	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyVars myVars = (MyVars) new MyVars(sVars).get();
//		if (fm.getObject().isLoaded())
//			return ret;
		// if the object does not have an inventory field
//		if (searchType == SearchTarget.SEARCHTYPES.INVENTORY && !fm.getObject().hasInventoryField())
//			return ret;
		if (searchType == SearchTarget.SEARCHTYPES.INVENTORYLINKS) {
			if (!fm.isObjectBelowMeInRow())
				return ret;
			if (!fm.getObject().hasInventoryLinkWith(fm.getObjectBelowMeInRow()))
				return ret;
		}
		doQuery(moveSearch);
		if (isEmpty()) {
			if (!fm.getObject().searchString.isEmpty()) {
				ret.rawText("Nothing found in " + SearchTarget.getIdAndStringLabels(searchType) + " list with "
						+ fm.getObject().searchString);
				ret.newLine();
			}
		} else {
			ret.startTable();
			ret.startRow();
			ret.startBold();
			ret.spanTextColumn(SearchTarget.getIdAndStringLabels(searchType) + " list", 2);
			ret.endBold();
			ret.endRow();
			ret.startRow();
			switch (searchType) {
			case ALL:
			case ANCESTORS:
			case DESCENDANTS:
				// allow room for the "no selection" option
				ret.startSingleSelection(myVars.SELECTFROMLIST, Math.min(DISPLAYSIZE + 1, this.size() + 1), false);
				break;
//			case INVENTORY:
//				ret.startSingleSelection(myVars.INVENTORYFROMLIST, Math.min(DISPLAYSIZE + 1, this.size() + 1), false);
//				break;
			case INVENTORYLINKS:
				ret.startSingleSelection(myVars.SELECTFROMINVENTORYLIST, Math.min(DISPLAYSIZE + 1, this.size() + 1),
						false);
				break;
			default:
				break;
			}
			ret.addNoSelectionOption();
			IdAndString tmp = new IdAndString();
			Iterator<IdAndString> itr = iterator();
			while (itr.hasNext()) {
				tmp = itr.next();
				ret.addSelectionOption("" + tmp.id, tmp.string);
			}
			ret.endSingleSelection();
			ret.endTable();
			switch (searchType) {
			case ALL:
			case ANCESTORS:
			case DESCENDANTS:
				ret.submitButton("Submit selected object from " + SearchTarget.getIdAndStringLabels(searchType),
						myVars.SELECTFROMLIST);
				break;
//			case INVENTORY:
//				ret.submitButton("Mark as inventoried " + searchType.toString(), myVars.INVENTORYFROMLIST);
//				break;
			case INVENTORYLINKS:
				if (fm.getObjectBelowMeInRow().isLoaded())
					ret.submitButton("Mark as inventoried " + searchType.toString(), myVars.SELECTFROMINVENTORYLIST);
				else
					ret.submitButton("Submit selected object from " + SearchTarget.getIdAndStringLabels(searchType),
							myVars.SELECTFROMINVENTORYLIST);
				break;
			}
			if (so.showNextButton())
				ret.addAll(nextButton(myVars));
			if (so.showPreviousButton())
				ret.addAll(previousButton(myVars));
		}
		return ret;
	}

	FormsArray nextButton(MyVars myVars) {
		FormsArray ret = new FormsArray();
		ret.submitButton("Next " + DISPLAYSIZE + " objects from " + SearchTarget.getIdAndStringLabels(searchType),
				myVars.NEXT + searchType);
		return ret;
	}

	FormsArray previousButton(MyVars myVars) {
		FormsArray ret = new FormsArray();
		ret.submitButton("Previous " + DISPLAYSIZE + " objects from " + SearchTarget.getIdAndStringLabels(searchType),
				myVars.PREVIOUS + searchType);
		return ret;
	}

	public void processOperatorInput(OPERATORINPUT input) {
		switch (input) {
		case CANCELLIST:
			moveSearch = false;
			break;
		case NEXT:
			// no NEXT button is offered to the user if the records have been exhausted
			direction = DIRECTION.FORWARD;
			moveSearch = true;
			break;
		case PREVIOUS:
			direction = DIRECTION.REVERSE;
			moveSearch = true;
			break;
		case WANTSALIST:
			moveSearch = false;
			break;
		}
	}
}
