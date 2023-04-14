package comTest.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

import com.db.SessionVars;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.security.MyLinkObject;

public class sanityTest {

	/**
	 * for each location, list every part stored there
	 */
	@Test
	public void partLocationtest() {
		SessionVars sVars = null;
		Connection connection = null;
		Connection innerConnection = null;
		Statement st = null;
		Statement innerStatement = null;
		ResultSet rs = null;
		ResultSet innerRs = null;
		Location location = null;
		Part part = null;
		MyLinkObject mlo = null;
		try {
			sVars = new SessionVars(true);
			sVars.connection.createBasicDataSource("cdm2");
			location = new Location(sVars);
			part = new Part(sVars);
			mlo = new MyLinkObject(part, location, sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// look for an infinite loop in the locations tree
		try {
			location = new Location(sVars);
			connection = sVars.connection.getConnection();
			innerConnection = sVars.connection.getConnection();
			st = connection.createStatement();
			innerStatement = innerConnection.createStatement();
			String query = "select id from " + location.getMyFileName();
			rs = st.executeQuery(query);
			while (rs.next()) {
				int locationId = rs.getInt("id");
				String innerQuery = "select parentId, childId from " + mlo.getMyFileName() + " where childId='"
						+ locationId + "'";
				innerRs = innerStatement.executeQuery(innerQuery);
				while (innerRs.next()) {
					int partId = innerRs.getInt("parentId");
					int innerLocationId = innerRs.getInt("childId");
					if (locationId != innerLocationId)
						fail("query failure, outer locationId=" + locationId + ", inner locationId=" + innerLocationId);
					part.find(partId);
					location.find(locationId);
					System.out.println(part.getInstanceName() + " at " + location.getInstanceName());
				}
			}
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

	}

	/**
	 * for each location, list its highest parent
	 */
	@Test
	public void orphanLocationtest() {
		SessionVars sVars = null;
		Connection connection = null;
		Connection innerConnection = null;
		Statement st = null;
		Statement innerStatement = null;
		ResultSet rs = null;
		ResultSet innerRs = null;
		Location location = null;
//		Part part = null;
		MyLinkObject mlo = null;
		try {
			sVars = new SessionVars(true);
			sVars.connection.createBasicDataSource("cdm2");
			location = new Location(sVars);
//			part = new Part(sVars);
			mlo = new MyLinkObject(location, location, sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		// look for an infinite loop in the locations tree
		try {
			location = new Location(sVars);
			connection = sVars.connection.getConnection();
			innerConnection = sVars.connection.getConnection();
			st = connection.createStatement();
			innerStatement = innerConnection.createStatement();
			String query = "select id from " + location.getMyFileName();
			rs = st.executeQuery(query);
			while (rs.next()) {
				int locationId = rs.getInt("id");
				location.find(locationId);
//				System.out.println("working on location id " + locationId + " `" + location.getInstanceName() + "`");
				String innerQuery = "select parentId, childId from " + mlo.getMyFileName() + " where childId='"
						+ locationId + "'";
				innerRs = innerStatement.executeQuery(innerQuery);
				boolean linkFound = false;
				while (innerRs.next()) {
					int parentId = innerRs.getInt("parentId");
					location.find(parentId);
//					System.out.println("parent location " + location.getInstanceName());
					linkFound = true;
				}
				if (!linkFound)
					System.out.println("orphan location " + location.getInstanceName());
			}
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

	}

}
