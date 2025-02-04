package comTest.utilities;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.db.MyConnection;
import com.db.SessionVars;
import com.db.XML;
import com.errorLogging.Internals;
import com.forms.FormsMatrixDynamic;
import com.forms.SearchTarget;
import com.forms.SearchTargets;
import com.forms.Utils;
//import com.security.FilteredList;
import com.security.MyObject;
import com.security.MyObjectsArray;
import com.security.Table;
import comTest.security.Level1;
import comTest.security.Level2;
import comTest.security.Level2Brother;
import comTest.security.Level2ParentInChild;
import comTest.security.Level3;

public class Utilities {
	private static String UTILITY = "util";
	public static String ASSEMBLYNAME = UTILITY + "Assembly";
	public static String ASSEMBLYLOCATION = UTILITY + "AssemblyLocation";
	public static String PARTNAME = UTILITY + "Part";
	public static String EXHIBITNAME = UTILITY + "Exhibit";
	public static String EXHIBITLOCATION = UTILITY + "ExhibitLocation";
	public static String VENDORNAME = UTILITY + "VendorName";
	public static String VENDORURL = UTILITY + "VendorURL";
	public static String FIRSTNAME = UTILITY + "firstName";
	public static String LASTNAME = UTILITY + "lastName";
	public static String USERNAME = UTILITY + "userName";
	public static String PASSWORD = UTILITY + "pass";
	public static String WAREHOUSE = UTILITY + "warehouse";
	public static String LOCATION = UTILITY + "location";
	public static String ROLE = UTILITY + "role";
	public static String CLASSACCESS = UTILITY + "classAccess";
	public static String LEVEL = UTILITY + "level";
	public static String COMPANY = UTILITY + "company";
	public static String ANCHOR = UTILITY + "anchor";
	protected static SessionVars sVars = null;

	static {
		try {
			sVars = new SessionVars();
		} catch (Exception e) {
			Internals.logStartupError(e);
		}
	}

	/**
	 * get the next string and pad it to numberOfCharacters for text sorting. For a
	 * numberOfCharacters = 8, a g78 becomes g0000078
	 * 
	 * @param numberOfCharacters
	 * @return
	 */
	public static String getPaddedNextString(int numberOfCharacters) {
		String ret = Utils.getNextString();
		for (int i = ret.length(); i < numberOfCharacters; i++) {
			ret = new StringBuilder(ret).insert(1, "0").toString();
		}
		return ret;
	}

//	public static void changeDatabase(SessionVars sVars, String databaseName) {
//		
////		XML xml = null;
////		String url = "";
//		Connection connection = null;
//		try {
////			connection = MyConnection.getConnection();
////			connection.close();
//			connection = sVars.getConnection(databaseName);
////			MyConnection.closeAndOpenBasicDataSource();
////			xml = new XML(null);
////			url = "jdbc:mysql://localhost:3306/";
////			// url += xml.readXML(XMLDBNAME);
////			url += databaseName;
////			url += "?user=" + xml.readXML(com.db.MyConnection.MXLDBACCOUNT);
////			url += "&password=" + xml.readXML(com.db.MyConnection.MXLDBPASSWORD);
////			url += "&serverTimezone=UTC";
////			MyConnection.assignURL(url);
////			MyConnection.setMaxTotal();
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}
//	}

