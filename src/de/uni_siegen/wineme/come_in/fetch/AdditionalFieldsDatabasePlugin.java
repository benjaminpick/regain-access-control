/*
 * regain/ACL - Fetch additional fields for a file from Database 
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

package de.uni_siegen.wineme.come_in.fetch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import de.uni_siegen.wineme.come_in.RegainHelper;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.Crawler;
import net.sf.regain.crawler.config.PreparatorConfig;
import net.sf.regain.crawler.document.RawDocument;
import net.sf.regain.crawler.document.WriteablePreparator;
import net.sf.regain.crawler.plugin.AbstractCrawlerPlugin;

public class AdditionalFieldsDatabasePlugin extends AbstractCrawlerPlugin {
	private static final String BLACKLIST_FIELD_SEPERATOR = ",";

	protected SelectAllColumnsDatabase database;

	private String relativeFilenameBase;

	private Collection<String> blacklist;
	
	private static Logger mLog = Logger.getLogger(AdditionalFieldsDatabasePlugin.class);
	
	/**
	 * Initializes the plugin.
	 *
	 * @param 	config 	The configuration for this plugin.
	 * @throws RegainException When the configuration has an error.
	 */
	@Override
	public void init(PreparatorConfig config) throws RegainException {
		Map<String, String> section = config.getSectionWithName("database");
		
		// Load config
		database = new SelectAllColumnsDatabase();
		try {
			database.init(section);
		} catch (ClassNotFoundException e) {
			throw new RegainException("Crawler Plugin could not be initialized", e);
		}
		
		relativeFilenameBase = section.get("relativeFilenameBase");
		
		String rawBlacklist = section.get("blacklistFields");
		if (rawBlacklist != null)
		{
			String[] arr = rawBlacklist.split(BLACKLIST_FIELD_SEPERATOR);
			blacklist = Arrays.asList(arr);
		}
		else
			blacklist = new ArrayList<String>();
	}
	
	/**
	 * Called before the crawling process starts (Crawler::run()).
	 * Initialize Thumbnail Generation:
	 * load Config and configure Thumbnailer
	 * 
	 * This may be called multiple times during the lifetime of a plugin instance,
	 * but CrawlerPlugin::onFinishCrawling() is always called in between.
	 * 
	 * @param crawler 		The crawler instance that is about to begin crawling
	 */
	public void onStartCrawling(Crawler crawler)  {
		// Connect to db
		try {
			database.connect();
		} catch (SQLException e) {
			throw new RuntimeException("Database access failed - connectionString", e);
		}
	}
	
	/**
	 * Called after a document is being prepared to be added to the index:
	 * Create the thumbnail and add the information about its creation to the lucene entry.
	 * 
	 * @param document		Regain document that was analysed
	 * @param preparator	Preparator that has analysed this document
	 */
	public void onAfterPrepare(RawDocument document, WriteablePreparator preparator) {

		String file = RegainHelper.convertUrlToFilename(document.getUrl(), relativeFilenameBase);
		
		Map<String, String> values;
		try {
			values = database.getAllDatabaseFields(file);
		} catch (SQLException e) {
			throw new RuntimeException("CrawlerPlugin could not find the additional fields in database", e);
		} 
		if (values == null)
			return;
		
		for (Map.Entry<String, String> entry : values.entrySet())
		{
			if (blacklist.contains(entry.getKey()))
					continue;
			preparator.addAdditionalField(_column2field(entry.getKey()), entry.getValue());
		}

		String additional_text = values.get("additional_searchable_text");
		if (additional_text != null)
		{
			String data = preparator.getCleanedMetaData();
			data += additional_text;
			preparator.setCleanedMetaData(data);
		}
	}
	
	private String _column2field(String key) {
		return "db_" + key;
	}

	/**
	 * Called after the crawling process has finished or aborted (because of an exception):
	 * Close the Thumbnail Generator.
	 * 
	 * This may be called multiple times during the lifetime of a plugin instance.
	 * 
	 * @param crawler 		The crawler instance that is about to finish crawling
	 */
	public void onFinishCrawling(Crawler crawler) {
		try {
			database.close();
		} catch (IOException e) {
			mLog.error("Disconnect from Database failed", e);
		}
	}
	
	public void close() throws IOException {
		if (database != null)
		{
			database.close();
			database = null;
		}
	}
}
