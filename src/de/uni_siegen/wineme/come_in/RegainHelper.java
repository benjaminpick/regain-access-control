package de.uni_siegen.wineme.come_in;

import org.apache.log4j.Logger;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;

public class RegainHelper {
	protected static Logger mLog = Logger.getLogger(RegainHelper.class);
	
	public static String convertUrlToFilename(String url, String relativeFilenameBase)
	{
		url = getRelativeFilename(relativeFilenameBase, url);
		String file = "";
		try {
			file = RegainToolkit.urlDecode(url, RegainToolkit.INDEX_ENCODING);
		} catch (RegainException e) {
			mLog.error(e);
		}

		return file;
	}
	
	public static String getRelativeFilename(String sBase, String sTarget) {
		if (sTarget.startsWith(sBase))
		{
			if (sBase.endsWith("/") || sBase.endsWith("\\") || sTarget.length() == sBase.length())
				return sTarget.substring(sBase.length());
			else
				return sTarget.substring(sBase.length() + 1);
		}
		else
			return sTarget; // Leave absolute
	}
}
