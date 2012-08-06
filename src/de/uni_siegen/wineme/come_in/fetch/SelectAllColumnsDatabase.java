package de.uni_siegen.wineme.come_in.fetch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import de.uni_siegen.wineme.come_in.Database;

public class SelectAllColumnsDatabase extends Database {

	private PreparedStatement querySelectGroups;
	
	public Map<String, String> getAllDatabaseFields(String file) throws SQLException {
		if (connection == null)
		{
			mLog.warn("No connection: return no fields");
			return null;
		}
		
		try {
			return _getAllDatabaseFields(file);
		} catch (SQLException e) {
			// Re-Connect and retry
			mLog.info("SQL Exception: " + e.getMessage() + " - Trying reconnect");
			connect();
			return _getAllDatabaseFields(file);
		}
	}

	
	
	private Map<String, String> _getAllDatabaseFields(String file) throws SQLException {
		prepareStatement();
		querySelectGroups.setString(1, file);
		
		ResultSet results = null; 
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			results = querySelectGroups.executeQuery();
			if (!results.next())
			{
				mLog.warn("AdditionalFieldsDatabasePlugin says: No database entry found for file '" + file + "'");
				return map;
			}
	
			ResultSetMetaData meta = results.getMetaData();
			int nbColumns = meta.getColumnCount();
			
			for (int i = 1; i <= nbColumns; i++)
			{
				String key = meta.getColumnName(i);
				String value = results.getString(i);
				map.put(key, value);
				
				//mLog.debug("Found key: " + key + " value: " + value);
			}
			return map;
		} finally {
			if (results != null)
				results.close();
		}
	}
	
	protected void prepareStatement() throws SQLException {
		if (querySelectGroups == null)
		{
			String statement = "SELECT * FROM \"" + tableName  + "\" WHERE \"" + filenameColumnName + "\" = ?";
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
