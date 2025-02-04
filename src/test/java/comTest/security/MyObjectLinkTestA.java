package comTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import comTest.utilities.Utilities;

public class MyObjectLinkTestA {
SessionVars sVars = null;
	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
		
		new Utilities().allNewTables(sVars);
		new POCWithOnlyOneParent(sVars).newTable(sVars);
		new ChildWithOnlyOneParent(sVars).newTable(sVars);
//		MyLinkInternals.addInternal(new ParentOfChildWithOnlyOneParent(),
//				new ChildWithOnlyOneParent(),
//				"fer shur",
//				MyLinkInternals.LINKTYPE.EXTERNAL, MyLinkInternals.DELETETYPE.KEEPDESCENDANTS);
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	// update a parent / child link that uses parentId
	@Test
	public void testUpdate() {
		POCWithOnlyOneParent p = null;
		try {
			p = new POCWithOnlyOneParent(sVars);
			p.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		ChildWithOnlyOneParent c = null;
		try {
			c = new ChildWithOnlyOneParent(sVars);
			c.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		MyLinkObject mlo = new MyLinkObject(p, c);
		try {
			p.addChild(c);
//			mlo.add(1);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
