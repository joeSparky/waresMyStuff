package comTest.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.MyConnection;
import com.db.XML;

import comTest.utilities.Utilities;

public class Credentials {
	private static final String EXPECTEDROOTNAME = "myTestDbRootName";
	private static final String EXPECTEDROOTPASSWORD = "myTestDbRootPassword";
	
	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
	}
	
	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();

	}
	@Test
	public void testGetRootCredentials() {
		String rootName = null;
		String rootPassword = null;

		try {
			rootName = XML.readXML(MyConnection.DBROOTNAME);
			rootPassword = XML.readXML(MyConnection.DBPASSWORD);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!rootName.equals(EXPECTEDROOTNAME))
			fail("read rootName of " + rootName + ". Expected " + EXPECTEDROOTNAME);
		if (!rootPassword.equals(EXPECTEDROOTPASSWORD))
			fail("read rootName of " + rootPassword + ". Expected " + EXPECTEDROOTPASSWORD);
	}
}
