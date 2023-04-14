package com.parts.security;

import com.db.SessionVars;
import com.forms.EndOfInputException;
import com.forms.FormsArray;
import com.forms.SmartForm;
import com.security.ExceptionCoding;

import jakarta.servlet.annotation.WebServlet;

/**
 * walk the user through the login process.
 * 
 * @author joe
 * 
 */
@WebServlet("/dbdiagnostics")
public class DbDiagnostics extends SmartForm {
	public DbDiagnostics(SessionVars sVars) {
		super(sVars, DbDiagnostics.class.getCanonicalName());
	}

	// for http
	public DbDiagnostics() {
		super(null, DbDiagnostics.class.getCanonicalName());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5238017179398755052L;
	private static final String MYNAME = DbDiagnostics.class.getCanonicalName();
	private static final String GO = MYNAME + "a";
	private static final String STOP = MYNAME + "b";
	/**
	 * should match webServlet string
	 */
	private static final String MYRETURNTO = "dbdiagnostics";

	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		if (sVars.hasParameterKey(GO)) {
			ret.addAll(doDiagnostics(sVars));
			throw new EndOfInputException(ret);
		}
		if (sVars.hasParameterKey(STOP)) {
			ret.addAll(new Dispatch().getForm(sVars));
			throw new EndOfInputException(ret);
		}
		return ret;
	}

	FormsArray doDiagnostics(SessionVars sVars) throws ExceptionCoding, Exception {
		FormsArray ret = new FormsArray();
		ret.rawText("ran diagnostics");
		ret.addAll(getForm(sVars));
		return ret;
	}

	/**
	 * create form to collect user name and password
	 */
	public FormsArray getForm(SessionVars sVars) throws ExceptionCoding, Exception {
		FormsArray ret = new FormsArray();
		ret.setReturnTo(MYRETURNTO);

		sVars.xml.getDefaultDbName();
		ret.submitButton("Run diagnostics on the database " + sVars.xml.getDefaultDbName(), GO);
//		ret.newLine();
		ret.rawText("This may take several minutes.");
		ret.newLine();
		ret.submitButton("Back to the dispatch screen", STOP);
		return ret;
	}

	@Override
	public FormsArray processButtons(SessionVars sVars) throws Exception {
		return extractParams(sVars);
	}
}
