package com.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.*;

import org.w3c.dom.*;

public class XML {
	// allow the name of the database name to be overridden for testing databases
//	static String overriddenDatabaseName = null;
	static Document dom = null;
	static DocumentBuilderFactory dbf = null;
	static DocumentBuilder db = null;
	static Element doc = null;
	public static final String XMLFILENAME = "commonParams.xml";
	public static final String DISPATCHPARAMNAME = "dispatchForm";
	public static final String CSVPATH = "dcsvPath";
	private static final String DEFAULTUSERNAME = "dUserName";
	private static final String DEFAULTPASSWORD = "dPassword";
	private static Hashtable<String, String> params = null;
	static InputStream is = null;
	// SessionVars sVars = null;

//	void initXMLReader(String path) throws ParserConfigurationException, SAXException, IOException {
//		
//	}

	public static String readXML(String tag) throws Exception {
		// if we haven't initialized yet
		if (params == null) {
			String p = getCommonParamsPath();
			if (p == null)
				throw new Exception("XML path is null");
			commonInit(getCommonParamsPath());
		}
//		if (tag.equals(MyConnection.XMLDBNAME) && overriddenDatabaseName != null)
//			return overriddenDatabaseName;

		// if we've read this parameter earlier
		if (params.get(tag) != null)
			// return what we read earlier
			return params.get(tag);

		// if this is the first value we've tried to read from the xml file

//		if (dbf == null) {
//			dbf = DocumentBuilderFactory.newInstance();
//			db = dbf.newDocumentBuilder();
//
//			String xmlFileName = params.get(XMLFILENAME);
//
//			// see if the xml file exists
//			File f = new File(xmlFileName);
//			if (!f.exists())
//				throw new Exception("file y " + f.getCanonicalPath() + " does not exist");
//			if (f.isDirectory())
//				throw new Exception(XMLFILENAME + " at " + params.get(XMLFILENAME) + " is a directory.");
//
//			is = new FileInputStream(params.get(XMLFILENAME));
//			dom = db.parse(is);
//			doc = dom.getDocumentElement();
//		}
		// checkXMLSetup();

		NodeList nl = doc.getElementsByTagName(tag);
		if (nl.getLength() != 1)
			throw new Exception("tag " + tag + " has a length of " + nl.getLength() + " in " + params.get(XMLFILENAME));
		if (!nl.item(0).hasChildNodes())
			throw new Exception("tag " + tag + " has no child nodes");
		params.put(tag, nl.item(0).getFirstChild().getNodeValue());
		return params.get(tag);
	}

//	public XML(String pathToFile) throws IOException, Exception {
//		commonInit(pathToFile);
//	}

	synchronized static void commonInit(String pathToFile) throws Exception {
		if (params == null)
			params = new Hashtable<String, String>();
		else
			params.clear();

		// see if the xml file exists
		File f = new File(pathToFile);
		if (!f.exists()) {
			throw new Exception("file " + pathToFile + " does not exist");
		}
		if (f.isDirectory()) {
			throw new Exception("file " + pathToFile + " is a directory.");
		}

		is = new FileInputStream(pathToFile);
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		dom = db.parse(is);
		doc = dom.getDocumentElement();
		params.put(XMLFILENAME, pathToFile);
	}

//	protected XML(SessionVars sVars) throws Exception {
////		if (params == null)
////			params = new Hashtable<String, String>();
//		if (sVars == null)
//			throw new Exception("null sVars in XML.java");
//		if (sVars.testMode) {
//			// running in testing mode
//			commonInit(System.getProperty("user.dir") + System.getProperty("file.separator") + "test" + XMLFILENAME);
////					commonInit(tmp);
////					params.put(XMLFILENAME,tmp);
//
//		} else
//			// running in the tomcat container
//			commonInit(sVars.context.getRealPath("/") + XMLFILENAME);
//	}

//	public SmartForm getLogin(SessionVars sVars) throws Exception {
//		if (params.get(LOGINPARAMNAME) == null)
//			params.put(LOGINPARAMNAME, readXML(LOGINPARAMNAME));
//		return new RunApplication().dispatchThis(params.get(LOGINPARAMNAME), sVars);
//	}

	public static String getCSVPath() throws Exception {
		return readXML(CSVPATH);
	}

	public static String getDefaultUserName() throws Exception {
		return readXML(DEFAULTUSERNAME);
	}

	public static String getDefaultUserPassword() throws Exception {
		return readXML(DEFAULTPASSWORD);
	}

	public static String getDefaultDbName() throws Exception {
		return readXML(MyConnection.XMLDBNAME);
	}

	// path to the commonParams.xml file
	// used to select one of several xml files for testing or running under the web
	// server
	static String commonPathString = null;

	public static String getCommonParamsPath() throws Exception {
//		if (commonPathString == null)
//			throw new Exception("path to xml file not set");
		return commonPathString;
	}

	public static void setCommonParamsPath(String s) throws Exception {
		commonPathString = s;
		File file = new File(commonPathString);
		if (!file.exists()) {
			throw new Exception("xml file:" + commonPathString + " does not exist");
		}
		// restart the document builder factory on the next read
		if (params != null) {
			params.clear();
			params = null;
		}

	}
}
