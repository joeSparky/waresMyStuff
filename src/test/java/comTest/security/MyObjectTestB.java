package comTest.security;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.security.MyLinkObject;
import com.security.MyObject;
import com.security.MyObjects;
import com.security.MyObjectsArray;

import comTest.utilities.Utilities;

public class MyObjectTestB {
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
	public void testGetTopOfList() {
		Level1 l1 = null;
		try {
			l1 = new Level1(sVars);
			l1.newTable(sVars);
			l1.setInstanceName("level 1");
			l1.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		Level2 l2 = null;
		try {
			l2 = new Level2(sVars);
			l2.newTable(sVars);
			l2.setInstanceName("level 2");
			l2.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		Level3 l3 = null;
		try {
			l3 = new Level3(sVars);
			l3.newTable(sVars);
			l3.setInstanceName("level 3");
			l3.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// link them
		// MyLinkInternals.addInternal(l1, l2,
		// "fur shur",
		// MyLinkInternals.LINKTYPE.EXTERNAL,
		// MyLinkInternals.DELETETYPE.KEEPDESCENDANTS);
		// MyLinkObject mlo = new MyLinkObject(l1, l2);
		try {
			l1.addChild(l2);
			// mlo.add(1);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// MyLinkInternals.addInternal(l2, l3,
		// "fur shur",
		// MyLinkInternals.LINKTYPE.EXTERNAL,
		// MyLinkInternals.DELETETYPE.KEEPDESCENDANTS);
		// mlo = new MyLinkObject(l2, l3);
		try {
			l2.addChild(l3);
			// mlo.add(1);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	// check the initial conditions for move
	@Test
	public void testMoveInitialize() {
		// unloaded instance
		try {
			new RecursiveNot(sVars).moveToNewParentRecursive(new Level2(sVars));
			fail("allowed unloaded source.");
		} catch (Exception e) {
		}

		// try loaded instance with unloaded destination
		RecursiveNot recursiveNot = null;
		try {
			recursiveNot = new RecursiveNot(sVars);
			recursiveNot.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			recursiveNot.moveToNewParentRecursive(new RecursiveNot(sVars));
			fail("allowed unloaded destination.");
		} catch (Exception e) {
		}

		// try nonrecursive source, recursive destination
		Recurse recursive = null;
		try {
			recursive = new Recurse(sVars);
			recursive.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			recursiveNot.moveToNewParentRecursive(recursive);
			fail("allowed a nonrecursive source");
		} catch (Exception e) {
		}

		// try a non-recursive destination
		try {
			recursive.moveToNewParentRecursive(recursiveNot);
			fail("allowed a nonrecursive destination");
		} catch (Exception e) {
		}

		// try to move two different recursive classes
		RecursiveTwo recursiveTwo = null;
		try {
			recursiveTwo = new RecursiveTwo(sVars);
			recursiveTwo.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			recursive.moveToNewParentRecursive(recursiveTwo);
			fail("allowed to move two different classes");
		} catch (Exception e) {
		}
	}

	// move a grandchild to be a child of the grandparent
	@Test
	public void testMove() throws Exception {
		MyObjects objs = null;
		Level1 levelOne = null;
		Recurse grandParent = null;
		Recurse parent = null;
		Recurse child = null;
		try {
			levelOne = new Level1(sVars);
			grandParent = new Recurse(sVars);
			parent = new Recurse(sVars);
			child = new Recurse(sVars);
			levelOne.add();
			grandParent.add();
			levelOne.addChild(grandParent);
			parent.add();
			grandParent.addChild(parent);
			child.add();
			parent.addChild(child);
			// verify everything is set up correctly
			objs = levelOne.getChildren(new Recurse(sVars));
			if (!objs.containsMyObject(grandParent)) {
				System.out.println("looking for '"+grandParent.getInstanceName()+"' in");
				Iterator<MyObject> itr = objs.iterator();
				while (itr.hasNext()) {
					System.out.println("objs '"+itr.next().getInstanceName()+"'");
				}
				fail("levelOne grandparent fail");
			}
			objs = grandParent.getChildren(new Recurse(sVars));
			if (!objs.containsMyObject(parent))
				fail("grandparent parent fail");
			objs = parent.getChildren(new Recurse(sVars));
			if (!objs.containsMyObject(child))
				fail("parent child fail");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			child.moveToNewParentRecursive(grandParent);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// the grandparent should now be a parent of both parent and child

		try {
			objs = grandParent.listChildren(new Recurse(sVars));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (objs.size() != 2)
			fail("expected size()==2, got " + objs.size());
		if (!objs.containsMyObject(parent))
			fail("did not find parent");
		if (!objs.containsMyObject(child))
			fail("did not find child");
	}

	// try to move a parent under a child
	@Test
	public void testMoveChildAnAncestor() {
		Level1 levelOne = null;
		Recurse parent = null;
		Recurse child = null;
		try {
			levelOne = new Level1(sVars);
			parent = new Recurse(sVars);
			child = new Recurse(sVars);
			levelOne.add();
			parent.add();
			child.add();
			parent.addChild(child);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			parent.moveToNewParentRecursive(child);
			fail("allowed a child to be assigned as a parent");
		} catch (Exception e) {
		}
	}

	// recursion with one child
	@Test
	public void testListChildrenRecursive() throws Exception {
		Recurse first = null;
		Recurse second = null;
		// Recursive third = new Recursive();
		try {
			first = new Recurse(sVars);
			second = new Recurse(sVars);
			first.add();
			second.add();
			// third.add(first);
			first.addChild(second);
			// second.addChild(third, 33);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		MyObjectsArray stack = new MyObjectsArray();
		stack.add(first);
		MyObjects kids = null;
		try {
			kids = first.listChildren(stack);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (kids.size() != 1)
			fail("expected first to have 1 child, got " + kids.size());
		if (!kids.containsMyObject(second))
			fail("second not in kids");
	}

	// recursion with one child
	@Test
	public void testListChildrenRecursive2Kids() throws Exception {
		Recurse first = null;
		Recurse second = null;
		Recurse third = null;
		try {
			first = new Recurse(sVars);
			second = new Recurse(sVars);
			third = new Recurse(sVars);
			first.add();
			second.add();
			third.add();
			first.addChild(second);
			first.addChild(third);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		MyObjectsArray stack = new MyObjectsArray();
		stack.add(first);
		MyObjects kids = null;
		try {
			kids = first.listChildren(stack);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (kids.size() != 2)
			fail("expected first to have 2 kids, got " + kids.size());
		if (!kids.containsMyObject(second))
			fail("second not in kids");
		if (!kids.containsMyObject(third))
			fail("third not in kids");
	}

	// move an orphan to a parent
	@Test
	public void testMoveOrphanOK() {
		Level1 commonAnchor = null;
		Recurse parent = null;
		Recurse orphan = null;

		try {
			commonAnchor = new Level1(sVars);
			parent = new Recurse(sVars);
			orphan = new Recurse(sVars);
			if (orphan.listParentsClasses().isEmpty())
				fail("need class with parent types define.");
			commonAnchor.add();
			parent.add();
			orphan.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// move
		try {
			orphan.moveToNewParentRecursive(parent);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			if (!new MyLinkObject(parent, orphan, sVars).linkExists())
				fail("did not move an orphan");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
