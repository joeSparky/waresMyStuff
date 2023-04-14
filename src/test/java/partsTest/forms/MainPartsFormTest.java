package partsTest.forms;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.db.MyConnection;
import com.db.MyStatement;
import com.db.SessionVars;
import com.forms.EndOfInputRedoQueries;
import com.forms.FormsMatrixDynamic;
import com.forms.IdAndStrings;
import com.forms.MainPartsForm;
//import com.forms.MainPartsForm;
import com.forms.SearchTarget;
import com.parts.inOut.Part;
import com.security.MyLinkObject;
import com.security.MyObject;
import com.security.User;

import comTest.utilities.Utilities;

public class MainPartsFormTest {
	SessionVars sVars = null;

	@Before
	public void setUp() throws Exception {
		if (sVars == null)
			sVars = new SessionVars(true);
//		
		new Utilities().allNewTables(sVars);
		User user = new Utilities().getAUser();
		sVars.userNumber = user.id;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFormSessionVars() {
		try {
			new MainPartsForm(sVars).getForm(sVars);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testMainPartsFormSessionVars() {
		try {
			new MainPartsForm(sVars);
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				System.out.println(ste.toString());
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testFormsMatrixDynamicByRowColumn() {
		
		Part part = null;
		try {
			new MainPartsForm(sVars);
			part = new Part(sVars);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

		sVars.fmd.get(0).get(0).obj = part;

		// spin through the rows
		for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
			for (sVars.fmd.column = 0; sVars.fmd.column < sVars.fmd.get(sVars.fmd.row).size(); sVars.fmd.column++) {
				MyObject obj = sVars.fmd.getObject();
				System.out.println("row:" + sVars.fmd.row + " column:" + sVars.fmd.column + " obj:" + obj.toString()
						+ " name:" + obj.getInstanceName());
			}
		}

//		IdAndStrings idAndStrings = null;
		for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
			// ignore the company in the first column
			for (sVars.fmd.column = 1; sVars.fmd.column < sVars.fmd.get(sVars.fmd.row).size(); sVars.fmd.column++) {
				for (SearchTarget.SEARCHTYPES type : SearchTarget.SEARCHTYPES.values()) {
					try {
						System.out.println(
								"row:" + sVars.fmd.row + " column:" + sVars.fmd.column + " type:" + type.toString());
						System.out.println(
								"forward " + sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getQuery(type, 0));
						System.out.println(
								"reverse " + sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getQuery(type, 0));
						System.out.println();

						IdAndStrings ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type);
						if (ids.size() > 0)
//							System.out.print("row:" + sVars.fmd.row + " column:" + sVars.fmd.column + " type:" + type.toString());
							System.out.println(" size:" + ids.size());
						else
							System.out.println();

//						idAndStrings = new IdAndStrings(fmd, type).doQuery();
					} catch (Exception e) {
						for (StackTraceElement ele : e.getStackTrace()) {
							System.out.println(ele);
						}
//					System.out.print(e.getStackTrace());
						fail(e.getLocalizedMessage());
					}
				}
			}
		}
	}

	//@Test
	/**
	 * verify all IdAndStrings.displayState are initialized
	 */
	public void testMainPartsFormStates() {
//		MainPartsForm mpf = null;
//		String currentDbName = null;
		try {
//			currentDbName = sVars.xml.getDefaultDbName();
//				Utilities.getDbName();
			sVars.connection.createBasicDataSource("cdm2");

//			FormsMatrixDynamic fmd = null;

			// set the default user id in sVars
			sVars.userNumber = new User(sVars).getDefaultUser().id;

			MainPartsForm mpf = new MainPartsForm(sVars);
			for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
				// ignore the company in the first column
				for (sVars.fmd.column = 1; sVars.fmd.column < sVars.fmd.get(sVars.fmd.row).size(); sVars.fmd.column++) {
					for (SearchTarget.SEARCHTYPES type : SearchTarget.SEARCHTYPES.values()) {
						if (!sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).giveMeAListButton)
							fail("row:" + sVars.fmd.row + " column:" + sVars.fmd.column + " type:" + type.toString()
									+ " giveMeAListButton is false");
					}
				}
			}
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				System.out.println(ste);
			fail(e.getLocalizedMessage());
		}
		try {
			sVars.connection.createBasicDataSource(MyConnection.XMLDBNAME);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}

	}

//	@Test
	/**
	 * exercise all queries
	 */
	public void testAllQueries() {
		try {
			sVars.connection.createBasicDataSource("cdm2");
			sVars.userNumber = new User(sVars).getDefaultUser().id;
			// have MainPartsForm create a new FormsMatrixDynamic in sVars
			new MainPartsForm(sVars);
			if (sVars.fmd.size() == 0)
				fail("empty " + FormsMatrixDynamic.class.getCanonicalName());
			for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
				// ignore the company in the first column
				for (sVars.fmd.column = 1; sVars.fmd.column < sVars.fmd.get(sVars.fmd.row).size(); sVars.fmd.column++) {
					for (SearchTarget.SEARCHTYPES type : SearchTarget.SEARCHTYPES.values()) {
						{
							// fake the user requesting a query
							String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
									.getIdAndStrings(type).new MyVars(sVars).REQUESTALIST;
							// clear all previous buttons we've inserted
							sVars.parameterMap.clear();
							sVars.parameterMap.put(buttonName, null);

							boolean endOfInput = false;
							try {
								sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
							} catch (EndOfInputRedoQueries e) {
								endOfInput = true;
							}
							if (!endOfInput)
								fail("extractParams failed");
						}
						IdAndStrings ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type)
								.doQuery();
						if (ids.isEmpty()) {
							System.out.println(
									"empty ids for " + sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							continue;
						}
						while (ids.size() == IdAndStrings.DISPLAYSIZE) {
							// ask for more
							System.out.println(
									"full ids for " + sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							{
								// fake the user requesting a query
								String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
										.getIdAndStrings(type).new MyVars(sVars).NEXT;
								// clear all previous buttons we've inserted
								sVars.parameterMap.clear();
								sVars.parameterMap.put(buttonName, null);
								boolean endOfInput = false;
								try {
									sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
								} catch (EndOfInputRedoQueries e) {
									endOfInput = true;
								}
								if (!endOfInput)
									fail("extractParams failed");
							}
							ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).doQuery();
						}
						if (!ids.isEmpty()) {
							System.out.println(ids.size() + " ids for " + sVars.fmd.getObject().getLogicalName()
									+ " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
						}

						// all of the empty responses were continued above so at this point, we have
						// non-empty queries.
						// at this point, each remaining IdAndString query responded and should be at
						// its end. unwind them back to the beginning

						// fake a PREVIOUS button

						{
							// fake the user requesting a query
							String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
									.getIdAndStrings(type).new MyVars(sVars).PREVIOUS;
							// clear all previous buttons we've inserted
							sVars.parameterMap.clear();
							sVars.parameterMap.put(buttonName, null);
							boolean endOfInput = false;
							try {
								sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
							} catch (EndOfInputRedoQueries e) {
								endOfInput = true;
							}
							if (!endOfInput)
								fail("extractParams failed");
						}
						do {
							ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).doQuery();
							System.out.println("previous " + ids.size() + " ids for "
									+ sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							{
								// fake the user requesting a query
								String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
										.getIdAndStrings(type).new MyVars(sVars).PREVIOUS;
								// clear all previous buttons we've inserted
								sVars.parameterMap.clear();
								sVars.parameterMap.put(buttonName, null);
								boolean endOfInput = false;
								try {
									sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
								} catch (EndOfInputRedoQueries e) {
									endOfInput = true;
								}
								if (!endOfInput)
									fail("extractParams failed");
							}
						} while (sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
								.getIdAndStrings(type).so.lastOffset > 0);

					}
				}
			}
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				System.out.println(ste);
			fail(e.getLocalizedMessage());
		}
		try {
			sVars.connection.createBasicDataSource(MyConnection.XMLDBNAME);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}

//	@Test
	/**
	 * exercise all queries with a part selected
	 */
	public void testAllQueriesPartSelected() {
//		String currentDbName =null;
		try {
//			currentDbName = sVars.xml.getDefaultDbName();
			sVars.connection.createBasicDataSource("cdm2");

//			FormsMatrixDynamic fmd = null;
			Part part = null;

			// set the default user id in sVars
			sVars.userNumber = new User(sVars).getDefaultUser().id;
			part = new Part(sVars);
			new MainPartsForm(sVars);

			// find a part object in the first row
			sVars.fmd.row = 0;
			for (sVars.fmd.column = 0; sVars.fmd.column < sVars.fmd.get(0).size(); sVars.fmd.column++) {
				if (sVars.fmd.get(0).get(sVars.fmd.column).obj.getMyFileName().equals(part.getMyFileName())) {

					// get any part in the database
					Connection con = sVars.connection.getConnection();
					MyStatement st = new MyStatement(con);
					ResultSet rs = st.executeQuery("select id from " + part.getMyFileName() + " limit 1");
					int partId = -1;
					if (rs.next()) {
						partId = rs.getInt(1);
					} else {
						rs.close();
						st.close();
						con.close();
						break;
					}
					rs.close();
					st.close();
					con.close();
					part.find(partId);
					break;
				}
			}
//			if (!part.isLoaded())
//				fail("part not found");

			for (sVars.fmd.row = 0; sVars.fmd.row < sVars.fmd.getNumberOfRows(); sVars.fmd.row++) {
				// ignore the company in the first column
				for (sVars.fmd.column = 1; sVars.fmd.column < sVars.fmd.get(sVars.fmd.row).size(); sVars.fmd.column++) {
					for (SearchTarget.SEARCHTYPES type : SearchTarget.SEARCHTYPES.values()) {
						{
							// fake the user requesting a query
							String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
									.getIdAndStrings(type).new MyVars(sVars).REQUESTALIST;
							// clear all previous buttons we've inserted
							sVars.parameterMap.clear();
							sVars.parameterMap.put(buttonName, null);

							boolean endOfInput = false;
							try {
								sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
							} catch (EndOfInputRedoQueries e) {
								endOfInput = true;
							}
							if (!endOfInput)
								fail("extractParams failed");
						}
						IdAndStrings ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type)
								.doQuery();
						if (ids.isEmpty()) {
							System.out.println(
									"empty ids for " + sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							continue;
						}
						while (ids.size() == IdAndStrings.DISPLAYSIZE) {
							// ask for more
							System.out.println(
									"full ids for " + sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							{
								// fake the user requesting a query
								String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
										.getIdAndStrings(type).new MyVars(sVars).NEXT;
								// clear all previous buttons we've inserted
								sVars.parameterMap.clear();
								sVars.parameterMap.put(buttonName, null);
								boolean endOfInput = false;
								try {
									sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
								} catch (EndOfInputRedoQueries e) {
									endOfInput = true;
								}
								if (!endOfInput)
									fail("extractParams failed");
							}
							ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).doQuery();
						}
						if (!ids.isEmpty()) {
							System.out.println(ids.size() + " ids for " + sVars.fmd.getObject().getLogicalName()
									+ " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
						}

						// all of the empty responses were continued above so at this point, we have
						// non-empty queries.
						// at this point, each remaining IdAndString query responded and should be at
						// its end. unwind them back to the beginning

						// fake a PREVIOUS button

						{
							// fake the user requesting a query
							String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
									.getIdAndStrings(type).new MyVars(sVars).PREVIOUS;
							// clear all previous buttons we've inserted
							sVars.parameterMap.clear();
							sVars.parameterMap.put(buttonName, null);
							boolean endOfInput = false;
							try {
								sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
							} catch (EndOfInputRedoQueries e) {
								endOfInput = true;
							}
							if (!endOfInput)
								fail("extractParams failed");
						}
						do {
							ids = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).doQuery();
							System.out.println("previous " + ids.size() + " ids for "
									+ sVars.fmd.getObject().getLogicalName() + " type:" + type);
//							System.out.println(" firstDislayedRecord:"
//									+ sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).getIdAndStrings(type).firstDisplayedRecord);
							{
								// fake the user requesting a query
								String buttonName = sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
										.getIdAndStrings(type).new MyVars(sVars).PREVIOUS;
								// clear all previous buttons we've inserted
								sVars.parameterMap.clear();
								sVars.parameterMap.put(buttonName, null);
								boolean endOfInput = false;
								try {
									sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column).extractParams(sVars);
								} catch (EndOfInputRedoQueries e) {
									endOfInput = true;
								}
								if (!endOfInput)
									fail("extractParams failed");
							}
						} while (sVars.fmd.get(sVars.fmd.row).get(sVars.fmd.column)
								.getIdAndStrings(type).so.lastOffset > 0);

					}
				}
			}
		} catch (Exception e) {
			for (StackTraceElement ste : e.getStackTrace())
				System.out.println(ste);
			fail(e.getLocalizedMessage());
		}
		try {
			sVars.connection.createBasicDataSource(MyConnection.XMLDBNAME);
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
