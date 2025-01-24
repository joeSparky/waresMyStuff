package comTest.security;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.forms.SearchTargets;
import com.forms.SmartForm;
import com.security.MyObject;
import com.security.User;

import comTest.forms.BlankForm;
import comTest.utilities.Utilities;

public class MyObjectTest {

	SearchTargets moa = null;

	SmartForm blankForm = null;
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
		blankForm = new BlankForm(sVars);
		
		
		new Utilities().allNewTables(sVars);
//		FormsMatrixDynamic fmd = Utilities.getFormsMatrixDynamic();
		moa = new SearchTargets(sVars);
		moa.add(new User(sVars));
		// moa.add(new Warehouse(sVars));
//		blankForm = new BlankForm();

	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void getANameTest() {
		for (MyObject mo : moa.getObjects()) {
			if (mo.getAName() == null)
				fail("null getAName");
			if (mo.getAName().isEmpty())
				fail("empty getAName");
			if (mo instanceof User) {
				if (mo.getAName().equals(User.ANAME))
					continue;
				else
					fail("did not get " + User.ANAME);
			}
			fail("unknown instance of " + mo.getClass().toString());
		}
	}

	@Test
	public void getNameTest() {
		for (MyObject mo : moa.getObjects()) {
			if (mo.getLogicalName() == null)
				fail("null getNAME");
			if (mo.getLogicalName().isEmpty())
				fail("empty getNAME");
			if (mo instanceof User) {
				if (mo.getLogicalName().equals(User.NAME))
					continue;
				else
					fail("did not get " + User.NAME);
			}
			fail("unknown instance of " + mo.getClass().toString());
		}
	}

	
}
