package utilities;

import java.io.File;

public class Utilities {
	
	public static void checkFolderExistence(File folder) throws Exception{
		if(!folder.exists()){
			folder.mkdir();
			System.out.println("# " + folder + " did not exist. New folder created.");
		}
	}
	
	public static String escapeHtml(String line) throws Exception{
		return line.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
	}
}
