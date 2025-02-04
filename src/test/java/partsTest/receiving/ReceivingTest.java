package partsTest.receiving;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import partsTest.Utilities.Utilities;

public class ReceivingTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();

		// System.out.println("database
		// name:"+XML.readStandardXML(MyConnection.XMLDBNAME));
		new Utilities().allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

//	@Test
//	public void initialTest() {
//		// Warehouse warehouse = Utilities.getAWarehouse();
//		// Family family = Utilities.getAFamily(warehouse);
//		// Member member = Utilities.getAMember(family);
//		// Location loc = Utilities.getLocation(warehouse);
////		TimeReceived item = null;
//		// item.parentId = member.id;
//		// item.locationId = loc.id;
//		try {item = new TimeReceived();
//			// item.sanity();
//			item.add(Utilities.getAnAnchor());
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

	@Test
	public void consolidateTest() {
		Part member = null;
		Location loc = null;
		PartLink pl = null;
		try {
			member = (Part) new Part(sVars).add();
			loc = (Location) new Location(sVars).add();
			pl = new PartLink(member, loc, sVars);
			pl.setLinkQuantity(1);
			pl.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (pl.getLinkQuantity() != 1)
				fail("expected quantInStock of 1, got " + pl.getLinkQuantity());
		} catch (Exception e1) {
			fail(e1.getLocalizedMessage());
		}
		// put another of the same item at the same location.
		try {
			pl.updateAddQuantity(1, false);
			if (pl.getLinkQuantity() != 2)
				fail("consolidation failure");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	// receive an item into a location. see if the location is recommended.
	@Test
	public void preferredLocationTestA() {
		// put an item into the warehouse

		Part part = null;
		Location loc = null;PartLink pl = null;
		try {
			part = (Part) new Part(sVars).add();
			loc = (Location) new Location(sVars).add();
			pl = new PartLink(part, loc, sVars);
			pl.setLinkQuantity(3);
			pl.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (pl.getLinkQuantity() != 3)
				fail("expected quantity of 3, got " + pl.getLinkQuantity());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
