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
import translator.Translator.LineTranslation;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import configuration.LocalConf;
import cards.Card;

public class Summaries {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Summaries summaries = new Summaries();
		HotcCleanFileParser parser = new HotcCleanFileParser();
		
		File file = new File(summaries.conf.gethotcCleanFilesFolderPath() + "is_the_order_a_rabbit_booster_pack.txt");
		File result = new File(summaries.conf.getTranslationPairsFolderPath() + "currentlyWorkingOn.txt");
		File result2 = new File(summaries.conf.getTranslationPairsFolderPath() + "currentProgress.txt");
		
		//summaries.generateAbilityListFile_BaseSetReference(parser.parseCards(file), result);
		//summaries.generateAbilityListFile_SetTranslationPairs(CardListUtilities.filterCards_FindSetPrs(parser.parseCards(file),"SHS/W56"), file2);
		summaries.generateAbilityListFile_PendingSetTranslations(parser.parseCards(file), result);
		//summaries.generateAbilityListFile_PendingSetTranslations(CardListUtilities.filterCards_FindSetPrs(parser.parseCards(file),"SHS/W56"), result);
		//summaries.generateAbilityListFile_RedundantPatterns();
		//summaries.generateTranslationProgress(parser.parseCards(file), result2);
		
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
		
		System.out.println("*** Generate Ability List (No Ids) Txt for " + file.getName() + " ***");
		
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
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\""));
				abilities.add("***");
				abilities.add("");
			}
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_RedundantPatterns() throws Exception{
		
		System.out.println("*** Generate Ability List (Redundant Patterns) Txt ***");
		
		TranslatorUtilities utilities = new TranslatorUtilities();
		HashMap<String,String> pairs = utilities.getTranslationsPairsFromFile_PairsFile(this.conf.translationPairsFullListFile);
		HashMap<String,String> seenPairs = new HashMap<String,String>();
		HashMap<String,String> duplicatedReplaces = new HashMap<String,String>();
		
		for(String key : pairs.keySet()){

			if(seenPairs.containsKey(pairs.get(key))){
				duplicatedReplaces.put(pairs.get(key), seenPairs.get(pairs.get(key)) + "$%&" + key);
				seenPairs.put(pairs.get(key), seenPairs.get(key) + "$%&" + key);
			}
			else{
				seenPairs.put(pairs.get(key), key);
			}
		}
		
		ArrayList<String> content = new ArrayList<String>();
		
		for(String key : duplicatedReplaces.keySet()){
			content.add(key);
			String[] patterns = duplicatedReplaces.get(key).split("\\Q$%&\\E");
			for(String pattern : patterns){
				content.add(pattern);
			}
			content.add("");
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
			if(cardTranslatedAbilities == card.habs.size()){
				translatedCards++;
			}
			content.add(card.id + "\t" + cardTranslatedAbilities + "/" + card.habs.size());
		}


		
		if(update){
			
			ArrayList<String> oldContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			content.add(0, "");
			content.add(0, "Past " + oldContent.get(1));
			content.add(0, "Past " + oldContent.get(0));
		}
		
		content.add(0, "");
		content.add(0, "Total abilities: " + translatedAbilities + "/" + abilityCount);
		content.add(0, "Total cards: " + translatedCards + "/" + filteredCards.size());
		
		Files.write(file.toPath(), content, StandardCharsets.UTF_8);
	}
	
}
