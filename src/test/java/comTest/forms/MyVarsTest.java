package comTest.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;

public class MyVarsTest {
	SessionVars sVars = null;
	MyVars myVars = null;
	String uniqueName = "ldkfsjs";

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);
		myVars = new MyVars(sVars, uniqueName);
	}

	class MyVars extends com.forms.MyVars {

		int myNumber = 7;
		boolean survived = true;

		public MyVars(SessionVars sVars, String uniqueName) throws Exception {
			super(sVars, uniqueName);
//			put();
			if (get() == null) {
				survived = false;
				put();
			}
		}
	}

	@Test
	public void testMyVars() {
		if (myVars.myNumber != 7)
			fail("did not initialize myNumber");
	}

	@Test
	public void coldGetTest() {
		MyVars anotherMyVars = null;
		try {
			anotherMyVars = new MyVars(sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (anotherMyVars.myNumber != 7)
			fail("did not get initial value of 7");
	}

	/**
	 * see if put changes the session copy
	 */
	@Test
	public void writeThroughTest() {
		MyVars anotherMyVars = null;
		try {
			anotherMyVars = new MyVars(sVars, uniqueName);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		anotherMyVars.myNumber = 1234;
		try {
			anotherMyVars.put();
		} catch (Exception e1) {
			fail(e1.getLocalizedMessage());
		}

		MyVars yetAnother = null;
		try {
			yetAnother = (MyVars) new MyVars(sVars, uniqueName).get();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (yetAnother.myNumber != 1234)
			fail("expected 1234, got " + yetAnother.myNumber);
	}

	@Test
	public void getAfterPutTest() {
		if (myVars.myNumber != 7)
			fail("don't understand initialization");
		myVars.myNumber = 93;
		try {
			myVars.put();
			myVars = (MyVars) myVars.get();
		} catch (Exception e) {
			fail("put failed. " + e.getLocalizedMessage());
		}
		if (myVars.myNumber != 93)
			fail("put failed. Expected 93, got " + myVars.myNumber);
	}
}
