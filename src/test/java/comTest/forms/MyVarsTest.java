package comTest.forms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;

import comTest.utilities.Utilities;

public class MyVarsTest {
	SessionVars sVars = null;
	TestClass myVars = null;
	String uniqueName = "ldkfsjs";

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
		myVars = TestClass.get(myVars, sVars, uniqueName);
	}
	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void testMyVars() {
//		myVars = TestClass.get(myVars, sVars, uniqueName);
		if (myVars.testNumber != 7)
			fail("did not initialize myNumber");
	}

	@Test
	public void coldGetTest() {
		TestClass anotherMyVars = null;
		sVars.testSessionVariables.clear();
		try {
			anotherMyVars = TestClass.get(anotherMyVars, sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			TestClass.get(anotherMyVars, sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (anotherMyVars.testNumber != 7)
			fail("did not get initial value of 7");
	}

	/**
	 * see if put changes the session copy
	 */
	@Test
	public void writeThroughTest() {
		TestClass anotherMyVars = null;
		try {
			anotherMyVars = TestClass.get(anotherMyVars, sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		anotherMyVars.testNumber = 1234;
//		try {
//			anotherMyVars.put();
//		} catch (Exception e1) {
//			fail(e1.getLocalizedMessage());
//		}

		TestClass yetAnother = null;
		try {
			yetAnother =TestClass.get(yetAnother, sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (yetAnother.testNumber != 1234)
			fail("expected 1234, got " + yetAnother.testNumber);
	}

	@Test
	public void getAfterPutTest() {
		if (myVars.testNumber != 7)
			fail("don't understand initialization");
		myVars.testNumber = 93;
		if (myVars.testNumber != 93)
			fail("put failed. Expected 93, got " + myVars.testNumber);
	}

	public class TestClass  {
		int testNumber = 7;

		public TestClass() {

		}

		static TestClass get(TestClass tc, SessionVars sVars, String un) {
			if (sVars.session == null) {
				// using testSessionVariables
				if (sVars.testSessionVariables.containsKey(un))
					// overwrite input TestClass with stored TestClass
					return (TestClass) sVars.testSessionVariables.get(un);
				else {
					// fresh start
					if (tc == null)
						tc = new MyVarsTest().new TestClass();
					sVars.testSessionVariables.put(un, tc);
					return tc;
				}
			} else {
				// using session variables
				if (sVars.session.getAttribute(un) == null) {
					// not in session
					if (tc == null)
						tc = new MyVarsTest().new TestClass();
					sVars.session.setAttribute(un, tc);
					return tc;
				} else {
					// overwrite input TestClass with stored TestClass
					return (TestClass) sVars.session.getAttribute(un);
				}
			}
		}
	}
}
