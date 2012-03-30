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

public class AccessControllerImpl {
	protected String defaultGroups = "";
	protected String groupSeperator = " ";

	public void init(Properties config) throws RegainException {
		System.out.println("init");
		String groupSeperatorParam = config.getProperty("groupSeperator");
		if (groupSeperatorParam != null && groupSeperatorParam.length() > 0)
			groupSeperator = groupSeperatorParam; 
		
		String defaultGroupsParam = config.getProperty("defaultGroups");
		if (defaultGroupsParam != null)
			defaultGroups = defaultGroupsParam + groupSeperator;
	}
	
	protected String[] groupSplit(String groups) {
		return groups.split(groupSeperator);
	}
}