//	public static String getDbName() {
//		Connection conn = null;
//		Statement st = null;
//		ResultSet rs = null;
//		String query = "select database()";
//		try {
//			conn = MyConnection.getConnection();
//			st = conn.createStatement();
//			rs = st.executeQuery(query);
//			if (rs.next())
//				return rs.getString(1);
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		} finally {
//			try {
//				if (rs != null)
//					rs.close();
//				if (st != null)
//					st.close();
//				if (conn != null)
//					conn.close();
//			} catch (SQLException e) {
//				fail(e.getLocalizedMessage());
//			}
//		}
//		return null;
//	}

	public String getDbName(SessionVars sVars) {
		Connection c = null;
		Statement s = null;
		ResultSet r = null;
		String result = "";
		try {
			c = MyConnection.getConnection();
			s = c.createStatement();
			r = s.executeQuery("select database()");
			if (r.next())
				result = r.getString(1);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		} finally {
			try {
				if (r != null)
					r.close();
				if (s != null)
					s.close();
				if (c != null)
					c.close();
			} catch (SQLException e) {
				fail(e.getLocalizedMessage());
			}
		}
		return result;
	}

	public synchronized void allNewTables(SessionVars sVars) {
		// wipe out all tables;
		Connection outerConn = null;
		Connection innerConn = null;
		Statement outerSt = null;
		Statement innerSt = null;
		ResultSet rs = null;
		String dbName = getDbName(sVars);
//		SessionVars sVars = null;
		try {
//			sVars = new SessionVars();
//			dbName = sVars.xml.readXML(MyConnection.XMLDBNAME);
//			new MyConnection(sVars);
			outerConn = MyConnection.getConnection();
//			new MyConnection(sVars);
			innerConn = MyConnection.getConnection();
			outerSt = outerConn.createStatement();
			innerSt = innerConn.createStatement();
//			String q = "select table_name from information_schema.tables where table_schema ='"
//					+ new XML().readXML(MyConnection.XMLDBNAME) + "'";
			rs = outerSt.executeQuery(
					"select table_name from information_schema.tables where table_schema ='" + dbName + "'");
//			innerSt = outerConn.createStatement();
			while (rs.next()) {
				innerSt.executeUpdate("drop table if exists " + rs.getString("table_name"));
			}

		} catch (Exception e) {
			fail(e.getLocalizedMessage());

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (outerSt != null)
					outerSt.close();
				if (innerSt != null)
					innerSt.close();
				if (outerConn != null)
					outerConn.close();
				if (innerConn != null)
					innerConn.close();
			} catch (Exception e) {
				fail(e.getLocalizedMessage());
			}
		}

//		new MyConnection().wipeDb(XML.readStandardXML(MyConnection.XMLDBNAME));
		// clear the internal table that tracks if database table has been checked for
		// existence
		Table.clearTableExists();
	}

	public MyObject getAnObject(MyObject m) {
//		Anchor anchor;
//		try {
//			anchor = new Anchor(sVars).find(myObjectAnchor);
//		} catch (AnchorNotFoundException e) {
//			fail(e.getLocalizedMessage());
//		}
//		if (m instanceof User)
//			try {
//				return getAUser();
//			} catch (Exception e) {
//				fail(e.getLocalizedMessage());
//			}
		fail("unknown object " + m.getCanonicalName());
		try {
			return m.getNew();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * get a user with no access rights
	 * 
	 * @return
	 */
//	public static User getAUser() {
//		return new Utilities().getAUser();
//	}

	

	public static Level1 getLevel1() {
		Level1 user = null;
		try {
			user = new Level1(sVars);
			user.setInstanceName(LEVEL + "1" + Utils.getNextString());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			user.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return user;
	}

	public static Level2 getLevel2() {
		Level2 user = null;
		try {
			user = new Level2(sVars);
			user.setInstanceName(LEVEL + "2" + Utils.getNextString());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			user.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return user;
	}

	public static Level2Brother getLevel2External() {
		Level2Brother user = null;
		try {
			user = new Level2Brother(sVars);
			user.setInstanceName(LEVEL + "2External" + Utils.getNextString());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			user.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return user;
	}

	public static Level2ParentInChild getLevel2ParentInChild() {
		Level2ParentInChild user = null;
		try {
			user = new Level2ParentInChild(sVars);
			user.setInstanceName(LEVEL + "2ParentInChild" + Utils.getNextString());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			user.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return user;
	}

	public static Level3 getLevel3() {
		Level3 user = null;
		try {
			user = new Level3(sVars);
			user.setInstanceName(LEVEL + "3" + Utils.getNextString());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		try {
			user.add();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return user;
	}

	public static void createTable(String tableName, SessionVars sVars) {
		final int NAMELENGTH = 30;
		String newTab = "CREATE TABLE `" + tableName + "` (" + "`name` varchar(" + NAMELENGTH + ") NOT NULL,"
				+ "`id` int(11) auto_increment," + "UNIQUE KEY `name` (`name`)," + "PRIMARY KEY  (`id`)"
				+ ") ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
		Connection conn = null;
		Statement st = null;
		try {
			conn = MyConnection.getConnection();
			st = conn.createStatement();
			st.executeUpdate("DROP TABLE IF EXISTS `" + tableName + "`");
			st.executeUpdate(newTab);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (Exception e) {
				fail(e.getLocalizedMessage());
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				fail(e.getLocalizedMessage());
			}
		}
	}

	public static SearchTargets createSearchTargets() {
		// create a FormsMatrixDynamic
		FormsMatrixDynamic fdm = null;
		// create a stack with links between the layers
		// levelOne will be the anchor for the objects
		Level1 levelOne = null;
		Level2 levelTwo = null;
		Level3 levelThree = null;

		MyObjectsArray objArray = null;
		try {
			fdm = new FormsMatrixDynamic(sVars);
			levelOne = new Level1(sVars);
			levelOne.add();
			levelTwo = new Level2(sVars);
			levelTwo.add();
			levelThree = new Level3(sVars);
			levelThree.add();

			objArray = new MyObjectsArray();
			objArray.add(levelOne);
			objArray.add(levelTwo);
			objArray.add(levelThree);

			// create all combinations of links between the layers
			levelOne.addChild(levelTwo);
			levelTwo.addChild(levelThree);

			// clear the bottom portion of the stack
			// levelTwo.clear();
			levelThree.clear();
			// exercise the search string
			levelThree.searchString = "dkkasd fjasd eve";

		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		SearchTargets objs = new SearchTargets(sVars);
		try {
			objs.add(levelOne, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(levelTwo, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
			objs.add(levelThree, SearchTarget.EDITSELECTTYPE.EDITANDSELECT);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		// create a forms matrix
//		FormsMatrix fm = new FormsMatrix();
		// put the first row into the forms matrix
		fdm.add(objs);

		// create a FormsMatrixDynamic
//		FormsMatrixDynamic fdm = new FormsMatrixDynamic();

		// pass the FormsMatrixDynamic to the lower levels so the lower levels know
		// about each other
//		fm.setFormsMatrixDynamic(fdm);

		// point to the middle tab of the first row
		fdm.row = 0;
		fdm.column = 1;

//		objs.initResults();
		return objs;
	}

	public static FormsMatrixDynamic getFormsMatrixDynamic() {
		FormsMatrixDynamic fdm = null;
		try {
			fdm = new FormsMatrixDynamic(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
//		FormsMatrix fm = new FormsMatrix();
		SearchTargets searchTargets = new SearchTargets(sVars);
		SearchTarget filteredList = new SearchTarget(getLevel1(), sVars);

		// first tab of first row
//		filteredList.obj = getLevel1(anchor);
		searchTargets.add(filteredList);

		// second tab of first row
		filteredList = new SearchTarget(getLevel2(), sVars);
//		filteredList.obj = getLevel2(anchor);
		searchTargets.add(filteredList);

		// third tab of first row
		filteredList = new SearchTarget(getLevel3(), sVars);
//		filteredList.obj = getLevel3(anchor);
		searchTargets.add(filteredList);

		// put the first row into formMatrix
		fdm.add(searchTargets);

		// pass the new structure to the lower levels
//		fm.setFormsMatrixDynamic(fdm);

		// point to the middle tab of the first row
		fdm.row = 0;
		fdm.column = 1;
		return fdm;
	}
	public static String getPathToTestParamsFile() {
		return System.getProperty("user.dir")+File.separator+"testcommonParams.xml";
	}
	
	public static void beforeTest() throws IOException, Exception {
		XML.setCommonParamsPath(Utilities.getPathToTestParamsFile());
		if (MyConnection.getActiveCount() != 0)
			throw new Exception("starting with " + MyConnection.getActiveCount() + " connections");		
	}
	public static void afterTest() throws IOException, Exception {
		if (MyConnection.getActiveCount() != 0)
			throw new Exception("ending with " + MyConnection.getActiveCount() + " connections");		
	}
}
