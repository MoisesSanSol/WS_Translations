package utilities;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class Utilities {
	
	public static void checkFolderExistence(File folder) throws Exception{
		if(!folder.exists()){
			folder.mkdirs();
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
	
	public static HashMap<String,ArrayList<String>> getHashMap_ReverseHashMap(HashMap<String,String> hashMap) throws Exception{
		
		HashMap<String,ArrayList<String>> inverse = new HashMap<String,ArrayList<String>>();
		
		for(String key : hashMap.keySet()){
			String value = hashMap.get(key);
			
			if(inverse.containsKey(value)){
				inverse.get(value).add(key);
			}
			else{
				ArrayList<String> newValues = new ArrayList<String>();
				newValues.add(key);
				inverse.put(value, newValues);
			}
		}
		
		return inverse;
	}
	
	public HashMap<String,String> getHashMap_FromPairsFile(File pairsFile) throws Exception{
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		
		List<String> content = new ArrayList<>(Files.readAllLines(pairsFile.toPath(), StandardCharsets.UTF_8));
		
		while(content.size() > 2){
			
			String patternLine = content.remove(0);
			String replacementLine = content.remove(0);
			content.remove(0); // Ignore line
			if(!replacementLine.equals("")){
				if(!pairs.containsKey(patternLine)){
					pairs.put(patternLine, replacementLine);
				}
			}
			else{
				throw new Exception("Argh!");
			}
		}
		
		return pairs;
	}
}
