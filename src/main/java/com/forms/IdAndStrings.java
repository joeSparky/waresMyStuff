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
import com.security.MyObject;

public class IdAndStrings extends ArrayList<IdAndString> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8594003232659591066L;

//	public SearchOffset so = null;

//	public enum OPERATORINPUT {
//		NEXT, PREVIOUS, WANTSALIST, CANCELLIST
//	}

	SessionVars sVars = null;

//	public enum DIRECTION {
//		UNKNOWN, FORWARD, REVERSE
//	};

	/**
	 * the user is moving the search window. update the search offset when true.
	 */
//	boolean moveSearch = false;

	// public for testing
//	public DISPLAYSTATE displayState = DISPLAYSTATE.SHOWGIVEMEALISTBUTTON;
	/**
	 * direction of the last submit key. NEXT -> forward, PREVIOUS -> reverse
	 */
//	public DIRECTION direction = DIRECTION.FORWARD;

	/**
	 * The number of records to display with each request.
	 */
	public static int DISPLAYSIZE = 12;

	FormsMatrixDynamic fm = null;
	SEARCHTYPES searchType = null;

	public IdAndStrings(FormsMatrixDynamic fm, SessionVars sVars, SEARCHTYPES s) {
		this.fm = fm;
		this.sVars = sVars;
		this.searchType = s;
		clear();
	}

	enum MYBUTTONS {
		/**
		 * the id returned by the browser is the id of an object.
		 */
		IDISANOBJECT,
		/**
		 * the id returned by the browser is the id of a link between two objects.
		 */
		IDISALINK
	};

	String myButtonString(MYBUTTONS myButton, SearchTarget.SEARCHTYPES searchType) {
		return IdAndStrings.class.getCanonicalName() + searchType.toString() + myButton.ordinal();
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

	public IdAndStrings doQuery(
//			SearchTarget.SEARCHTYPES searchType
			) throws Exception {
		super.clear();
		String myQuery = fm.get(fm.row).get(fm.column).getQuery(searchType);
		if (myQuery.isEmpty())
			return this;
		Connection conn = null;
		MyStatement st = null;
		ResultSet rs = null;
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
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();
		}
//		so.storeLastSearchResults(recordsFound);
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
		if (sVars.getParameterKeys().contains(myButtonString(MYBUTTONS.IDISANOBJECT, searchType))) {
			int id = -1;
			try {
				id = Integer.parseInt(sVars.getParameterValue(myButtonString(MYBUTTONS.IDISANOBJECT, searchType)));
			} catch (Exception e) {
				ret.errorToUser("Please make a selection before clicking the Select button");
				throw new EndOfInputException(ret);
			}
			MyObject obj = fm.getObject();
			obj.find(id);
			throw new EndOfInputRedoQueries(ret);
		}

		if (sVars.getParameterKeys().contains(myButtonString(MYBUTTONS.IDISALINK, searchType))) {
			int id = -1;
			try {
				// get the id of the inventory link
				id = Integer.parseInt(sVars.getParameterValue(myButtonString(MYBUTTONS.IDISALINK, searchType)));
			} catch (Exception e) {
				ret.errorToUser("Please make a selection before clicking the Select button");
				throw new EndOfInputException(ret);
			}
			// we have the id of the link, but not which set of links the id belongs to.
			MyObject parent = fm.getObject().getNew();
			MyObject child = fm.getObjectBelowMeInRow().getNew();
			InventoryDate inventoryDate = new InventoryDate(parent, child, sVars);
			inventoryDate.find(id);
			inventoryDate.setInventoried(true);
			inventoryDate.update();
			throw new EndOfInputRedoQueries(ret);
		}

		return ret;
	}

	public FormsArray getForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
//		MyVars myVars = (MyVars) new MyVars(sVars).get();
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
//		addAll(doQuery(searchType));
		doQuery(
//				searchType
				);
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
			ret.rawText(setTopOfList(searchType));
			ret.endBold();
			ret.endRow();
			ret.startRow();
			switch (searchType) {
			case ALL:
			case ANCESTORS:
			case DESCENDANTS:
			case MYDESCENDANTS:
			case ORPHANS:
				// allow room for the "no selection" option
				ret.startSingleSelection(myButtonString(MYBUTTONS.IDISANOBJECT, searchType),
						Math.min(DISPLAYSIZE + 1, this.size() + 1), false);
				break;
			case INVENTORYLINKS:
				ret.startSingleSelection(myButtonString(MYBUTTONS.IDISALINK, searchType),
						Math.min(DISPLAYSIZE + 1, this.size() + 1), false);
				break;
			}

			ret.addNoSelectionOption(setTopOfList(searchType));
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
			case MYDESCENDANTS:
			case ORPHANS:
				ret.submitButton("Submit selected object from " + SearchTarget.getIdAndStringLabels(searchType),
						myButtonString(MYBUTTONS.IDISANOBJECT, searchType));
				break;
			case INVENTORYLINKS:
				if (fm.getObjectBelowMeInRow().isLoaded())
					ret.submitButton("Mark as inventoried " + searchType.toString(),
							myButtonString(MYBUTTONS.IDISALINK, searchType));
				else
					ret.submitButton("Submit selected object from " + SearchTarget.getIdAndStringLabels(searchType),
							myButtonString(MYBUTTONS.IDISALINK, searchType));
				break;
			}
		}
		return ret;
	}

	String setTopOfList(SEARCHTYPES searchType) {
		switch (searchType) {
		case ALL:
		case ANCESTORS:
		case INVENTORYLINKS:
		case ORPHANS:
			return SearchTarget.getIdAndStringLabels(searchType);
		case DESCENDANTS:
		case MYDESCENDANTS:
			return ("Contents of " + fm.getObject().getInstanceName());
		}
		return "";
	}

//	FormsArray nextButton(SearchTarget.SEARCHTYPES searchType) {
//		FormsArray ret = new FormsArray();
//		ret.submitButton("Next " + DISPLAYSIZE + " objects from " + SearchTarget.getIdAndStringLabels(searchType),
//				myButtonString(MYBUTTONS.NEXT, searchType));
//		return ret;
//	}

//	FormsArray previousButton(SearchTarget.SEARCHTYPES searchType) {
//		FormsArray ret = new FormsArray();
//		ret.submitButton("Previous " + DISPLAYSIZE + " objects from " + SearchTarget.getIdAndStringLabels(searchType),
//				myButtonString(MYBUTTONS.PREVIOUS, searchType));
//		return ret;
//	}

//	public void processOperatorInput(OPERATORINPUT input) {
//		switch (input) {
//		case CANCELLIST:
//			moveSearch = false;
//			break;
//		case NEXT:
//			// no NEXT button is offered to the user if the records have been exhausted
//			direction = DIRECTION.FORWARD;
//			moveSearch = true;
//			break;
//		case PREVIOUS:
//			direction = DIRECTION.REVERSE;
//			moveSearch = true;
//			break;
//		case WANTSALIST:
//			moveSearch = false;
//			break;
//		}
//	}
}
