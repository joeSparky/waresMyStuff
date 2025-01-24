package partsTest.exhibit;

import static org.junit.Assert.fail;

//import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.SessionVars;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.parts.security.PartLink;
import com.security.ExceptionCoding;
import com.security.MyObjects;

import partsTest.Utilities.Utilities;

public class LocationTest {
	SessionVars sVars = null;
	Utilities utilities = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		if (sVars == null)
			sVars = new SessionVars();
		utilities = new Utilities();//
		utilities.allNewTables(sVars);
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void testMoveItemWithUnloadedItem() {
		Location destination = null;
		Location source = null;
		try {
			// Anchor anchor = company.getAnchor();
			destination = new Utilities().getLocation();
			source = new Utilities().getLocation();
//			Attributes family = Utilities.getAFamily(w, warehouseAnchor);
//			Part member = Utilities.getAMember(family, warehouseAnchor);
		} catch (Exception e) {
			e.getLocalizedMessage();
		}
		try {
			destination.moveItem(new Part(sVars), source, 33);
			fail("did not find unloaded item");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMoveItemWithUnloadedDestination() {
		Part item = null;
		Location source = null;
		try {
			source = new Utilities().getLocation();
			item = new Part(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			new Location(sVars).moveItem(item, source, 9);
			fail("did not find unloaded destination");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMoveItemWithUnloadedSource() {
		Location destination = null;
		Part member = null;
		try {
			destination = new Location(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			destination.moveItem(member, new Location(sVars), 4);
			fail("did not find unloaded source");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMoveItemWithUnlinkedSource() throws ExceptionCoding {
		Part part = null;
		Location source = null;
		Location destination = null;
		try {
			part = (Part) new Part(sVars).add();
			destination = new Location(sVars);
			source = (Location) new Location(sVars).add();
			part.addChild(source, 4545);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// delete the link
		PartLink mlo = null;
		try {
			mlo = new PartLink(part, source, sVars).find();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!mlo.linkExists())
			fail("link should exist");
		try {
			mlo.deleteUnconditionally();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			destination.moveItem(part, source, 1);
			fail("did not find missing item -> source link");
		} catch (Exception e) {
		}
	}

	@Test
	public void testMoveLocationNotLoaded() {
		try {
			new Location(sVars).moveChildrenOfThisParentToNewParent(new Location(sVars), new Location(sVars));
			fail("did not catch unloaded source location");
		} catch (Exception e) {
		}
	}

	// put an item at a location
	@Test
	public void testUtilityLocation() {
		Location source = null;
		Part member = null;
		try {
			source = (Location) new Location(sVars).add();
			member = (Part) new Part(sVars).add();
			member.addChild(source, 99);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		MyObjects objs = null;
		try {
			objs = source.listAllParentsOfType(member);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (objs.size() != 1)
			fail("expected 1 item in the source location, found " + objs.size());
		if (!objs.contains(member))
			fail("did not find item at source.");
	}

	@Test
	public void testEquals() {
		Location loc = null;
		Location secondLocation = null;
		try {
			loc = utilities.getLocation();
			secondLocation = utilities.getLocation();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			if (!loc.equals(loc))
				fail("loc is not equal to itself");
			if (loc.equals(secondLocation))
				fail("loc is not secondLocation");
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	/**
	 * create two locations in a parent-child configuration. try to delete the
	 * parent. see if deleteTest complains
	 */
	@Test
	public void testDeleteTest() {
		Location loc = null;
		try {
			loc = utilities.getLocation();
			loc.addChild(utilities.getLocation());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			loc.deleteTest();
		} catch (Exception e) {
			fail("did not allow the deletion of parent location");
		}
	}
}
