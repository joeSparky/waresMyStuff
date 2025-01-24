package com.forms;

import java.io.IOException;
import java.io.PrintWriter;

import com.db.SessionVars;
import com.db.XML;
import com.errorLogging.Internals;
import com.parts.security.Login;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * base class for the applications interfacing with tomcat
 * 
 * @author joe
 * 
 */
public abstract class SmartForm extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String uniqueName = null;

	// From HttpServlet constructor, align subclass constructor with SmartForm
	protected SmartForm(SessionVars sVars, String uniqueName) {
		this.uniqueName = uniqueName;
	}

	// subclass template
	public FormsArray processButtons(SessionVars sVars) throws Exception {
		return new FormsArray();
	}

	public FormsArray extractParams(SessionVars sVars) throws Exception {
		return extractParams(sVars, uniqueName);
	}

	/**
	 * general form
	 */
	public FormsArray getForm(SessionVars sVars) throws Exception {
		return getForm(sVars, uniqueName);
	}

	public String handleNull(String str) {
		if (str == null)
			return "";
		// leading space for the remainder of the title
		return str + " ";
	}

	public void syncTarget() throws Exception {
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doBoth(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doBoth(request, response);
	}

	protected void doBoth(HttpServletRequest request, HttpServletResponse response) {
		FormsArray ret = new FormsArray();
		SessionVars sVars = null;
		response.setContentType("text/html");
		HttpSession session = null;
		ServletContext context = getServletContext();

		// get the session
		try {
			session = request.getSession(true);
		} catch (Exception e) {
			bestOutputEffort(response, e.getLocalizedMessage(), new FormsArray());
			return;
		}
		if (session == null) {
			bestOutputEffort(response, "null session", new FormsArray());
			return;
		}

		// try to get a previous sVars
		sVars = (SessionVars) session.getAttribute(SessionVars.SESSIONATTRIBUTE);

		/**
		 * if there's no SessionVars already stored in the session
		 */
		if (sVars == null) {
			try {
				// start a new sVars.
				sVars = new SessionVars(request, response, context, session);
				// save sVars in the session variables
				session.setAttribute(SessionVars.SESSIONATTRIBUTE, sVars);
			} catch (Exception e) {
				bestOutputEffort(response, e.getLocalizedMessage(), new FormsArray());
				return;
			}
		} else {
			try {
				// we have a previous sVars. Update it with the latest
				sVars.update(request, response, context, session);
			} catch (Exception e) {
				bestOutputEffort(response, e.getLocalizedMessage(), new FormsArray());
				return;
			}
		}
		String s = null;
		try {
			s = XML.getCommonParamsPath();

		} catch (Exception e) {
			ret.errorToUser(e);
		}

		// set up the xml path

		if (s == null) {
			try {
//				System.out.println("setting path to:" + context.getRealPath(XML.XMLFILENAME));
				XML.setCommonParamsPath(context.getRealPath(XML.XMLFILENAME));				
			} catch (Exception e) {
				ret.errorToUser(e);
			}

		} 
//		else
//			try {
//				System.out.println("existing path:" + XML.getCommonParamsPath());
//			} catch (Exception e) {
//				ret.errorToUser(e);
//			}

		// dump the class loader info
//		DumpLoader.dumpLoader();

//		// strip the input parameters of junk
		sVars.extractInputParams(request);
		SmartForm runThis = null;

		// if (we are not logged in) AND this instance is
		// not the login class)
		if (!sVars.isLoggedIn() && !(this instanceof Login))
			try {
				runThis = new Login(sVars);
			} catch (Exception e) {
				ret.errorToUser(e);
			}
		else
			// run this class
			runThis = this;

		boolean keepGoing = false;
		FormsArray passedThroughException = null;
		try {
			ret.addAll(runThis.processButtons(sVars));
		} catch (EndOfInputException e) {
			keepGoing = true;
			passedThroughException = e.fa;
		} catch (Exception e) {
			ret.errorToUser(e);
		}
		if (keepGoing) {
			try {
				ret.addAll(passedThroughException);
			} catch (Exception e) {
				bestOutputEffort(response, "best output effort", ret);
				return;
			}
			ret.executeForm(sVars);
			return;
		}
		bestOutputEffort(response, "best output effort", ret);
//		try {
//			ret.addAll(extractParams(sVars));
//			ret.addAll(getForm(sVars));
//		} catch (Exception e) {
//			Internals.logStartupError(e);
//		}

//		ret.executeForm(sVars, sVars.request, sVars.response, sVars.context);
		// if tVars.context = getServletContext();

		// new FormToRun().whichForm(sVars, newSession);

//		if (sVars.ret == null) {
//			sVars.ret = new FormsArray();
//			sVars.ret.errorToUser("3/11/2022" + FormsArray.lineSeparator);
//		}
//		if (newSession)
//			sVars.ret.errorToUser("new session " + FormsArray.lineSeparator);

//		if (sVars.threadAlreadyRunning) {
////			Internals.logString("thread already running");
//			sVars.ret.errorToUser("Second thread attempted.");
//			sVars.threadAlreadyRunning = false;
//			return;
//		} else
//			sVars.threadAlreadyRunning = true;

//		Internals.logWithDate();

//		if (Internals.startupError())
//			sVars.ret.errorToUser(Internals.getStartupError());

//		XML xml = null;
//		SmartForm login = null;
		// get the form associated with the input or null
//		SmartForm sf = sVars.getApToRun();

//		Exception ex = null;
//		try {
////			xml = 
//			// let parts override
//			login = sVars.getLoginForm();
////			login = xml.getLogin(null, sVars);
//		} catch (Exception e) {
//			// save the exception for later
//			ex = e;
//		}
//		if (ex != null) {
//			// we can't run
//			// bestOutputEffort(login, ex);
//			bestOutputEffort(response);
//			sVars.threadCount--;
//			return;
//		}

		// if the user is not logged in or the input did not have an id for the
		// application to run
//		if (!sVars.isLoggedIn() || sf == null) {
//			SmartForm login = null;
//			try {
//				login = sVars.getLoginForm();
//			} catch (Exception e) {
//				// should have been caught when the servlet first ran
//				Internals.logStartupError(e);
//			}
//			processInput(login, sVars);
//			sVars.threadCount--;
//			return;

//		try {
//			sf = sVars.getApToRun(returnTo);
//		} catch (Exception e) {
//		}
//		if (sf == null) {
//			backToLogin(sVars.ret, login);
//			sVars.threadAlreadyRunning = false;
//			return;
//		}
//
//		// if not logged in
//		if (!sVars.isLoggedIn()) {
//			processInput(login);
//			sVars.threadAlreadyRunning = false;
//			return;
//		}
//		Internals.logWithDate();

//		processInput(sf, sVars);
//		sVars.threadCount--;
//
//	}
//
//	private void processInput(SmartForm thisOne, SessionVars sVars) {

//		if (thisOne == null)
//			ret.errorToUser(Internals.dumpExceptionToString(new Exception("null thisOne")));
//		if (sVars == null)
//			ret.errorToUser(Internals.dumpExceptionToString(new Exception("null sVars")));
//		if (sVars.request == null)
//			ret.errorToUser(Internals.dumpExceptionToString(new Exception("null sVars.request")));
//		if (sVars.response == null)
//			ret.errorToUser(Internals.dumpExceptionToString(new Exception("null sVars.response")));
//		if (sVars.context == null)
//			ret.errorToUser(Internals.dumpExceptionToString(new Exception("null sVars.context")));
//
//		try {
//			ret.addAll(processButtons(sVars));
//		} catch (Exception e) {
//			ret.errorToUser(e);
//		}
////		ret.errorToUser("inCount:" + inCount + " outCount:" + outCount);
//
//		ret.executeForm(sVars, sVars.request, sVars.response, sVars.context);
	}

	public void bestOutputEffort(HttpServletResponse response, String errorString, FormsArray ret) {
//		FormsArray ret = new FormsArray();
		// get startup errors
		ret.errorToUser(Internals.getStartupError());
		// get the error passed to us
		if (!errorString.isEmpty())
			ret.errorToUser(errorString);
//		ret.errorToUser("inCount:" + inCount + " outCount:" + outCount);
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// try to dump the exception the next time we run
			Internals.logStartupError(e);
		}
		out.print(ret.generateHTML());
		out.flush();

//		try {
//			blankForm = new BlankForm(new FormsMatrixDynamic(sVars), sVars);
//			blankForm.setErrorToUser(e);
//			blankForm.processButtons(sVars);
//			FormsArray ret = new FormsArray();
//			ret.addAll(blankForm.processButtons(sVars));
//			if (Internals.startupError())
//				ret.errorToUser(Internals.getStartupError());
//			ret.executeForm(sVars, sVars.request, sVars.response, sVars.context);
//		} catch (Exception e1) {
//			// we can't get to the user. try dumping the failure to the log.
//			Internals.logStartupError(e1);
//		}
	}

	public void formInit() throws Exception {

	}

	/**
	 * manipulations on a loaded object
	 */
	public FormsArray getForm(SessionVars sVars, String uniqueName) throws Exception {
		return null;
	}

	public FormsArray extractParams(SessionVars sVars, String uniqueName) throws Exception {
		return null;
	}
}
