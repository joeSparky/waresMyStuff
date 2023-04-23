package comTest.security;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.FormsMatrixDynamic;
import com.forms.SearchTarget;
import com.forms.SearchTarget.SEARCHTYPES;
import com.forms.SearchTargets;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.security.MyObject;
import comTest.utilities.Utilities;

public class FilteredListTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);

		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * used to build setDescendantsQuery
	 */
	@Test
	public void testSetDescendantsQuery() {
		// create a stack with links between the layers
		Level1 levelOne = null;
		Level2 levelTwo = null;
		Level3 levelThree = null;
		int levelThreeId = -1;
		try {
			levelOne = new Level1(sVars);
			levelOne.add();
			levelTwo = new Level2(sVars);
			levelTwo.add();
			levelThree = new Level3(sVars);
			levelThree.add();
			levelThreeId = levelThree.id;
			// create all combinations of links between the layers
			levelOne.addChild(levelTwo);
			levelTwo.addChild(levelThree);
			levelThree.clear();
			// exercise the search string
			levelThree.searchString = "dkkasd fjasd eve";
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		FormsMatrixDynamic fmd = null;
		SearchTargets objs = new SearchTargets(sVars);
//		objs.initResults();
		try {
			fmd = new FormsMatrixDynamic(sVars);
			fmd.row = 0;
			fmd.column = 1;
			objs.add(levelOne, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(levelTwo, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(levelThree, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			objs.get(0).setDescendantsQuery();
			// get the ancestors of levelThree into levelOne
			levelOne.clear();
			// let levelTwo be an ancestor of levelThree
			levelTwo.clear();
			levelTwo.searchString = "dkkasd fjasd eve";
			// load up levelThree again so levelTwo can be its ancestor
			levelThree.find(levelThreeId);
			objs.get(0).setAncestorsQuery();
			objs.get(0).setDescendantsQuery();
			objs.get(0).setInventoryLinkQuery();
//			objs.get(0).setInventoryQuery(0);
		} catch (Exception e) {
			for (StackTraceElement s : e.getStackTrace()) {
				System.out.println(s);
			}
			fail(e.getLocalizedMessage());
		}
	}

	// part and location
	@Test
	public void InventoryQueryTest() {
		Part part = null;
		Location location = null;
		FormsMatrixDynamic fmd = null;
		SearchTargets objs = new SearchTargets(sVars);
//		objs.initResults();
		try {
			part = new Part(sVars);
			location = new Location(sVars);
			fmd = new FormsMatrixDynamic(sVars);
			fmd.row = 0;
			fmd.column = 0;
			objs.add(part, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(location, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			String str = objs.get(0).setInventoryLinkQuery();
			System.out.println(str);
//			partLink = new PartLink(part, location, sVars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// unselected part and selectedlocation
	@Test
	public void InventoryQueryTestUnselectedPartSelectedLocation() {
		Part part = null;
		Location location = null;
		FormsMatrixDynamic fmd = null;
		SearchTargets objs = new SearchTargets(sVars);
//		objs.initResults();
		try {
			part = new Part(sVars);
			location = new Location(sVars);
			location.id = 2;
			fmd = new FormsMatrixDynamic(sVars);
			fmd.row = 0;
			fmd.column = 0;
			objs.add(part, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(location, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			String str = objs.get(0).setInventoryLinkQuery();
			System.out.println(str);
//				partLink = new PartLink(part, location, sVars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// selected part and unselected location
	@Test
	public void InventoryQueryTestSelectedPartUnselectedLocation() {
		Part part = null;
		Location location = null;
		FormsMatrixDynamic fmd = null;
		SearchTargets objs = new SearchTargets(sVars);
//		objs.initResults();
		try {
			part = new Part(sVars);
			part.id = 31;
			location = new Location(sVars);
			fmd = new FormsMatrixDynamic(sVars);
			fmd.row = 0;
			fmd.column = 0;
			objs.add(part, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(location, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			String str = objs.get(0).setInventoryLinkQuery();
			System.out.println(str);
//						partLink = new PartLink(part, location, sVars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// location and location
	@Test
	public void InventoryQueryLocationLocationTest() {
		Location parentLocation = null;
		Location childLocation = null;
		FormsMatrixDynamic fmd = null;
		SearchTargets objs = new SearchTargets(sVars);
//		objs.initResults();
		try {
			parentLocation = new Location(sVars);
			childLocation = new Location(sVars);
			fmd = new FormsMatrixDynamic(sVars);
			fmd.row = 0;
			fmd.column = 0;
			objs.add(parentLocation, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(childLocation, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			String str = objs.get(0).setInventoryLinkQuery();
			System.out.println(str);
//				partLink = new PartLink(part, location, sVars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFormsMatrixDynamicBySearchTargets() {
		FormsMatrixDynamic fmd = Utilities.getFormsMatrixDynamic();

		// extract the forms matrix
//		FormsMatrix formsMatrix = fmd.fm;

		// spin through the searchTargets
		Iterator<SearchTargets> itr = fmd.iterator();
		while (itr.hasNext()) {
			SearchTargets tmp = itr.next();
			// spin through the filtered lists
			Iterator<SearchTarget> fitr = tmp.iterator();
			while (fitr.hasNext()) {
				SearchTarget tmpFilteredList = fitr.next();
				// spin through each query
				for (SEARCHTYPES type : SEARCHTYPES.values()) {
					try {
						System.out.println("type:" + type.toString() + " filteredList:" + tmpFilteredList + "\nquery:"
								+ tmpFilteredList.getQuery(type));
					} catch (Exception e) {
						System.out.print(e.getStackTrace());
						fail(e.getLocalizedMessage());
					}
				}

			}
		}
	}

	@Test
	public void testFormsMatrixDynamicByRowColumn() {
		FormsMatrixDynamic fmd = Utilities.getFormsMatrixDynamic();

		// extract the forms matrix
//		FormsMatrix formsMatrix = fmd.fm;

		// spin through the rows
		for (int row = 0; row < fmd.getNumberOfRows(); row++) {
			for (int column = 0; column < fmd.getRow().size(); column++) {
				MyObject obj = fmd.getObject();
				System.out.println("row:" + row + " column:" + column + " obj:" + obj.toString() + " name:"
						+ obj.getInstanceName());
			}
		}
	}
}
