package comTest.db;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.MyConnection;
import com.db.XML;

import comTest.utilities.Utilities;
import junit.framework.TestCase;

public class TestXML extends TestCase {

//	static final String LOGINFORMPATH = "comTest.sessionVars.LoginForm";
	static final String DISPATCHFORM = "comTest.sessionVars.DispatchForm";

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();

	}

	@Test
	public void testGetLoginName() {
//		SessionVars sVars = null;
//		try {
//			sVars = new SessionVars();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
		String tmp = null;

//		try {
//			tmp = sVars.xml.readXML(XML.LOGINPARAMNAME);
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//		if (!tmp.equals(LOGINFORMPATH))
//			fail("got '" + tmp + "', not '" + LOGINFORMPATH + "' in " + XML.XMLFILENAME);

		try {
			tmp = XML.readXML(XML.DISPATCHPARAMNAME);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!tmp.equals(DISPATCHFORM))
			fail("got '" + tmp + "' not '" + DISPATCHFORM + "' in " + XML.XMLFILENAME);

//		try {
//			sVars.xml.getLogin(sVars);
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
	}

	@Test
	public void testPathToFile() {
		try {
			XML.setCommonParamsPath("pureJunk");
			fail("accepted non-existant file");
		} catch (Exception e) {

		}

		try {
			XML.setCommonParamsPath(Utilities.getPathToTestParamsFile());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		String pathToAlmostEmpty = System.getProperty("user.dir") + File.separator + "almostEmptyCommonParams.xml";
		File file = new File(pathToAlmostEmpty);
		if (!file.exists())
			fail("could not find test file " + pathToAlmostEmpty);
		try {
			XML.setCommonParamsPath(pathToAlmostEmpty);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// try a garbage read
		try {
			XML.readXML("jfasdf");
			fail("read a parameter for jfasdf");
		} catch (Exception e) {
		}

		// try a valid read on a valid path
		try {
			XML.setCommonParamsPath(Utilities.getPathToTestParamsFile());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			XML.readXML(MyConnection.XMLDBUSER);
		} catch (Exception e) {
			fail("did not find " + MyConnection.XMLDBUSER + " in " + Utilities.getPathToTestParamsFile());
		}
	}

	@Test
	public void testPasswords() {
		String tmp = null;
		try {
			tmp = XML.readXML(XML.ADMINPASSWORD);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!tmp.equals("testAdminPassword"))
			fail("expected testAdminPassword, got " + tmp);
		
		try {
			tmp = XML.readXML(XML.READONLYPASSWORD);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		if (!tmp.equals("testReadOnlyPassword"))
			fail("expected testReadOnlyPassword, got " + tmp);
	}

}
