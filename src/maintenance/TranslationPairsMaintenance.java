package maintenance;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import configuration.LocalConf;
import translator.LineTranslation;
import translator.Translator;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import utilities.Utilities;

public class TranslationPairsMaintenance {
	
private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		TranslationPairsMaintenance dispatcher = new TranslationPairsMaintenance();

		dispatcher.removeUnusedTranslationPairs();
		//dispatcher.removeDuplicatedPatterns();
		
		System.out.println("*** Finished ***");
	}
	
	public TranslationPairsMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	
	public void removeUnusedTranslationPairs() throws Exception{
		
		Translator translator = new Translator();
		ArrayList<String> allAbilities = CardListUtilities.getAbilities_AllSorted();
		
		File unusedTranslations = new File(this.conf.getTranslationPairsFolderPath() + "UnusedTranslationPairs.txt");
		Utilities.checkFileExistence(unusedTranslations);
		ArrayList<String> unusedContent = (ArrayList<String>)Files.readAllLines(unusedTranslations.toPath(), StandardCharsets.UTF_8);
		
		HashMap<String,String> keptTranslationsPairs = new HashMap<String,String>();
		
		for(LineTranslation lineTranslation : translator.lineTranslations) {
			
			boolean used = false;
			
			for(String ability : allAbilities) {
				
				if(lineTranslation.matchesAbility(ability)) {
					used = true;
				}
			}
			
			if(!used) {
				unusedContent.add("* Unused Translation Pair:");
				unusedContent.add(lineTranslation.patternString);
				unusedContent.add(lineTranslation.replace);
				System.out.println("* Unused Translation Pair:");
				System.out.println(lineTranslation.patternString);
				System.out.println(lineTranslation.replace);
			}
			else{
				keptTranslationsPairs.put(lineTranslation.patternString, lineTranslation.replace);
			}
		}
		
		Files.write(unusedTranslations.toPath(), unusedContent, StandardCharsets.UTF_8);
		TranslatorUtilities transUtility = new TranslatorUtilities();
		transUtility.updateTranslationsPairsFullListWithPairs(keptTranslationsPairs);
	}
	
	public void removeDuplicatedPatterns() throws Exception{
		
		System.out.println("** Remove Duplicated Patterns");
		
		TranslatorUtilities transUtility = new TranslatorUtilities();
		transUtility.updateTranslationsPairsFullListWithPairsFile(this.conf.translationPairsFullListFile);
		
	}
}
