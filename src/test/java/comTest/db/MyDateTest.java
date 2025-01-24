package comTest.db;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;

import comTest.utilities.Utilities;

public class MyDateTest {
	MyDate myDate = null;
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
		new Utilities().allNewTables(sVars);
		myDate = new MyDate(sVars);
	}
	
	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	/**
	 * create an instance of myDate
	 */
	void createInstance() {
		try {
			myDate.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

//	@Test
//	public void testExtendUpdate() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testExtendNewTable() {
		try {
			myDate.newTable(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testMyDate() {
		// catch the instance before it's been inventoried
		try {
			myDate.getInventoryDate();
			fail("did not catch the instance before it's been invetoried.");
		} catch (Exception e) {
		}
		createInstance();
		String inventoriedString = null;
		try {
			inventoriedString = myDate.getInventoryDate();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String stringDate = formatter.format(new Date());
		if (!stringDate.equals(inventoriedString))
			fail("inventoriedDate is '" + inventoriedString + "', expected '" + stringDate + "'");
	}

	@Test
	public void testExtractInfoResultSet() {
		createInstance();

		// look for the instance in the database
		MyDate foundMyDate = null;
		try {
			foundMyDate = new MyDate(sVars);
			foundMyDate.find(myDate.id);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// see if the inventoried date is set in the instance
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		String stringDate = formatter.format(new Date());
		String foundInventoryDate = null;
		try {
			foundInventoryDate = foundMyDate.getInventoryDate();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!stringDate.equals(foundInventoryDate))
			fail("inventoriedDate is '" + foundInventoryDate + "', expected '" + stringDate + "'");

	}

}
