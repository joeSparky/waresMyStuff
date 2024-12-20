package com.db;

import java.sql.Connection;

import org.apache.commons.dbcp2.BasicDataSource;

import com.errorLogging.Internals;

//import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class MyConnection {
	static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String XMLDBNAME = "dbName";
	public static final String XMLDBUSER = "dbUser";
	public static final String XMLDBPASSWORD = "dbPassword";
	public static final int MAXPOOLEDCONNECTIONS = 10;

	static BasicDataSource basicDataSource = null;
	SessionVars sVars = null;
	String dbName = null;

	// create a BasicDataSource connection pool to the dbName database
	protected MyConnection(SessionVars sVars, String dbName) throws Exception {
		this.sVars = sVars;
		this.dbName = dbName;
		if (basicDataSource == null)
			createBasicDataSource(dbName);
	}

	/**
	 * create a BasicDataSource connected to the dbName database. If a
	 * BasicDataSource was already set up, it will be closed before creating the new
	 * BasicDataSource. Be sure to put it back when done with the new databases so
	 * that the next connections go to the dbName in the XML file!
	 * 
	 * @param dbName
	 * @throws Exception
	 */
	public void createBasicDataSource(String dbName) throws Exception {
		if (basicDataSource != null)
			basicDataSource.close();
		basicDataSource = new BasicDataSource();
		String url = "jdbc:mysql://localhost:3306/";
		url += dbName;
		url += "?user=" + sVars.xml.readXML(XMLDBUSER);
		url += "&password=" + sVars.xml.readXML(XMLDBPASSWORD);
		url += "&serverTimezone=UTC";
		basicDataSource.setUrl(url);
		basicDataSource.setMaxTotal(MAXPOOLEDCONNECTIONS);
	}

	public Connection getConnection() throws Exception {		
		if (MAXPOOLEDCONNECTIONS == getActiveCount()){
			throw new Exception("pool depleted");
		}
		return basicDataSource.getConnection();
	}
	
	public int getActiveCount() {
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
