package com.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.forms.FormsMatrixDynamic;
import com.forms.SelectAndEditForm;
import com.forms.SmartForm;
import com.security.ExceptionCoding;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SessionVars {
	public HashMap<String, String[]> parameterMap;
	HttpServletRequest request;
	HttpServletResponse response;
	public static final String SESSIONATTRIBUTE = "sessionVariables";
	public ServletContext context;
	public HttpSession session = null;
	public FormsMatrixDynamic fmd = null;
	public SelectAndEditForm se = null;
	public String buttonName = null;
	// has administrative privileges (add and delete parts, add and delete
	// locations)
	public boolean admin = false;
	// has read only privileges (put parts into inventory, remove parts from
	// inventory)
	public boolean readOnly = false;

	public SessionVars() {
	}

//	public SessionVars(boolean noLongerUsed) {}
	/**
	 * the first time the server runs. Used to verify the connection to the database
	 * when the program starts.
	 */
	public boolean firstTime = true;

	/**
	 * while running in the tomcat container
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void setServletContext(ServletContext context) throws Exception {
		this.context = context;
	}

	/**
	 * during testing (ServletContext context == null)
	 * 
	 * @throws Exception
	 */
//	public SessionVars() throws Exception {
//		clear();
//	}

	public void update(HttpServletRequest request, HttpServletResponse response, ServletContext context,
			HttpSession session) throws Exception {
		this.request = request;
		this.response = response;
		this.context = context;
		this.session = session;
	}

	public SessionVars(HttpServletRequest request, HttpServletResponse response, ServletContext context,
			HttpSession session) throws Exception {
		this.request = request;
		this.response = response;
		this.context = context;
		this.session = session;
		this.response.setContentType("text/html");
//		setUpSession(request, response);
//		if (xml == null) {
//			String path = context.getRealPath("/WEB-INF");
//			String path = context.getRealPath(XML.XMLFILENAME);
//			path +=  System.getProperty("file.separator") + XML.XMLFILENAME;
		// path = System.getProperty("user.dir") + System.getProperty("file.separator")
		// + XML.XMLFILENAME;
//			xml = new XML(path);
//		}
	}

	/**
	 * allow testing when not running in the tomcat container
	 * 
	 * @param testMode
	 * @throws Exception
	 */
//	public SessionVars(boolean testMode) throws Exception {
////		this.testMode = testMode;
////		XML xml = null;
//		if (testMode)
//			xml = new XML(
//					System.getProperty("user.dir") + System.getProperty("file.separator") + "test" + XML.XMLFILENAME);
//		else
//			xml = new XML(System.getProperty("user.dir") + System.getProperty("file.separator") + XML.XMLFILENAME);
//		clear(xml);
//	}
//
//	public SessionVars(XML xml) {
//		this.xml = xml;
//	}

	/**
	 * fake out the HTTPServlet functions
	 */
	// boolean testMode = false;

	public void clear() throws Exception {
//		synchronized (this) {
//			if (xml == null)
//				xml = new XML(this);
//		}
//		synchronized (this) {
//
//			new MyConnection(xml);
//		}
//		testSessionVariables.clear();

		parameterMap = new HashMap<String, String[]>();
		buttonName = null;
	}

	public boolean isLoggedIn() {
//		return true;
		return readOnly || admin;
	}

	/**
	 * mark the user as logged out
	 */
	public void logout() {
		readOnly = false;
		admin = false;
	}

	public void clearLogin() {
		logout();
	}

	public String stripped(String stripThis) {
		stripThis = stripThis.replace("'", "");
		stripThis = stripThis.replace("\"", "");
		stripThis = stripThis.replace("<", "");
		stripThis = stripThis.replace(">", "");
		return stripThis;
	}

	private String[] strippedArray(String[] stripThis) {
		for (int i = 0; i < stripThis.length; i++) {
			stripThis[i] = stripped(stripThis[i]);
		}
		return stripThis;
	}

	/**
	 * strip the input parameters
	 */
	public void extractInputParams(HttpServletRequest request) {
		Map<String, String[]> tmp = request.getParameterMap();
		Set<String> keys = tmp.keySet();
		// replace the dirty keys with clean keys
		parameterMap = new HashMap<String, String[]>();
		for (String thisKey : keys) {
			parameterMap.put(stripped(thisKey), strippedArray(tmp.get(thisKey)));
		}
	}

	public HashMap<String, String[]> getParameterMap() {
		return parameterMap;
	}

	/**
	 * clear the parameter map so that there will not be any more values processed
	 */
	public void clearParameterMap() {
		parameterMap.clear();
	}

	public String[] getParameterValues(String str) {
		return parameterMap.get(str);
	}

	/**
	 * return the value of the parameter or a null if the parameter was not set
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public String getParameterValue(String str) throws Exception {
		if (parameterMap.containsKey(str))
			return parameterMap.get(str)[0];
		throw new Exception("String " + str + " is not in parameterMap " + parameterMap.toString());

	}

	public boolean hasParameterKey(String str) {
		return parameterMap.keySet().contains(str);
	}

	public String getParamterValueException(String str) throws Exception {
		if (hasParameterKey(str))
			return getParameterValue(str);
		else
			throw new Exception(str + " not found.");

	}

	/**
	 * if a key name and key value are in the input parameters
	 * 
	 * @param keyName
	 * @param keyValue
	 * @return
	 */
	public boolean keyNameValue(String keyName, String keyValue) {
		if (parameterMap.containsKey(keyName) && parameterMap.get(keyName)[0].equals(keyValue))
			return true;
		return false;
	}

	public Set<String> getParameterKeys() {
		return parameterMap.keySet();
	}

	public LocalDateTime created = LocalDateTime.now();
	/**
	 * detect multiple inputs on a session before the container can respond
	 */
	public int threadCount = 0;

//	public SmartForm getDispatch() throws Exception {
//		return xml.readXML(XML.DISPATCHPARAMNAME);
//		return xml.getDispatch(this);
//	}

	public SmartForm getDispatch() throws Exception {
		String tmp = XML.readXML(XML.DISPATCHPARAMNAME);
		return dispatchThis(tmp, this);
	}

	public SmartForm dispatchThis(String className, SessionVars sVars)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ClassNotFoundException {

		Class<?> runClass = null;
//		try {
		runClass = this.getClass().getClassLoader().loadClass(className);
//		} catch (ClassNotFoundException e) {
//			Internals.dumpExceptionExit(e);
//			// during testing, dumpExceptionExit returns
//			return null;
//		}
		SmartForm ret = null;

		// constructor with sVars argument
		Constructor<?> constructor = runClass.getDeclaredConstructor(SessionVars.class);
		ret = (SmartForm) constructor.newInstance(sVars);
		return ret;
	}

//	public String getSeparator() throws Exception {
//		return xml.getSeparator(this);
//	}

	public String getSeparator() {
		if (this.context != null)
			return "/";
		else
			return System.getProperty("file.separator");
	}

	public String getCSVPATH() throws Exception {
		return XML.getCSVPath();
	}

	public String getDefaultUserName() throws Exception {
		return XML.getDefaultUserName();
	}

	public String getDefaultUserPassword() throws Exception {
		return XML.getDefaultUserPassword();
	}

	/**
	 * get my local variables from the session. If my local variables are not in the
	 * session, use initialMyVars as the initial entry in the session.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getMyVars(String attribute) throws Exception {
		if (session == null) {
			if (testSessionVariables.containsKey(attribute))
				return testSessionVariables.get(attribute);
			else
				throw new Exception("testsSessionVariables doesn't have " + attribute);
		} else
			return session.getAttribute(attribute);
	}

	public boolean hasMyVars(String attribute) {
		if (session == null)
			return testSessionVariables.containsKey(attribute);
		else
			return session.getAttribute(attribute) != null;
	}

	public void putMyVars(String attribute, Object initialMyVars) throws Exception {
		if (initialMyVars == null)
			throw new ExceptionCoding("null initialMyVars");
		if (session == null)
			testSessionVariables.put(attribute, initialMyVars);
		else
			session.setAttribute(attribute, initialMyVars);
	}

	/**
	 * when running in test mode (no tomcat), store the session variables here
	 */
	public HashMap<String, Object> testSessionVariables = new HashMap<String, Object>();

	/**
	 * if sVars has already been stored in the session, return it. Otherwise, create
	 * a template in the session and return the empty template.
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
//	public SessionVars setUpSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		SessionVars tmp = null;
////		response.setContentType("text/html");
//		// if this is the first time we've run in this session
//		if (this.request == null) {
//			// set this up for getMyVars to run
//			this.request = request;
//			this.response = response;
//			// get the original or create a new one if there was no original
//			tmp = (SessionVars) getMyVars(SESSIONATTRIBUTE, this);
//			// save the request and response in whatever we got back
//			tmp.request = request;
//			tmp.response = response;
//
//			// throw an exception when we start a session if we can't connect to the
//			// database
//			connection.getConnection();
//			extractInputParams(request);
//			return tmp;
//		} else {
//			// set this up for getMyVars to run
//			this.request = request;
//			this.response = response;
//			// get the original or create a new one if there was no original
//			tmp = (SessionVars) getMyVars(SESSIONATTRIBUTE, this);
//			// save the request and response in whatever we got back
//			tmp.request = request;
//			tmp.response = response;
//			extractInputParams(request);
//			return tmp;
//		}
//	}

	public PrintWriter getPrintWriter() throws IOException {
		return response.getWriter();
	}

//	public boolean areMyVars(String attribute) throws Exception {
////		if (request == null)
////			throw new Exception("null request");
////		HttpSession session = request.getSession(false);
////		if (session == null)
////			throw new Exception("null session");
////		Object object = session.getAttribute(attribute);
//		return session.getAttribute(attribute) != null;
//	}

//	/**
//	 * set my local variables in the session
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	void setMyVars(String attribute, Object vars) throws Exception {
//		if (request == null)
//			throw new Exception("null request");
//		HttpSession session = request.getSession(false);
//		if (session == null)
//			throw new Exception("null session");
//		session.setAttribute(attribute, vars);
//	}

	public void backToOrigin(String whatToShow) throws ServletException, IOException {
		session.invalidate();
		RequestDispatcher dispatcher = context.getRequestDispatcher(whatToShow);
		dispatcher.forward(request, response);
	}
}
