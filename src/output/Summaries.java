package output;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import parser.HotcCleanFileParser;
import translator.Translator;
import translator.LineTranslation;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import utilities.Utilities;
import configuration.LocalConf;
import cards.Card;

public class Summaries {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Summaries summaries = new Summaries();
		
		summaries.generateAbilityListFile_RedundantPatterns();
		
		System.out.println("*** Finished ***");
	}
	
	public Summaries() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void generateAbilityListFile_NoIds(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (No Ids) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilities = CardListUtilities.getAbilities_Sorted(cards);

		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_Ids(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Ids) Txt for " + file.getName() + " ***");
		
		HashMap<String,String> allAbilities = new HashMap<String,String>(); 
		
		for(Card card : cards){
			for(String ability :card.habs){
				if(allAbilities.containsKey(ability)){
					allAbilities.put(ability, allAbilities.get(ability) + "," + card.id);
				}
				else{
					allAbilities.put(ability, "\t" + card.id);
				}
			}
		}
		
		ArrayList<String> abilities = new ArrayList<String>();
		
		for(String ability : allAbilities.keySet()){
			abilities.add(ability + allAbilities.get(ability));
		}
		
		Collections.sort(abilities);
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_BaseSetReference(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Base Set Reference) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){
			abilities.add(ability);
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\""));
				abilities.add("***");
			}
			else{
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.replace);
			}
			abilities.add("");
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_TranslatedSetForCorrections(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Base Set Reference) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation != null){
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.replace);
			}
			abilities.add("");
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_TranslatedSetReference(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Base Set Reference) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){
			abilities.add(ability);
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\""));
				abilities.add("***");
				abilities.add("***");
			}
			else{
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.replace);
				abilities.add(lineTranslation.translateAbility(ability));
			}
			abilities.add("");
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_SetTranslationPairs(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Base Set Reference) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){
			//abilities.add(ability);
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\""));
				abilities.add("***");
			}
			else{
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.replace);
			}
			abilities.add("");
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_PendingSetTranslations(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Set Pending Translations) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){

			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				abilities.add(ability);
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\"").replaceAll("\\+\\d+? Power", "+(d+?) Power"));
				abilities.add("***");
				abilities.add("");
			}
		}
		abilities.add("LóL: force notepad++ to recognice the file as UTF-8. No real need to remove before processing, but suit yourself.");
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_PendingReferencelessTranslations(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Set Pending Translations) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){

			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				String referencelesAbility = ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\"").replaceAll("\\+\\d+? Power", "+(d+?) Power"); 
				if(!abilities.contains(referencelesAbility)){
					abilities.add(referencelesAbility);
					abilities.add("***");
					abilities.add("");
				}
			}
		}
		abilities.add("LóL: force notepad++ to recognice the file as UTF-8. No real need to remove before processing, but suit yourself.");
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_TranlationReferences_Applied(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Translations References Applied to Cards) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> translations = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){

			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			
			if(lineTranslation != null){
				
				String translation = lineTranslation.translateAbility(ability);
				String cleanTranslation = TranslatorUtilities.removeCustomTags(translation);

				if(!translations.contains(cleanTranslation)){
					translations.add(cleanTranslation);
				}
			}
		}

		Collections.sort(translations);
		
		Files.write(file.toPath(), translations, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_TranlationReferences_Raw(File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Translations References Raw) Txt for " + file.getName() + " ***");
		
		Translator auxTranslator = new Translator();

		ArrayList<String> replaceStrs = new ArrayList<String>();
		
		for(LineTranslation lineTranslation : auxTranslator.lineTranslations){

			String replaceStr = lineTranslation.replace;
			
			if(!replaceStrs.contains(replaceStr)){
				
				replaceStrs.add(replaceStr);
			}
		}

		Collections.sort(replaceStrs);
		
		Files.write(file.toPath(), replaceStrs, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_RedundantPatterns() throws Exception{
		
		System.out.println("*** Generate Ability List (Redundant Patterns) Txt ***");
		
		TranslatorUtilities utilities = new TranslatorUtilities();
		HashMap<String,String> pairs = utilities.getTranslationsPairsFromFile_PairsFile(this.conf.translationPairsFullListFile);
		HashMap<String,ArrayList<String>> inversePairs = Utilities.getHashMap_ReverseHashMap(pairs);

		ArrayList<String> content = new ArrayList<String>();
		
		ArrayList<String> sortedKeys = new ArrayList<String>(inversePairs.keySet());
		Collections.sort(sortedKeys);
		
		for(String replace : sortedKeys){
			ArrayList<String> patterns = inversePairs.get(replace);
			if(patterns.size() > 1){
				content.add(replace);
				for(String pattern : patterns){
					content.add(pattern);	
				}
				content.add("");
			}
		}
		
		File result = new File(this.conf.getGeneralResultsFolderPath() + "RedundantPatterns.txt");
		Files.write(result.toPath(), content, StandardCharsets.UTF_8);
	}

	public void generateTranslationProgress(ArrayList<Card> cards, File file, boolean update) throws Exception{
		
		System.out.println("*** Generate Translation Progress Txt for " + file.getName() + " ***");
		
		ArrayList<Card> filteredCards = CardListUtilities.filterOutParallelCards(cards);
		ArrayList<String> content = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		int abilityCount = 0;
		int translatedCards = 0;
		int translatedAbilities = 0;
		
		for(Card card : filteredCards){
			Card translatedCard = translator.translateCard(card);
			int cardTranslatedAbilities = 0;
			for(String ability : card.habs){
				abilityCount++;
				if(!translatedCard.habs.contains(ability)){
					cardTranslatedAbilities++;
					translatedAbilities++;
				}
			}
			String check = "X";
			if(cardTranslatedAbilities == card.habs.size()){
				check = "O";
				translatedCards++;
			}
			content.add(card.id + "\t" + cardTranslatedAbilities + "/" + card.habs.size() + "\t" + check);
		}


		
		if(update){
			
			ArrayList<String> oldContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			content.add(0, "");
			content.add(0, "Past " + oldContent.get(1));
			content.add(0, "Past " + oldContent.get(0));
		}
		
		content.add(0, "");
		int abilityPercent = (int)(((double)translatedAbilities / (double)abilityCount) * 100);
		content.add(0, "Total abilities: " + translatedAbilities + "/" + abilityCount + "(" + abilityPercent + "%)");
		int cardPercent = (int)(((double)translatedCards / (double)filteredCards.size()) * 100);
		content.add(0, "Total cards: " + translatedCards + "/" + filteredCards.size() + "(" + cardPercent + "%)");
		
		Files.write(file.toPath(), content, StandardCharsets.UTF_8);
	}
	
}
