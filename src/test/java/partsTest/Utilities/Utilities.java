package partsTest.Utilities;

import static org.junit.Assert.fail;

import com.forms.Utils;
import com.parts.location.Location;

public class Utilities extends comTest.utilities.Utilities {
	private static String UTILITY = "utility";
	public static String ASSEMBLYNAME = UTILITY + "Assembly";
	public static String ITEMNAME = UTILITY + "Item";
	public static String LOCATIONNAME = UTILITY + "Location";
	public static String ASSEMBLYLOCATION = UTILITY + "AssemblyLocation";
	public static String PARTNAME = UTILITY + "Part";
	public static String EXHIBITNAME = UTILITY + "Exhibit";
	public static String EXHIBITLOCATION = UTILITY + "ExhibitLocation";
	public static String VENDORNAME = UTILITY + "VendorName";
	public static String VENDORURL = UTILITY + "VendorURL";
	public static String FIRSTNAME = UTILITY + "firstName";
	public static String LASTNAME = UTILITY + "lastName";
	public static String USERNAME = UTILITY + "userName";
	public static String PASSWORD = UTILITY + "password";
	public static String WAREHOUSE = UTILITY + "warehouse";
	public static String FAMILYNAME = UTILITY + "Family";
	public static String FAMILYDESCRIPTION = UTILITY + "family description";
	public static String MEMBERNAME = UTILITY + "Member";

	

	

	/**
	 * add a generic location in warehouse. Assumes no parent location.
	 * 
	 * @param warehouse
	 * @return
	 */
	public Location getLocation() {
		Location location = null;
		// location.warehouse = warehouse.id;
		try {
			location = new Location(sVars);
			location.setInstanceName(LOCATIONNAME + Utils.getNextString());
			location.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return location;
	}
}
