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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Interface to database.
 * Uses JDBC as to be able to switch databases later.
 * 
 * @see http://www.fankhausers.com/postgresql/jdbc/#hello
 * @see http://www.itblogging.de/java/java-mysql-jdbc-tutorial/
 */
public class Database {

	private String driverClassName = "";
	private String connectionString = "";
	private Connection connection;
	
	/**
	 * 
	 * @param config	Configuration from XML
	 */
	public void init(Properties config) {
		driverClassName = config.getProperty("driverClassName");
	}
	
	/**
	 * Connect to database
	 * 
	 * @throws ClassNotFoundException	The driverClassName has not be found (is the connector in classpath?)
	 * @throws SQLException				Connection could not be established (credentials incorrect?)
	 */
	public void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(driverClassName);
		connection = DriverManager.getConnection(connectionString);
	}
	
	public String getGroupsForFile(String file)
	{
		return "";
	}
}