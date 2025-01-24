package comTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.MyConnection;
import com.db.SessionVars;
import com.db.XML;
import com.security.MyObject;
import com.security.MyObjectsArray;

import comTest.utilities.Utilities;

/**
 * test the MyObject instances that can have only one parent (the ID of the
 * parent resides in the child)
 * 
 * @author Joe
 *
 */
public class MyObjectLinkTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		XML.setCommonParamsPath(Utilities.getPathToTestParamsFile());
		sVars = new SessionVars();
		new Utilities().allNewTables(sVars);
		new NoParents(sVars).newTable(sVars);
		new OneParentChild(sVars).newTable(sVars);
		if (MyConnection.getActiveCount() != 0)
			throw new Exception("starting with " + MyConnection.getActiveCount() + " connections");
	}

	@After
	public void tearDown() throws Exception {
		if (MyConnection.getActiveCount() != 0)
			throw new Exception("leaving with non-empty pool");

	}

	MyObject noParents = null;
	MyObject oneParent = null;
	MyObjectsArray objs = null;

	public void setup() {
		// get a parent that can not have parents

		try {
			noParents = new NoParents(sVars);
			noParents.sanity();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			noParents.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// try {
		// oneParent.setParentId( noParents.id);
		// } catch (Exception e1) {
		// fail(e1.getLocalizedMessage());
		// }
		try {// get a child that can have only one parent
			oneParent = new OneParentChild(sVars);
			oneParent.sanity();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			oneParent.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		objs = new MyObjectsArray();
		objs.add(noParents);
		objs.add(oneParent);

		// MyLinkInternals.addInternal(noParents, oneParent,
		// "fer shur",
		// MyLinkInternals.LINKTYPE.EXTERNAL,
		// MyLinkInternals.DELETETYPE.DELETEDESCENDANTS);

		try {
			// new MyLinkObject(noParents, oneParent).add(1);
			noParents.addChild(oneParent);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * test adding a parent and a child
	 */
	@Test
	public void testAdd() {
		setup();
	}

	/**
	 * try to delete the parent
	 */
//	@Test
//	public void testDelete() {
//		setup();
//		try {
//			noParents.deleteTest();
//			fail("parent deletion should have failed");
//		} catch (Exception e) {
//		}
//	}
}
