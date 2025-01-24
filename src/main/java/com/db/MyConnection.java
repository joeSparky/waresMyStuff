package com.db;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;

import com.errorLogging.Internals;

//import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class MyConnection {
	static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String XMLDBNAME = "dbName";
	public static final String XMLDBUSER = "dbUser";
	public static final String XMLDBPASSWORD = "dbPassword";
	public static final String DBROOTNAME = "dbRootUserName";
	public static final String DBPASSWORD = "dbRootPassword";
	public static final int MAXPOOLEDCONNECTIONS = 10;

	static BasicDataSource basicDataSource = null;
//	SessionVars sVars = null;
	// String dbName = null;
//	XML xml = null;

	public MyConnection() throws Exception {
//		this.xml = xml;
		// this.dbName = xml.readXML(XMLDBNAME);
		if (basicDataSource == null)
			createBasicDataSource();
	}

	// create a BasicDataSource connection pool to the dbName database
//	protected MyConnection(SessionVars sVars, String dbName) throws Exception {
//		this.sVars = sVars;
//		this.dbName = dbName;
//		if (basicDataSource == null)
//			createBasicDataSource(dbName);
//
////		Connection myConnection = null;
////		// see if the connection works
////		try {
////			myConnection = getConnection();
////		} catch (Exception e) {
////			System.out.println("Exception:" + e.getLocalizedMessage());
////		}
////		myConnection.close();
//	}

	/**
	 * create a BasicDataSource connected to the dbName database. If a
	 * BasicDataSource was already set up, it will be closed before creating the new
	 * BasicDataSource. Be sure to put it back when done with the new databases so
	 * that the next connections go to the dbName in the XML file!
	 * 
	 * @param dbName
	 * @throws Exception
	 */
	public static synchronized void createBasicDataSource() throws Exception {
//		XML xml = new XML();
		if (basicDataSource == null) {
//			basicDataSource.close();
			basicDataSource = new BasicDataSource();
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = XML.readXML(XMLDBNAME);
			if (dbName == null)
				throw new Exception("null database name");
			url += dbName;
			String user = XML.readXML(XMLDBUSER);
			if (user == null)
				throw new Exception("null database user name");
			url += "?user=" + user;
			String password = XML.readXML(XMLDBPASSWORD);
			if (password==null)throw new Exception("null database password");
			url += "&password=" + password;
			url += "&serverTimezone=UTC";
			basicDataSource.setUrl(url);
			basicDataSource.setMaxTotal(MAXPOOLEDCONNECTIONS);
		}
	}

	public static Connection getConnection() throws Exception {
		if (MAXPOOLEDCONNECTIONS == getActiveCount()) {
			throw new Exception("pool depleted");
		}
		if (basicDataSource == null) {
			throw new Exception("null basicDataSource in MyConnection");
		}
		return basicDataSource.getConnection();
	}

	public static int getActiveCount() throws IOException, Exception {
		if (basicDataSource == null)
			createBasicDataSource();
		return basicDataSource.getNumActive();
	}

	// load the driver class
	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			Internals.logStartupError(e);
		}
	}
}
