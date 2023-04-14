package comTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.security.MyObjects;

import comTest.utilities.Utilities;

public class MyObjectTestCTest {
SessionVars sVars = null;
	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);
		
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindExceptions() {
		Level1 levelOne = null;
		boolean exceptionThrown = false;
		try {
			levelOne = new Level1(sVars);
			levelOne.find(84848);
			fail("did not throw exception on invalid find().");
		} catch (Exception e) {
			exceptionThrown = true;
		}
		if (!exceptionThrown)
			fail("exception not thrown.");
	}

	/**
	 * move children of oldParent to be children of newParent
	 * @throws Exception 
	 */
	@Test
	public void testMoveChildrenOfThisParentToNewParent() throws Exception {
		Level1 oldParent = null;
		Level1 newParent = null;
		Level2 firstChild = null;
		Level2 secondChild = null;
		try {
			oldParent = new Level1(sVars);
			oldParent.add();
			newParent = new Level1(sVars);
			newParent.add();
			firstChild = new Level2(sVars);
			firstChild.add();
			secondChild = new Level2(sVars);
			secondChild.add();
			oldParent.addChild(firstChild);
			oldParent.addChild(secondChild);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			oldParent.moveChildrenOfThisParentToNewParent(newParent, new Level2(sVars));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		MyObjects kids = null;
		try {
			kids = newParent.getChildren(new Level2(sVars));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (kids.size() != 2)
			fail("expected a size of 2, got " + kids.size());
		if (!kids.containsMyObject(firstChild))
			fail("kids does not contain firstChild");
		if (!kids.containsMyObject(secondChild))
			fail("kids does not contain secondChild");
	}

	/**
	 * ask checkAncestry to throw an exception
	 */
	@Test
	public void testAnAncestorOf() {
		Recurse firstAndAnchor = null;
		Recurse second = null;
		Recurse third = null;
		Recurse fourth = null;
		try {
			firstAndAnchor = new Recurse(sVars);
			firstAndAnchor.add();

			second = new Recurse(sVars);
			second.add();
			firstAndAnchor.addChild(second);

			third = new Recurse(sVars);
			third.add();
			second.addChild(third);

			fourth = new Recurse(sVars);
			fourth.add();
			third.addChild(fourth);

		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// second is an ancestor of third
		try {
			if (!second.anAncestorOf(third))
				fail("second is an ancestor of third");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// second is an ancestor of fourth
		try {
			if (!second.anAncestorOf(fourth))
				fail("second is an ancestor of fourth");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// third is not an ancestor of second
		try {
			if (third.anAncestorOf(second))
				fail("allowed third to be an ancestor of second");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// an ancestor can not be an ancestor of itself
		try {
			if (third.anAncestorOf(third))
				fail("allowed self to pass ancestry.");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// top of the stack
		try {
			if (!firstAndAnchor.anAncestorOf(second))
				fail("firstAndAnchor is an ancestor of second");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

	}
}
