package partsTest.forms;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;

import com.db.SessionVars;
import com.parts.forms.CSVFormSelect;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import partsTest.Utilities.Utilities;

public class testCSVForm {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		if (sVars == null)
			sVars = new SessionVars();
//		
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

//	@Test
	// TODO broken testDumpInventory
	public void testDumpInventory() {
		Part partOne = null;
		Location location = null;

		try {
			partOne = new Part(sVars);
			partOne.setInstanceName("my part name");
			partOne.add();
			location = new Location(sVars);
			location.add();
			new PartLink(partOne, location, sVars).add().updateAddQuantity(33, false);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		SessionVars sVars = new SessionVars();
//		sVars.userNumber = 8483;
		String results = null;
		try {
			results = new CSVFormSelect(new SessionVars()).dumpInventory();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!results.contains("my part name, 33"))
			fail("did not find \"my part name, 33\"");
		System.out.println(results);
	}

}
