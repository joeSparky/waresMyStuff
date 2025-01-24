package comTest.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.MyConnection;
import comTest.utilities.Utilities;

public class FindTestXMLFile {

	@Before
	public void setUp() throws Exception {
		Utilities.beforeTest();
	}
	
	@After
	public void tearDown() throws Exception {
		Utilities.afterTest();
	}

	@Test
	public void test() {
		Connection c = null;
		try {
			c = MyConnection.getConnection();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		
		try {
			c.close();
		} catch (SQLException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
