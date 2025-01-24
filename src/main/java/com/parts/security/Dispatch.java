package com.parts.security;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.db.MyConnection;
import com.db.MyStatement;
import com.db.SessionVars;
import com.db.XML;
import com.errorLogging.Internals;
import com.forms.DispatchRunStuff;
import com.forms.EndOfInputException;
import com.forms.FormsArray;
import com.forms.MainPartsForm;
import com.forms.SmartForm;
import com.parts.inOut.Part;
import com.parts.location.Location;
import com.security.ExceptionCoding;
import com.security.MyObjectsArray;
import com.security.User;

import com.forms.StorageFactory;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/dispatch")
public class Dispatch extends SmartForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8430354314875902725L;

	public Dispatch() throws Exception {
		super(null, Dispatch.class.getCanonicalName());
	}

//	class MyVars extends com.forms.MyVars {
//
//		Exception dbChangeException = null;
//		boolean survived = true;
//
////		protected MyVars(SessionVars sVars) throws Exception {
////			super(sVars, Dispatch.class.getCanonicalName());
////			if (get() == null) {
////				survived = false;
//////				put();
////			}
////
////		}
//	}

	private static final String MYNAME = Dispatch.class.getCanonicalName();
	private String PARTS = MYNAME + "a";
	private String LOGOUT = MYNAME + "b";
	private String RUNDBDIAGS = MYNAME + "c";
	private String LISTSTUFF = MYNAME + "d";
	/**
	 * should match WebServlet string
	 */
	private static String MYRETURN = "dispatch";

	// public String DISPATCHID = Utils.getNextString();

	/**
	 * create a form to select the class to run
	 * 
	 * @param sVars
	 * @return
	 * @throws Exception
	 */
	@Override
	public FormsArray getForm(SessionVars sVars) throws Exception {
//		fdfdsVars.setApToRun(GOTO, this);
		FormsArray ret = new FormsArray();
		MyStorage myVars = null;
		myVars = new MyStorage().get(myVars, sVars, this.getClass().getCanonicalName());
		if (myVars.dbChangeException != null) {
			ret.errorToUser(myVars.dbChangeException);
			myVars.dbChangeException = null;
		}
		User user = new User(sVars);
		user.find(sVars.getUserNumber());
		ret.rawText(user.firstName + " " + user.lastName + "<br>");
		ret.rawText("Database:" + XML.readXML(MyConnection.XMLDBNAME) + "<br>");
//			ret.rawText("Error log:" + Internals.getLogFilePathandName() + "<br>");
//		ret.rawText(myVars.currentNode.buttonName + " menu:<br>");
		ret.submitButton("Parts", PARTS);
		ret.submitButton("Log Out", LOGOUT);
		ret.submitButton("Database Diagnostics", RUNDBDIAGS);
		ret.submitButton("List Inventory", LISTSTUFF);

//		for (DispatchRunStuff thisNode : myVars.currentNode.children) {
//			Permissions p = new Permissions();
//			p.add(Permission.ADMINISTRATOR);
//			if (thisNode.hasRunnables(p)) {
//				ret.submitButton(thisNode.buttonName, NODE_GOTO + " " + thisNode.thisId);
//				// ret.radioButton(NODE_GOTO, "" + thisNode.thisId,
//				// thisNode.buttonName, true, false);
//				// ret.newLine();
//			}
//		}
//		if (!myVars.currentNode.equals(mainBranch)) {
//			ret.submitButton("Go up one level", GOUP);
//			// ret.newLine();
//		}
		ret.setReturnTo(MYRETURN);
		// ret.hiddenField(FormsArray.HIDDEN, this.toString());
		return ret;

	}

	/**
	 * just dispatched a new class. use the just dispatched class for getForm
	 */
//	private SmartForm justDispatched = null;

	@Override
	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		MyStorage myVars = null;
		myVars = new MyStorage().get(myVars, sVars, this.getClass().getCanonicalName());

		// move to login
		try {
			DBChanges.doDBChanges(sVars);
		} catch (SQLException e) {
			myVars.dbChangeException = e;
		} catch (Exception e) {
			myVars.dbChangeException = e;
		}

		// run dispatch again by default
