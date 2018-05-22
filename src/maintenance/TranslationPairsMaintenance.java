package maintenance;

import java.util.ArrayList;

import cards.Card;
import configuration.LocalConf;
import translator.LineTranslation;
import translator.Translator;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;

public class TranslationPairsMaintenance {
	
private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		TranslationPairsMaintenance dispatcher = new TranslationPairsMaintenance();

		//dispatcher.checkUnusedTranslationPairs();
		//dispatcher.removeDuplicatedPatterns();
		
		System.out.println("*** Finished ***");
	}
	
	public TranslationPairsMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	public void checkUnusedTranslationPairs() throws Exception{
		
		Translator translator = new Translator();
		ArrayList<String> allAbilities = CardListUtilities.getAbilities_AllSorted();
		
		for(LineTranslation lineTranslation : translator.lineTranslations) {
			
			boolean used = false;
			
			for(String ability : allAbilities) {
				
				if(lineTranslation.matchesAbility(ability)) {
					used = true;
				}
			}
			
			if(!used) {
				System.out.println("* Unused Translation Pattern:\r\n" + lineTranslation.patternString);
				System.out.println("* Unused Translation:\r\n" + lineTranslation.replace);
			}
		}
	}
	
	public void removeDuplicatedPatterns() throws Exception{
		
		System.out.println("** Remove Duplicated Patterns");
		
		TranslatorUtilities transUtility = new TranslatorUtilities();
		transUtility.updateTranslationsPairsFullListWithPairsFile(this.conf.translationPairsFullListFile);
		
	}
}
