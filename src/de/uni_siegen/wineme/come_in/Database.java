/*
 * regain/ACL - Implement SearchAccessController/CrawlerAccessController for regain (User/Group via URL) 
 * Copyright (C) 2012  Come_IN Computerclubs (University of Siegen)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Come_IN-Team <come_in-team@listserv.uni-siegen.de>
 */

package de.uni_siegen.wineme.come_in;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import net.sf.regain.RegainToolkit;

import org.apache.log4j.Logger;

/**
 * Interface to database.
 * Use JDBC / Postgres
 * 
 * Postgresql needs to be configured correctly (if not localhost):
 * @see http://jdbc.postgresql.org/documentation/80/prepare.html
 * 
 * For details on the connection string,
 * @see http://jdbc.postgresql.org/documentation/80/connect.html
 */
public class Database implements Closeable {

	private static final String PARAM_DB_PASSWORD = "dbPassword";
	private static final String PARAM_DB_USERNAME = "dbUsername";
	private static final String PARAM_DB_CONNECTION_STRING = "dbConnectionString";
	private static final int DATABASE_CONNECT_TIMEOUT_SEC = 5;
	private static final int DATABASE_TIMEOUT_SEC = 2;

	protected static Logger mLog = Logger.getLogger(Database.class);
	
	private String driverClassName = "org.postgresql.Driver"; // MySQL: com.mysql.jdbc.Driver
	private String connectionString = "";
	private String username;
	private String password;

	protected String tableName = "file";
	protected String filenameColumnName = "fs_location";
	
	protected Connection connection;
	protected void init() throws ClassNotFoundException
	{
		Class.forName(driverClassName);
	}
	
	/**
	 * Init use.
	 * @param config	Configuration from XML
	 * @throws ClassNotFoundException	The driverClassName has not be found (is the connector in classpath?)
	 */
	public void init(Properties config) throws ClassNotFoundException {
		
		//driverClassName = config.getProperty("dbDriverClassName");
		connectionString = config.getProperty(PARAM_DB_CONNECTION_STRING);
		username = config.getProperty(PARAM_DB_USERNAME);
		password = config.getProperty(PARAM_DB_PASSWORD);
		
		init();
	}

	/**
	 * Init use.
	 * @param config	Configuration from Parameters
	 * @throws ClassNotFoundException	The driverClassName has not be found (is the connector in classpath?)
	 */
	public void init(Map<String, String> config) throws ClassNotFoundException {
		//driverClassName = config.getProperty("dbDriverClassName");
		connectionString = config.get(PARAM_DB_CONNECTION_STRING);
		username = config.get(PARAM_DB_USERNAME);
		password = config.get(PARAM_DB_PASSWORD);
		
		init();
	}
	
	
	
	/**
	 * Connect to database
	 * 
	 * @throws SQLException				Connection could not be established (credentials incorrect?)
	 * @see http://jdbc.postgresql.org/documentation/80/connect.html
	 */
	public void connect() throws SQLException
	{
		try {
			disconnect();
		} catch (SQLException e) {
		}

		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("loginTimeout", DATABASE_CONNECT_TIMEOUT_SEC + "");
		props.setProperty("socketTimeout", DATABASE_TIMEOUT_SEC + "");
		
		connection = DriverManager.getConnection(connectionString, props);
	}
	
	public void disconnect() throws SQLException
	{
		if (connection != null)
		{
			connection.close();
			connection = null;
		}
	}
	
	@Override
	public void close() throws IOException {
		try {
			disconnect();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
	}
}
