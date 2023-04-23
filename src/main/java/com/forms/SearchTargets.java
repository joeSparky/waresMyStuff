package com.forms;

import java.util.ArrayList;

import com.db.SessionVars;
import com.security.MyObject;
import com.security.MyObjectsArray;

/**
 * A row of tabs with an individual tab being a SearchTarget.
 * 
 * @author joe
 *
 */
public class SearchTargets extends ArrayList<SearchTarget> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1297625850611117138L;
	protected SessionVars sVars = null;

	public SearchTargets(SessionVars sVars) {
		this.sVars = sVars;
	}

	/**
	 * find me in this, return the index or -1 if not found
	 * 
	 * @param me
	 * @return
	 * @throws Exception
	 */
	public int findIndexOfObject(MyObject me) throws Exception {
		for (int i = 0; i < size(); i++)
			if (get(i).obj.getMyFileName().equals(me.getMyFileName()))
				return i;
		return -1;
	}


//	/**
//	 * initialize the SearchTargets array. Return the index of the lowest selected
//	 * object.
//	 * 
//	 * @param myResults
//	 * @return
//	 */
//	public void initResults() {
//
//		// for each object in MyObjectsArray
//		for (int myIndex = 0; myIndex < size(); myIndex++) {
//			get(myIndex).clear();
//		}
//	}

	
	/**
	 * find the first loaded object above myLevel
	 * 
	 * @param myLevel
	 * @return
	 */
	public int getLowestLoadedIndexAbove(int myLevel) {
		if (myLevel == 0)
			return -1;
		for (int myIndex = myLevel - 1; myIndex >= 0; myIndex--) {
			if (get(myIndex).obj.isLoaded())
				return myIndex;
		}
		return -1;
	}

	/**
	 * find the first loaded object below myLevel
	 * 
	 * @param myLevel
	 * @return
	 */
	public int getHighestLoadedIndexBelow(int myLevel) {
		if (myLevel >= size() - 1)
			return -1;
		for (int myIndex = myLevel + 1; myIndex < size(); myIndex++) {
			if (get(myIndex).obj.isLoaded())
				return myIndex;
		}
		return -1;
	}

	public MyObjectsArray getObjects() {
		MyObjectsArray objs = new MyObjectsArray();
		for (int i = 0; i < size(); i++)
			objs.add(get(i).obj);
		return objs;
	}

	// legacy adds
	public void add(MyObject obj) throws Exception {
		add(obj, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
	}

	public void add(MyObject obj, SearchTarget.EDITSELECTTYPE editSelectType) throws Exception {
		SearchTarget st = new SearchTarget(obj, sVars);
		st.obj = obj;
		st.editSelectType = editSelectType;
		st.column = this.size();
		this.add(st);
		
	}
}
