package comTest.forms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;

import com.forms.FormsMatrixDynamic;
import com.forms.IdAndStrings;
import com.forms.SearchTarget;
import com.forms.SearchTargets;
import com.forms.SelectForm;
import com.security.User;

import comTest.security.Level1;
import comTest.utilities.Utilities;

public class SelectFormTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		sVars = new SessionVars(true);
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * create a list of companies that should be available for selection
	 */
	@Test
	public void testGetForm() {
		
		sVars.fmd = Utilities.getFormsMatrixDynamic();
		SearchTargets objs = new SearchTargets(sVars);

		try {
			objs.add(new User(sVars));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		objs.anchorObject = myCompany;

//		objs.setListInterface(new FilteredList());
//		try {
//			// create company to user link table
//			new MyLinkObject(myCompany, new User(sVars), sVars);
////TODO reinstate?
//			// objs.updateFilteredList();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
		SelectForm sf = null;
		try {
			sf = new SelectForm(sVars, SelectFormTest.class.getCanonicalName());
			sf.getForm(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testGetFilteredListTopSelected() {
		Level1 selected = Utilities.getLevel1();
		FormsMatrixDynamic fmd = null;
		// have the user as the object in focus
		// create 3 users that are children of selected;
		try {
			fmd = new FormsMatrixDynamic(sVars);
			selected.addChild(new Utilities().getAUser());
			selected.addChild(new Utilities().getAUser());
			selected.addChild(new Utilities().getAUser());
		} catch (Exception e1) {
			fail(e1.getLocalizedMessage());
		}

		SearchTargets objs = new SearchTargets(sVars);

		try {
			objs.add(selected);
			objs.add(new User(sVars));
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		fmd.add(objs);
		fmd.row = 0;
		fmd.column = 1;

		IdAndStrings idAndStrings = null;
		try {
			idAndStrings = new IdAndStrings(fmd, SearchTarget.SEARCHTYPES.DESCENDANTS, sVars);
//			idAndStrings.displayState = IdAndStrings.DISPLAYSTATE.ATBEGINNINGWITHOUTASEARCH;			
//			idAndStrings.direction = IdAndStrings.DIRECTION.FORWARD;
//			idAndStrings.firstDisplayedRecord = -1;
			
			idAndStrings.doQuery();
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace()) {
				System.out.println(ste);
			}
			fail(e.getLocalizedMessage());
		}
		if (idAndStrings == null)
			fail("second.targets null");
//		if (second.descendantTargets.size() != 3)
		if (idAndStrings.size() != 3)
			fail("targets does not have 3 instances");
	}
}
