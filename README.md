Regain Access Control
=====================

These classes are used to implement regain's ACL for an XML Backend.
That means that the group names can be trusted (the front-end is responsible to check these.)

Install
-------

1. ant package
2. Copy the created file `build/AccessController.jar` to your regain directory (or adjust the directories below)
  * Desktop version: The directory where `regain.jar` resides
  * Server version: The directory of `regain-crawler.jar`, as well as `$CATALINA_HOME/conf/regain` (where `SearchConfiguration.xml` resides)

3. Modify `CrawlerConfiguration.xml`: Inside `<configuration>`, add:

		<crawlerAccessController>
		  <class jar="AccessController.jar">de.uni_siegen.wineme.come_in.acl.CrawlerAccessControllerImpl</class>
		  <config>
		    <param name="groupSeperator">,</param>
		    <!-- Groups that are always added -->
		    <param name="defaultGroups"> </param>
		    <!-- Should be equal to start in startlist -->
		    <param name="relativeFilenameBase">file:///path/to/dir</param>
		
		    <param name="dbConnectionString">jdbc:postgresql://localhost/database</param>    
		    <param name="dbUsername">username</param>
		    <param name="dbPassword">password</param>
		  </config>
		</crawlerAccessController>

4. Modify SearchConfiguration.xml: Inside `<defaultSettings>`, add:

		<searchAccessController>
			<class jar="AccessController.jar">de.uni_siegen.wineme.come_in.acl.SearchAccessControllerImpl</class>
	    		<config>
	    			<param name="groupSeperator">,</param>
				<param name="defaultGroups">public</param>
			</config>
		</searchAccessController>

