package comTest.forms;

import org.junit.After;
import org.junit.Before;
import com.db.SessionVars;

import comTest.utilities.Utilities;

public class SelectFormTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

//	/**
//	 * create a list of companies that should be available for selection
//	 */
//	@Test
//	public void testGetForm() {
//		
//		sVars.fmd = Utilities.getFormsMatrixDynamic();
//		SearchTargets objs = new SearchTargets(sVars);
//
//		try {
//			objs.add(new User(sVars));
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
////		objs.anchorObject = myCompany;
//
////		objs.setListInterface(new FilteredList());
////		try {
////			// create company to user link table
////			new MyLinkObject(myCompany, new User(sVars), sVars);
//////TODO reinstate?
////			// objs.updateFilteredList();
////		} catch (Exception e) {
////			fail(e.getLocalizedMessage());
////		}
//		SelectForm sf = null;
//		try {
//			sf = new SelectForm(sVars, SelectFormTest.class.getCanonicalName());
//			sf.getForm(sVars);
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

	
}
