package partsTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.FormsMatrixDynamic;
import com.forms.SearchTargets;
import com.forms.Utils;
import com.parts.location.Location;
import partsTest.Utilities.Utilities;
import com.security.MyObjectsArray;

import comTest.security.Level1;
import comTest.security.Level2;

public class SearchTargetsTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
	}

	

	// set up loaded warehouse as an anchor. see if isAnchorInstanceSet returns
	// true
//	@Test
//	public void testWarehouseAnchorIsSet() {
//		Warehouse warehouse = null;
//		Level1 levelOne = null;
//		try {
//			warehouse = new Warehouse(sVars);
//			levelOne = new Level1(sVars);
//			warehouse.add(new Anchor(sVars));
//			levelOne.add(warehouse);
//			warehouse.addChild(levelOne);
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		SearchTargets objs = new SearchTargets(sVars);
//		objs.add(warehouse);
//		objs.add(levelOne);
////		objs.setListInterface(new FilteredList());
//		try {
//			if (!objs.isAnchorInstanceSet(levelOne).isEmpty())
//				fail("error string should be empty");
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

	// set up unloaded warehouse as an anchor. see if isAnchorInstanceSet
	// returns false
//	@Test
//	public void testWarehouseAnchorIsNotSet() {
//		Warehouse warehouse = null;
//		Level1 levelOne = null;
//		try {
//			warehouse = new Warehouse(sVars);
//			levelOne = new Level1(sVars);
//			warehouse.add(new Anchor(sVars));
//			levelOne.add(warehouse);
//			warehouse.addChild(levelOne);
//			warehouse.clear();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		SearchTargets objs = new SearchTargets(sVars);
//		objs.add(warehouse);
//		objs.add(levelOne);
////		objs.setListInterface(new FilteredList());
//		try {
//			if (objs.isAnchorInstanceSet(levelOne).isEmpty())
//				fail("error string should not be empty");
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

	// set up without warehouse as an anchor. see if isAnchorInstanceSet
	// returns false
//	@Test
//	public void testWarehouseAnchorNoWarehouse() {
//		Level2 notAWarehouse = null;
//		Level1 levelOne = null;
//		try {
//			notAWarehouse = new Level2(sVars);
//			levelOne = new Level1(sVars);
//			notAWarehouse.add(new Anchor(sVars));
//			levelOne.add(notAWarehouse);
//			notAWarehouse.addChild(levelOne);
//			notAWarehouse.clear();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		SearchTargets objs = new SearchTargets(sVars);
//		objs.add(notAWarehouse);
//		objs.add(levelOne);
////		objs.setListInterface(new FilteredList());
//		try {
//			if (objs.isAnchorInstanceSet(levelOne).isEmpty())
//				fail("error string should not be empty");
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

	/**
	 * recursive locations, parent attached to a central location, ask for
	 * singleParents to top
	 */
	@Test
	public void testLocationLevel() {
		FormsMatrixDynamic fmd = null;
		try {
			fmd = new FormsMatrixDynamic(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		Level1 levelOne = null;
		Location location1 = null;
		Location location2 = null;
		Location location3 = null;
		Location location4 = null;
		try {
//			anchor = warehouse.getAnchor();
			levelOne = new Level1(sVars);
			levelOne.add();
			location1 = new Location(sVars);
			location1.setInstanceName(Utils.getNextString() + "dkfj");
			location1.add();
			location2 = new Location(sVars);
			location2.setInstanceName(Utils.getNextString() + "dkfj");
			location2.add();
			location3 = new Location(sVars);
			location3.setInstanceName(Utils.getNextString() + "dkfj");
			location3.add();
			location4 = new Location(sVars);
			location4.setInstanceName(Utils.getNextString() + "dkfj");
			location4.add();
			location1.addChild(location2);
			location2.addChild(location3);
			location3.addChild(location4);

			levelOne.addChild(location3);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		SearchTargets searchTargets = new SearchTargets(sVars);
		try {
			searchTargets.add(levelOne);
			searchTargets.add(location3);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		fmd.add(searchTargets);
		fmd.row = 0;
		fmd.column = 1;

		MyObjectsArray moa = new MyObjectsArray();
		try {
			moa.getSingleParentsToTop(fmd, sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (moa.size() != 3)
			fail("expected size of 3, got " + moa.size());
		try {
			if (!moa.get(0).equals(location2))
				fail("expected location2 at moa[0], got " + moa.get(0).getInstanceName());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (!moa.get(1).equals(location1))
				fail("expected location1 at moa[1], got " + moa.get(1).getInstanceName());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (!moa.get(2).equals(levelOne))
				fail("expected levelOne at moa[2], got " + moa.get(2).getInstanceName());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void testLocationLevelTopNotSelected() {
		FormsMatrixDynamic fmd = null; 
		try {
			fmd = new FormsMatrixDynamic(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		Anchor anchor = null;
		Level1 levelOne = null;
		Location location1 = null;
		Location location2 = null;
		Location location3 = null;
		Location location4 = null;
		try {
//			anchor = warehouse.getAnchor();
			levelOne = new Level1(sVars);
			levelOne.add();
			location1 = new Location(sVars);
			location1.setInstanceName(Utils.getNextString() + "dkfj");
			location1.add();
			location2 = new Location(sVars);
			location2.setInstanceName(Utils.getNextString() + "dkfj");
			location2.add();
			location3 = new Location(sVars);
			location3.setInstanceName(Utils.getNextString() + "dkfj");
			location3.add();
			location4 = new Location(sVars);
			location4.setInstanceName(Utils.getNextString() + "dkfj");
			location4.add();
			location1.addChild(location2);
			location2.addChild(location3);
			location3.addChild(location4);
			levelOne.addChild(location3);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		SearchTargets searchTargets = new SearchTargets(sVars);
		try {
			searchTargets.add(levelOne);
			searchTargets.add(location3);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		fmd.add(searchTargets);
		fmd.row = 0;
		fmd.column = 1;

		levelOne.clear();

		MyObjectsArray moa = new MyObjectsArray();
		try {
			moa.getSingleParentsToTop(fmd, sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (moa.size() != 2)
			fail("expected size of 2, got " + moa.size());
		try {
			if (!moa.get(0).equals(location2))
				fail("expected location2 at moa[0], got " + moa.get(0).getInstanceName());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (!moa.get(1).equals(location1))
				fail("expected location1 at moa[1], got " + moa.get(1).getInstanceName());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		if (!moa.get(2).equals(levelOne))
//			fail("expected levelOne at moa[2], got " + moa.get(2).getInstanceName());

	}

}
