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

package de.uni_siegen.wineme.come_in.acl;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

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

	private static final int DATABASE_CONNECT_TIMEOUT_SEC = 5;
	private static final int DATABASE_TIMEOUT_SEC = 2;

	private static Logger mLog = Logger.getLogger(Database.class);
	
	private String driverClassName = "org.postgresql.Driver"; // MySQL: com.mysql.jdbc.Driver
	private String connectionString = "";
	private String username;
	private String password;

	private String tableName = "file";
	private String groupColumnName = "group";
	private String filenameColumnName = "filename";
	
	private Connection connection;
	private PreparedStatement query;
	
	/**
	 * Inti use.
	 * @param config	Configuration from XML
	 * @throws ClassNotFoundException	The driverClassName has not be found (is the connector in classpath?)
	 */
	public void init(Properties config) throws ClassNotFoundException {
		Class.forName(driverClassName);
		
		//driverClassName = config.getProperty("dbDriverClassName");
		connectionString = config.getProperty("dbConnectionString");
		username = config.getProperty("dbUsername");
		password = config.getProperty("dbPassword");
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
		try {
			if (query != null)
			{
				query.close();
				query = null;
			}
		} finally {
			if (connection != null)
			{
				connection.close();
				connection = null;
			}
		}
	}
	
	public String getGroupsForFile(String file) throws SQLException
	{
		if (connection == null)
		{
			mLog.warn("No connection: return no groups");
			return "";
		}
		
		try {
			return _getGroupsForFile(file);
		} catch (SQLException e) {
			// Re-Connect and retry
			mLog.info("SQL Exception: " + e.getMessage() + " - Trying reconnect");
			connect();
			return _getGroupsForFile(file);
		}
	}

	private String _getGroupsForFile(String file) throws SQLException {
		if (query == null)
		{
			String statement = "SELECT \"" + groupColumnName  + "\" FROM \"" + tableName  + "\" WHERE \"" + filenameColumnName + "\" = ?";
			mLog.debug("SQL Query: '" + statement + "'");
			query = connection.prepareStatement(statement);
			query.setMaxRows(1);
		}
		query.setString(1, file);
		ResultSet results = null; 
		try {
			results = query.executeQuery();
			if (!results.next())
			{
				mLog.warn("CrawlerAccessController says: No database entry found for file '" + file + "'");
				return "";
			}

			return results.getString(1);
		} finally {
			if (results != null)
				results.close();
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
