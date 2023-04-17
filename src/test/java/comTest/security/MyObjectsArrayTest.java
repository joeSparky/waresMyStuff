package comTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.FormsMatrixDynamic;
import com.forms.IdAndString;
import com.forms.IdAndStrings;
import com.forms.SearchTarget;
import com.forms.SearchTargets;
import com.security.MyLinkObject;
import com.security.MyObject;

import comTest.utilities.Utilities;

public class MyObjectsArrayTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);

		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
	}

	// create a family tree. select the lowest member. see if the correct family
	// member shows up at the desired level
	@Test
	public void testGetFilteredListLowestSelected() {
		// family tree from level1 to level3, select level3, see if level1
		// returned
		FormsMatrixDynamic fmd = null;
		Level1 levelOne = null;
		Level2 levelTwo = null;
		Level3 levelThree = null;
		SearchTargets objArr = null;
		try {
			objArr = new SearchTargets(sVars);
			fmd = new FormsMatrixDynamic(sVars);
			levelOne = new Level1(sVars);
			levelTwo = new Level2(sVars);
			levelThree = new Level3(sVars);
			levelOne.add();
			levelTwo.add();
			levelThree.add();
			levelOne.addChild(levelTwo);
			levelTwo.addChild(levelThree);
			objArr.add(new Level1(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objArr.add(new Level2(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objArr.add(levelThree, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		fmd.add(objArr);
		fmd.row = 0;
		fmd.column = 0;
		IdAndStrings idAndStrings = null;
		try {
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.ANCESTORS, sVars).doQuery(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		if (idAndStrings.size() != 1)
			fail("targets not size of 1");
		if (!idAndStrings.contains(levelOne))
			fail("targets does not contain levelOne");

	}

	// create a family tree. select the youngest descendant. ask for its ancestors
	// at
	// different levels
	@Test
	public void testGetFilteredAncestors() {
		FormsMatrixDynamic fmd = null;
		Level1 levelOne = null;
		Level2 levelTwo = null;
		Level3 levelThree = null;
		SearchTargets objArr = null;
		// l1.isAnchor = true;
		try {
			fmd = new FormsMatrixDynamic(sVars);
			levelOne = new Level1(sVars);
			levelTwo = new Level2(sVars);
			levelThree = new Level3(sVars);
			levelOne.add();
			levelTwo.add();
			levelOne.addChild(levelTwo);
			levelThree.add();
			levelTwo.addChild(levelThree);

			objArr = new SearchTargets(sVars);
			objArr.add(new Level1(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			// unselected level2
			objArr.add(new Level2(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			// selected level3
			objArr.add(levelThree, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		fmd.add(objArr);
		fmd.row = 0;
		// looking for the ancestors of level3 at the level2 level
		fmd.column = 1;

//		SearchTarget second = objArr.getFilteredList(1);
		IdAndStrings idAndStrings = null;
		try {
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.ANCESTORS, sVars).doQuery(true);
		} catch (Exception e) {
			for (StackTraceElement sts : e.getStackTrace())
				System.out.println(sts);
			fail(e.getLocalizedMessage());
		}
		// if (second.selectedObject != null)
		// fail("second selectedObject not null");
		if (idAndStrings == null)
			fail("targets null in second");
		if (!idAndStrings.contains(levelTwo))
			fail("targets does not contain " + levelTwo.getInstanceName());

		// go to top level
		fmd.column = 0;
		try {
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.ANCESTORS, sVars).doQuery(true);
		} catch (Exception e) {
			for (StackTraceElement sts : e.getStackTrace())
				System.out.println(sts);
			fail(e.getLocalizedMessage());
		}
		if (!idAndStrings.contains(levelOne))
			fail("targets does not contain " + levelOne.getInstanceName());
	}

	// get an empty list when the top object is loaded but has no children
	// object
	@Test
	public void testGetFilteredListTopLoadedCurrentLoaded() {
		FormsMatrixDynamic fmd = null;
		// Anchor anchor = null;
		// Level1 levelOne = new Level1(sVars);
		// Level2 levelTwo = new Level2(sVars);

		// try {
		// anchor = company.getAnchor();
		// levelOne.add(anchor);
		// levelTwo.add(anchor);
		// } catch (Exception e) {
		// fail(e.getLocalizedMessage());
		// }

		SearchTargets objs = new SearchTargets(sVars);

		try {
			fmd = new FormsMatrixDynamic(sVars);
			objs.add(new Level1(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(new Level2(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			// create a table
			new MyLinkObject(new Level1(sVars), new Level2(sVars), sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		fmd.add(objs);
		// set the focus on levelTwo
		fmd.row = 0;
		fmd.column = 1;
		IdAndStrings idAndStrings = null;
		try {
//			idAndStrings = new IdAndStrings(objs, 1, SearchTarget.SEARCHTYPES.DESCENDANTS).doQuery(true);
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.DESCENDANTS, sVars);
//			idAndStrings.displayState = IdAndStrings.DISPLAYSTATE.ATBEGINNINGWITHOUTASEARCH;
//			idAndStrings.direction = IdAndStrings.DIRECTION.FORWARD;
//			idAndStrings.firstDisplayedRecord = -1;
			idAndStrings.doQuery(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// if (second.selectedObject != null)
		// fail("second selectedObject set");
		if (!idAndStrings.isEmpty())
			fail("second targets not empty");
//		SearchTarget third = objs.getFilteredList(1);
		try {
//			idAndStrings = new IdAndStrings(objs, 1, SearchTarget.SEARCHTYPES.DESCENDANTS).doQuery(true);
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.DESCENDANTS, sVars);
//			idAndStrings.displayState = IdAndStrings.DISPLAYSTATE.ATBEGINNINGWITHOUTASEARCH;
//			idAndStrings.direction = IdAndStrings.DIRECTION.FORWARD;
//			idAndStrings.firstDisplayedRecord = -1;
			idAndStrings.doQuery(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// if (third.selectedObject != null)
		// fail("third selectedObject set");
		if (!idAndStrings.isEmpty())
			fail("third targets not empty");
	}

	// create a family tree with the top selected. Put in 4 objects
	// that use the top object as an anchor. Put in 4 objects
	// that do NOT use the top as an anchor.
	// ask for a filtered list and see if the 4 anchored objects
	// are returned.
	@Test
	public void testGetFilteredListAnchors() {
		FormsMatrixDynamic fmd = null;
		Level1 levelOneIsAnchor = null;
		Level1 levelOneIsNotAnchor = null;
		try {
			fmd = new FormsMatrixDynamic(sVars);
			levelOneIsAnchor = new Level1(sVars);
			levelOneIsNotAnchor = new Level1(sVars);
			levelOneIsAnchor.add();
			levelOneIsNotAnchor.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// 4 level2s that are anchored to l1Anchor
		for (int i = 0; i < 4; i++)
			try {
				Level2 levelTwoWithAnchor = new Level2(sVars);
				levelTwoWithAnchor.add();
				levelOneIsAnchor.addChild(levelTwoWithAnchor);
			} catch (Exception e) {
				fail(e.getLocalizedMessage());
			}
		// 4 level2s that are not anchored to l1Anchor
		for (int i = 0; i < 4; i++)
			try {
				Level2 levelTwoNotAnchor = new Level2(sVars);
				levelTwoNotAnchor.add();
			} catch (Exception e) {
				fail(e.getLocalizedMessage());
			}

		SearchTargets objs = new SearchTargets(sVars);

		try {
			objs.add(levelOneIsAnchor, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(new Level2(sVars), SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			fmd.add(objs);
			fmd.row = 0;
			fmd.column = 1;
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		objs.anchorObject = levelOneIsAnchor;
//		objs.setListInterface(new FilteredList());
//		try {
//			// TODO reinstate?
//			// objs.updateFilteredList();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}

		// SearchTarget first = objs.getFilteredList(0);
		// if (first.selectedObject == null)
		// fail("first not selected");
		// if (!first.selectedObject.equals(levelOneIsAnchor))
		// fail("first not levelOneAnchor");
//		SearchTarget second = objs.getFilteredList(1);
		IdAndStrings idAndStrings = null;
		try {
//			idAndStrings = new IdAndStrings(objs, 1, SearchTarget.SEARCHTYPES.DESCENDANTS).doQuery(true);
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.DESCENDANTS, sVars);
//			idAndStrings.displayState = IdAndStrings.DISPLAYSTATE.ATBEGINNINGWITHOUTASEARCH;
//			idAndStrings.direction = IdAndStrings.DIRECTION.FORWARD;
//			idAndStrings.firstDisplayedRecord = -1;
			idAndStrings.doQuery(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (idAndStrings == null)
			fail("no targets");
		if (idAndStrings.size() != 4)
			fail("expected 4 targets, got " + idAndStrings.size());
	}

	// create a family tree. select the highest element in the tree. see if the
	// correct
	// family shows up at the desired level
	@Test
	public void testGetFilteredListHighestSelectedUseClass() {
		FormsMatrixDynamic fmd = null;
		Level1 levelOne = null;
		Level2 levelTwo = null;
		Level3 levelThree = null;
		SearchTargets objArr = new SearchTargets(sVars);
		IdAndStrings idAndStrings = null;
		try {
			fmd = new FormsMatrixDynamic(sVars);

			// three generations
			levelOne = new Level1(sVars);
			levelTwo = new Level2(sVars);
			levelThree = new Level3(sVars);
			levelOne.add();
			levelTwo.add();
			levelThree.add();
			levelOne.addChild(levelTwo);
			levelTwo.addChild(levelThree);

			// selected level1
			objArr.add(levelOne, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			// unselected level2
			objArr.add(new Level2(sVars));
			// unselected level3
			objArr.add(new Level3(sVars));
			fmd.add(objArr);

			// focus on levelThree
			fmd.row = 0;
			fmd.column = 2;

			// ask for the descendants of levelOne, the highest selected object
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.DESCENDANTS, sVars);
			idAndStrings.doQuery(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		if (idAndStrings.isEmpty())
			fail("targets empty in second");
		if (idAndStrings.size() != 1)
			fail("expected size of 1, got " + idAndStrings.size());
		// did we get levelThree as a descendant of levelTwo
		if (!IdAndString.toIdAndString(levelThree).equals(idAndStrings.get(0)))
			fail("not an instance of level3");
	}

	boolean isTheSame(MyObject o, IdAndString idAndString) {
		if (idAndString.string == null || idAndString.string.isEmpty())
			fail("empty name");
		return idAndString.id == o.id && idAndString.string.equals(o.getInstanceName());
	}
}
