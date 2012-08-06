package de.uni_siegen.wineme.come_in.acl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_siegen.wineme.come_in.Database;

public class SelectGroupsDatabase extends Database {

	private String groupColumnName = "groups";
	private PreparedStatement querySelectGroups;

	public String getGroupsForFile(String file) throws SQLException {
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
		prepareStatement();
		querySelectGroups.setString(1, file);
		
		ResultSet results = null; 
		try {
			results = querySelectGroups.executeQuery();
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

	protected void prepareStatement() throws SQLException {
		if (querySelectGroups == null)
		{
			String statement = "SELECT \"" + groupColumnName  + "\" FROM \"" + tableName  + "\" WHERE \"" + filenameColumnName + "\" = ?";
			mLog.debug("SQL Query: '" + statement + "'");
			querySelectGroups = connection.prepareStatement(statement);
			querySelectGroups.setMaxRows(1);
		}
	}
	
	public void disconnect() throws SQLException
	{
		try {
			if (querySelectGroups != null)
			{
				querySelectGroups.close();
				querySelectGroups = null;
			}
		} finally {
			super.disconnect();
		}
	}

}
