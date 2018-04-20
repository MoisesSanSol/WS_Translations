package main;

import java.util.HashMap;



import translator.TranslatorUtilities;
import configuration.LocalConf;

public class CustomOneShotsAndOthers {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		CustomOneShotsAndOthers dispatcher = new CustomOneShotsAndOthers();
		//dispatcher.replaceNumbersWithNumericalRegExp();
		dispatcher.cleanPendingTranslationsThatSlippedBy();
		
		System.out.println("*** Finished ***");
	}
	
	public CustomOneShotsAndOthers() throws Exception{
		this.conf = LocalConf.getInstance();
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
