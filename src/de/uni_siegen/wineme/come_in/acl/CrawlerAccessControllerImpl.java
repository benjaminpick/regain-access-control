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

import java.util.Properties;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.access.CrawlerAccessController;
import net.sf.regain.crawler.document.RawDocument;

public class CrawlerAccessControllerImpl implements CrawlerAccessController {

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
	public void init(Properties arg0) throws RegainException {
		// TODO Auto-generated method stub

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
	public String[] getDocumentGroups(RawDocument arg0) throws RegainException {
		// TODO Auto-generated method stub
		return null;
	}

}
