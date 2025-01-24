package com.parts.security;

import com.db.SessionVars;
import com.db.XML;
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
@WebServlet("/login")
public class Login extends SmartForm {
	public Login(SessionVars sVars) {
		super(sVars, Login.class.getCanonicalName());
	}

	// for http
	public Login() {
		super(null, Login.class.getCanonicalName());
	}

	@Override
	public void init() {

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5238017179398755052L;
	private static final String MYNAME = Login.class.getCanonicalName();
	private static final String PASSWORD = MYNAME + "a";
//	private static final String USER = MYNAME + "b";
	private static final String LOGIN = MYNAME + "c";
	/**
	 * should match webServlet string
	 */
	private static final String MYRETURNTO = "login";

	public FormsArray extractParams(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
//		if (!sVars.hasParameterKey(USER)) {
//			ret.addAll(getNextForm(sVars));
//			throw new EndOfInputException(ret);
//		}
		if (!sVars.hasParameterKey(PASSWORD)) {
			ret.addAll(getNextForm(sVars));
			throw new EndOfInputException(ret);
		}
		if (XML.readXML(XML.ADMINPASSWORD).equals(sVars.getParameterValue(PASSWORD))) {
			sVars.admin = true;
			ret.addAll(getNextForm(sVars));
			throw new EndOfInputException(ret);
		}
		if (XML.readXML(XML.READONLYPASSWORD).equals(sVars.getParameterValue(PASSWORD))) {
			sVars.readOnly = true;
			ret.addAll(getNextForm(sVars));
			throw new EndOfInputException(ret);
		}
		
//		try {
//			sVars.userNumber = user.isValidUser(sVars.getParameterValue(USER), sVars.getParameterValue(PASSWORD)).id;
//		} catch (ExceptionContinue e) {
//			ret.body = e.getLocalizedMessage();
//		} catch (Exception e) {
//			ret.errorToUser(e);
//		}

		ret.addAll(getNextForm(sVars));
		throw new EndOfInputException(ret);
	}

	FormsArray getNextForm(SessionVars sVars) throws Exception {
		FormsArray ret = new FormsArray();
		if (sVars.admin || sVars.readOnly)
			ret.addAll(new Dispatch().getForm(sVars));
		else
			ret.addAll(getForm(sVars));
		return ret;
	}

	/**
	 * create form to collect user name and password
	 */
	public FormsArray getForm(SessionVars sVars) throws ExceptionCoding, Exception {
		FormsArray ret = new FormsArray();
		ret.setReturnTo(MYRETURNTO);
		ret.startTable();
//		ret.startRow();
//		ret.textBox(USER, User.USERNAMELENGTH, "User Name", "", true, false);
//		ret.endRow();
		ret.startRow();
		ret.passwordBox(PASSWORD, 20, "Password", "", false);
		ret.endRow();
		ret.endTable();
		ret.submitButton("Log In", LOGIN);
		ret.newLine();
		ret.link("Help (Open in a new window.)<br>", "help.htm");
		ret.newLine();
		return ret;
	}

	@Override
	public FormsArray processButtons(SessionVars sVars) throws Exception {
		return extractParams(sVars);
	}
}
