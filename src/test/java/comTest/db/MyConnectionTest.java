package comTest.db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.db.MyConnection;
import com.db.SessionVars;
import com.db.XML;

import comTest.utilities.Utilities;

public class MyConnectionTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
		sVars = new SessionVars();
	}

	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void testGetCurrentDbName() {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String query = "select database()";
		String connDbName = "";
		try {
			conn = MyConnection.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next())
				connDbName = rs.getString(1);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			fail(e.getLocalizedMessage());
		}
//		System.out.println(connDbName);

		// what the xml file thinks the database name is
		// mysql converts database names to lower case
		String xmlDbName = "";
		try {
			xmlDbName = XML.readXML(MyConnection.XMLDBNAME);// .toLowerCase();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

		if (!connDbName.equals(xmlDbName))
			fail("connection database name:" + connDbName + " xml database name:" + xmlDbName);

//		String newDbName = "";
//		// try to connect to the cdm2 database
//		try {
//			MyConnection.createBasicDataSource("cdm2");
//			conn = MyConnection.getConnection();
//			st = conn.createStatement();
//			// use the same query
//			rs = st.executeQuery(query);
//			if (rs.next())
//				newDbName = rs.getString(1);
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
//		if (!newDbName.equals("cdm2"))
//			fail("expected database name of cdm2, got " + newDbName);
	}

	/*
	 * take the number of active connections to the pool max
	 */
	@Test
	public void testActiveMax() {
		Set<Connection> connectionSet = new HashSet<Connection>();
		int moreConnections = 0;
		try {
			moreConnections = MyConnection.MAXPOOLEDCONNECTIONS - MyConnection.getActiveCount();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		for (int i = 0; i < moreConnections; i++) {
			try {
				connectionSet.add(MyConnection.getConnection());
			} catch (Exception e) {
				fail("couldn't get all the connections");
			}
		}
		for (Connection c : connectionSet) {
			try {
				c.close();
			} catch (SQLException e) {
				fail(e.getLocalizedMessage());
			}
		}
	}

	/*
	 * test the creation of a database when it doesn't exist
	 */
	@Test
	public void testCreateDatabase() {
		Connection c = null;
		try {
			c=MyConnection.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
