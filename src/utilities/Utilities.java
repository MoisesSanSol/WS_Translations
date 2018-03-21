package utilities;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

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
	
	public static ArrayList<String> getFileNames(File folder) throws Exception{
		ArrayList<String> names = new ArrayList<String>();
		for(File yuyuteiImage : folder.listFiles()) {
			String fileName = yuyuteiImage.getName();
			String name = FilenameUtils.removeExtension(fileName);
			names.add(name);
		}
		return names;
	}
}
