package main;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;



import java.util.List;

import org.apache.commons.io.FilenameUtils;

import translator.TranslatorUtilities;
import configuration.LocalConf;

public class CustomOneShotsAndOthers {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		CustomOneShotsAndOthers dispatcher = new CustomOneShotsAndOthers();
		//dispatcher.replaceNumbersWithNumericalRegExp();
		//dispatcher.cleanPendingTranslationsThatSlippedBy();
		//dispatcher.filterSetWorkingFile();
		
		for(File file : dispatcher.conf.hotcCleanFilesFolder.listFiles()){
			System.out.println(FilenameUtils.getName(file.getName()));
		}
		
		System.out.println("*** Finished ***");
	}
	
	public CustomOneShotsAndOthers() throws Exception{
		this.conf = LocalConf.getInstance();
	}

	public void filterSetWorkingFile() throws Exception{
		
		File file = new File(conf.getGeneralResultsFolderPath() + "AllAbilities_WorkingOn.txt"); 
				
		ArrayList<String> oldContent = new ArrayList<String>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> newContent = new ArrayList<String>();
		
		while(oldContent.size() > 2){
		
			//String line1 = oldContent.remove(0);
			String line2 = oldContent.remove(0);
			String line3 = oldContent.remove(0);
			String line4 = oldContent.remove(0);
			
			if(line2.contains("[C] EXPERIENCE")){
				if(!newContent.contains(line2)){
					//newContent.add(line1);
					newContent.add(line2);
					newContent.add(line3);
					newContent.add(line4);
				}
			}
		}
		Files.write(file.toPath(), newContent, StandardCharsets.UTF_8);
	}
	
	
	public void replaceNumbersWithNumericalRegExp() throws Exception{
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		HashMap<String,String> fullTranslationsPairs = utility.getTranslationsPairsFromFile_PairsFile(conf.translationPairsFullListFile);
		HashMap<String,String> newTranslationsPairs = new HashMap<String,String>(); 
		
		for(String pattern : fullTranslationsPairs.keySet()){
			
			String newPattern = pattern.replaceFirst("\\+\\d+? Power", "+(d+?) Power");
			
			if(!newTranslationsPairs.containsKey(newPattern)){
				
				String replacement = fullTranslationsPairs.get(pattern);

				int regExpCount = replacement.replaceAll("[^\\$]", "").length() + 1;
				String newReplacement =  replacement.replaceFirst("\\+\\d+? de Poder", "+\\$" + regExpCount + " de Poder");
						
				newTranslationsPairs.put(newPattern, newReplacement);
			}
		}
		
		utility.createFileFromTranslationPairs(newTranslationsPairs, conf.translationPairsFullListFile);
	}
	
	public void cleanPendingTranslationsThatSlippedBy() throws Exception{
		
		TranslatorUtilities utility = new TranslatorUtilities();
		utility.updateTranslationsPairsFullListWithPairsFile(conf.translationPairsFullListFile);
	}
}
