package comTest.db;

import static org.junit.Assert.*;

import org.junit.Test;

import com.db.MyConnection;
import com.db.SessionVars;

public class Credentials {
	private static final String EXPECTEDROOTNAME = "myTestDbRootName";
	private static final String EXPECTEDROOTPASSWORD = "myTestDbRootPassword";

	@Test
	public void testGetRootCredentials() {

		SessionVars sVars = null;
		try {
			sVars = new SessionVars(true);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		String rootName = null;
		String rootPassword = null;

		try {
			rootName = sVars.xml.readXML(MyConnection.DBROOTNAME);
			rootPassword = sVars.xml.readXML(MyConnection.DBPASSWORD);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!rootName.equals(EXPECTEDROOTNAME))
			fail("read rootName of " + rootName + ". Expected " + EXPECTEDROOTNAME);
		if (!rootPassword.equals(EXPECTEDROOTPASSWORD))
			fail("read rootName of " + rootPassword + ". Expected " + EXPECTEDROOTPASSWORD);
	}

//	@Test
//	public void testCreateBasicDataSource() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetConnection() {
//		fail("Not yet implemented");
//	}

}
