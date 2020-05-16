package de.brightside.bnotes.util;

import de.brightside.bnotes.model.Role;
import de.brightside.bnotes.model.User;

public class BnotesUtil {
	public static String getEndingOfFilename(String filename) {
		if ((filename == null) || (filename.length() == 0)) {
			return "";
		}
		int pos = filename.indexOf(".");
		if (pos < 0) {
			return "";
		}
		return filename.substring(pos + 1).toLowerCase();
	}
	
	public static boolean hasRole(User user, String roleName) {
		if (user == null) {
			return false;
		}
		if (roleName == null) {
			return false;
		}
		if (user.getRoles() == null) {
			return false;
		}
		for (Role i: user.getRoles()) {
			if (roleName.equals(i.getRoleName())) {
				return true;
			}
		}
		return false;
	}

}
