Regain Access Control
=====================

These classes are used to implement regain's ACL for an XML Backend. That means that the group names can be trusted (the front-end is responsible to check these.)

Install
-------

1. ant package
2. Copy the created file build/AccessController.jar to your regain directory
3. Modify CrawlerConfiguration.xml:

4. Modify SearchConfiguration.xml: Inside <defaultSettings>, add

	<searchAccessController>
		<class jar="AccessController.jar">de.uni_siegen.wineme.come_in.acl.SearchAccessControllerImpl</class>
    		<config>
    			<param name="groupSeperator">,</param>
			<param name="defaultGroups">public</param>
		</config>
	</searchAccessController>
