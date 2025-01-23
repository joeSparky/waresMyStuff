package comTest.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.StorageFactory;

public class StorageFactoryTest {

	SessionVars sVars = null;
	MyStorage myVars = null;
	String uniqueName = "ldkfsjs";

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars();
		myVars = new MyStorage().get(myVars, sVars, uniqueName);
	}

	@Test
	public void testNumberSet() {
		if (myVars.testNumber != 7)
			fail("expected 7, got " + myVars.testNumber);
	}

	@Test
	public void testGetStorage() {
		MyStorage ms = null;
		ms = new MyStorage().get(ms, sVars, "kdiecksj");
		if (ms == null)
			fail("no storage");
	}

	@Test
	public void testStoreStuff() {
		String string545 = "random string";
		MyStorage newToMe = null;
		newToMe = new MyStorage().get(newToMe, sVars, string545);
		if (newToMe.testNumber != 7)
			fail("expected testnumber=7, got testNumber=" + newToMe.testNumber);
		// set this instance
		newToMe.testNumber = 545;

		// get the same instance using the same unique name
		MyStorage shouldHave545 = null;
		shouldHave545 = new MyStorage().get(shouldHave545, sVars, string545);
		if (shouldHave545.testNumber != 545)
			fail("expected testnumber=545, got testNumber=" + shouldHave545.testNumber);
	}

	class MyStorage extends StorageFactory {
		int testNumber = 7;

		@Override
		protected MyStorage getNew() {
			return new MyStorage();
		}

		@Override
		public MyStorage get(Object tc, SessionVars sVars, String unique) {
			return (MyStorage) super.get(tc, sVars, unique);
		}

	}

}
