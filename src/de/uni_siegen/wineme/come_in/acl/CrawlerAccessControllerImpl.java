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
import java.sql.SQLException;
import java.util.Properties;

import de.uni_siegen.wineme.come_in.RegainHelper;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.access.CrawlerAccessController;
import net.sf.regain.crawler.document.RawDocument;

public class CrawlerAccessControllerImpl extends AccessControllerImpl implements CrawlerAccessController, Closeable {

	private SelectGroupsDatabase database;
	private String relativeFilenameBase;

	public CrawlerAccessControllerImpl()
	{
		database = new SelectGroupsDatabase();
	}
	
	/**
	 * Initializes the CrawlerAccessController.
	 * <p>
	 * This method is called once right after the CrawlerAccessController
	 * instance was created.
	 * 
	 * @param config
	 *            The configuration.
	 * 
	 * @throws RegainException
	 *             If loading the config failed.
	 */
	@Override
	public void init(Properties config) throws RegainException {
		super.init(config);
		
		try {
			database.init(config);
			database.connect();
		} catch (ClassNotFoundException e) {
			throw new RegainException("Database access failed - verify driverClassName", e);
		} catch (SQLException e) {
			throw new RegainException("Database access failed - connectionString", e);
		}
		
		relativeFilenameBase = config.getProperty("relativeFilenameBase");
	}

	/**
	 * Gets the names of the groups that are allowed to read the given document.
	 * <p>
	 * Note: The group array must not be <code>null</code> and the group names
	 * must not contain whitespace.
	 * 
	 * @param document
	 *            The document to get the groups for.
	 * @return The groups that are allowed to read the given document.
	 * 
	 * @throws RegainException
	 *             If getting the groups failed.
	 */
	@Override
	public String[] getDocumentGroups(RawDocument config) throws RegainException {
		String groups = defaultGroups; 
		
		// (If the permissions change, you'll need to touch the file in order to re-index it with the correct permissions.)
		String file = RegainHelper.convertUrlToFilename(config.getUrl(), relativeFilenameBase);
		
		try {
			groups = groups + database.getGroupsForFile(file);
		} catch (SQLException e) {
			throw new RegainException("Could not get meta-data from database", e);
		}
		
		return groupSplit(groups);
	}
	
	@Override
	public void close() throws IOException {
		if (database != null)
			database.close();
	}
}
