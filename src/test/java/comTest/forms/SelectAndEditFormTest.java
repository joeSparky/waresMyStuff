package comTest.forms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import com.db.SessionVars;
import com.forms.FormsMatrixDynamic;
import com.forms.SearchTargets;
import com.forms.SelectAndEditForm;
import comTest.utilities.Utilities;
//import partsTest.forms.SelectAndEditForm;

public class SelectAndEditFormTest {
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

//	@Test
	public void testResetForm() {

//		FormsMatrixDynamic fmd = null;
//		User user = null;
		try {
			sVars.fmd = new FormsMatrixDynamic(sVars);
//			user = new User(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		SearchTargets objs = new SearchTargets(sVars);
//		try {
//			objs.add(user);
////TODO reinstate?
//			// objs.updateFilteredList();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
		sVars.fmd.add(objs);
		try {
			objs = new SearchTargets(sVars);
//			objs.add(user);
			

//TODO reinstate?
			// objs.updateFilteredList();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

//		Everyone everyone = new Everyone();
//		everyone.put(user);
//		everyone.put(role);
//		everyone.put(company);
		sVars.fmd.add(objs);
		SelectAndEditForm sf = null;
		try {
			sf = new SelectAndEditForm(sVars, sVars.fmd);
			sf.getForm(sVars);
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				System.out.println(ste);
			fail(e.getLocalizedMessage());
		}
	}
}