//		ret.nextFormToRun = this;
		if (sVars.getParameterKeys().contains(PARTS)) {
			MainPartsForm mpf = new MainPartsForm(sVars);
			ret.addAll(mpf.getForm(sVars));
			throw new EndOfInputException(ret);
		}

		if (sVars.getParameterKeys().contains(LOGOUT)) {
			sVars.logout();
			Login login = new Login();
			ret.addAll(login.getForm(sVars));
			throw new EndOfInputException(ret);
		}

		if (sVars.getParameterKeys().contains(RUNDBDIAGS)) {
			DbDiagnostics login = new DbDiagnostics();
			ret.addAll(login.getForm(sVars));
			throw new EndOfInputException(ret);
		}

		if (sVars.getParameterKeys().contains(LISTSTUFF)) {
			ret.addAll(listStuff(sVars));
			ret.addAll(getForm(sVars));
			throw new EndOfInputException(ret);
		}

		return ret;
//		throw new Exception("shouldn't get here");
	}

	FormsArray listStuff(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();

		File f = new File("list.txt");
		if (f.exists()) {
			f.delete();
			if (f.exists())
				throw new Exception("could not delete " + System.getProperty("user.dir")
						+ System.getProperty("file.separator") + "list.txt");
		}
		PrintWriter writer = null;
		Part part = new Part(sVars);
		Location location = new Location(sVars);
		PartLink pl = new PartLink(part, location, sVars);
		String query = "SELECT parentId, childId, quantity, part.name, location.name, location.id FROM "
				+ pl.getMyFileName() + ", " + part.getMyFileName() + ", " + location.getMyFileName()
				+ " WHERE parentId=part.id AND childId=location.id ORDER BY part.name";
		Connection co = null;
		MyStatement st = null;
		ResultSet rs = null;
		try {
			co = MyConnection.getConnection();
			st = new MyStatement(co);
			rs = st.executeQuery(query);
			writer = new PrintWriter("list.txt", "UTF-8");
			while (rs.next()) {
				// build a location string to the top
				Location loc = new Location(sVars);
				loc.find(rs.getInt("location.id"));
				MyObjectsArray pathToTheTop = new MyObjectsArray();
				pathToTheTop.getRecursiveParents(loc, sVars);
				String parentString = loc.getInstanceName();
				for (int i = 0; i < pathToTheTop.size(); i++) {
					parentString += "::" + pathToTheTop.get(i).getInstanceName();
				}
				writer.println(rs.getString("part.name") + ", " + parentString);
			}
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (co != null)
				co.close();
			writer.close();
		}
		ret.rawText("wrote comma separated inventory to " + System.getProperty("user.dir")
				+ System.getProperty("file.separator") + "list.txt");
		return ret;
	}

//	DispatchRunStuff currentNode = mainBranch;
	static DispatchRunStuff mainBranch = null;
	static {
		/**
		 * top of the menu
		 */
		mainBranch = null;
		try {
			mainBranch = new DispatchRunStuff("", "Main");
		} catch (ExceptionCoding e) {
			Internals.logStartupError(e);
		}
		try {
			mainBranch.addChildren(new DispatchRunStuff("com.forms.MainPartsForm", "Parts"));
		} catch (ExceptionCoding e) {
			Internals.logStartupError(e);
		}
//		DispatchRunStuff testingBranch = new DispatchRunStuff("", null, "Testing");
//		mainBranch.addChildren(
//				new DispatchRunStuff("com.parts.forms.CSVForm", Permission.USER, "print inventory"));
		try {
			mainBranch.addChildren(new DispatchRunStuff("com.parts.security.FormLogout", "Log Off"));
		} catch (ExceptionCoding e) {
			Internals.logStartupError(e);
		}
//		testingBranch.addChildren(
//				new DispatchRunStuff("com.parts.forms.ImportCDM", Permission.ADMINISTRATOR, "import CDM database"));
//		mainBranch.addChildren(testingBranch);

		try {
			DispatchRunStuff.findDuplicateClassName(mainBranch, new HashSet<String>());
			DispatchRunStuff.findDuplicateButtonName(mainBranch, new HashSet<String>());
		} catch (Exception e) {
			com.errorLogging.Internals.dumpException(e);
		}

	}

	@Override
	public FormsArray processButtons(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		ret.addAll(extractParams(sVars));
		ret.addAll(getForm(sVars));
		return ret;
	}

	class MyStorage extends StorageFactory {
		int testNumber = 7;
		Exception dbChangeException = null;
		boolean survived = true;

		@Override
		protected MyStorage getNew() {
			return new MyStorage();
		}

		@Override
		public MyStorage get(Object tc, SessionVars sVars, String unique) {
			return (MyStorage) super.get(tc, sVars, unique);
		}

	}
}
