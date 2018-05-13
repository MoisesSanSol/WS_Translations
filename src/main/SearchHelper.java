package main;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import parser.HotcCleanFileParser;
import cards.Card;
import translator.LineTranslation;
import translator.TranslatorUtilities;
import utilities.Utilities;
import configuration.LocalConf;

public class SearchHelper {
	
	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		SearchHelper dispatcher = new SearchHelper();

		dispatcher.search_TranslationPairs_ReplaceBased();
		//dispatcher.search_Cards_PatternBased();
		
		
		System.out.println("*** Finished ***");
	}
	
	public SearchHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public HashMap<String,String> search_TranslationPairs_ReplaceBased(String replaceStr) throws Exception{
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		
		HashMap<String,String> fullTranslationsPairs = utility.getTranslationsPairsFromFile_PairsFile(conf.translationPairsFullListFile);
		HashMap<String,ArrayList<String>> inverseTranslationsPairs = Utilities.getHashMap_ReverseHashMap(fullTranslationsPairs);

		for(String pattern : inverseTranslationsPairs.get(replaceStr)){
			pairs.put(pattern, replaceStr);
		}
		
		return pairs;
	}
	
	public void search_TranslationPairs_ReplaceBased() throws Exception{
		
		System.out.println("** Search Tranlation Pairs Replace Based");
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		String searchFilePath = this.conf.getGeneralResultsFolderPath() + "PairsSearch_Replace" + ".txt";
		File searchFile = new File(searchFilePath);
		String resultsFilePath = this.conf.getGeneralResultsFolderPath() + "PairsSearch_Replace_Results" + ".txt";
		File resultsFile = new File(resultsFilePath);
		
		if(Utilities.checkFileExistence(searchFile)){
			
			HashMap<String,String> translationPairs = new HashMap<String,String>();
			
			ArrayList<String> replacesStr = new ArrayList<String>(Files.readAllLines(searchFile.toPath(), StandardCharsets.UTF_8));
			
			while(replacesStr.size() > 0){
				
				String replaceStr = replacesStr.remove(0);
				
				if(!replaceStr.isEmpty()){
					translationPairs.putAll(this.search_TranslationPairs_ReplaceBased(replaceStr));
				}
			}
			
			utility.createFileFromTranslationPairs(translationPairs, resultsFile);
		}
	}
	
	public HashMap<Card, String> search_Cards_PatternBased(LineTranslation helper) throws Exception{
		
		System.out.println("** Search Cards By Ability");
		
		ArrayList<Card> allCards = new ArrayList<Card>();  
		
		for(File hotcCleanFile : conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(hotcCleanFile));
		}
		
		HashMap<Card, String> foundCards = new HashMap<Card, String>(); 
		
		for(Card card : allCards){
			for(String ability :card.habs){
				if(helper.matchesPattern(ability)){
					foundCards.put(card, ability);
					System.out.println("Found in Card: " + card.id);
				}
			}
		}
		
		return foundCards;
	}
	
	public HashMap<Card, String> search_Cards_PatternBased(String pattern) throws Exception{
		
		System.out.println("** Search Cards By Ability");
		
		LineTranslation helper = new LineTranslation(pattern,"Irrelevant");
		
		HashMap<Card, String> foundCards = this.search_Cards_PatternBased(helper); 
		
		return foundCards;
	}
	
	public void search_Cards_PatternBased() throws Exception{
		
		System.out.println("** Search Cards Pattern Based");
		
		String searchFilePath = this.conf.getGeneralResultsFolderPath() + "CardSearch_Pattern" + ".txt";
		File searchFile = new File(searchFilePath);
		String resultsFilePath = this.conf.getGeneralResultsFolderPath() + "CardSearch_Pattern_Results" + ".txt";
		File resultsFile = new File(resultsFilePath);
		
		if(Utilities.checkFileExistence(searchFile)){
			
			ArrayList<String> resultsFileContent = new ArrayList<String>();
			
			ArrayList<String> patterns = new ArrayList<String>(Files.readAllLines(searchFile.toPath(), StandardCharsets.UTF_8));
			
			while(patterns.size() > 0){
				
				String pattern = patterns.remove(0);
				
				if(!pattern.isEmpty()){
					
					resultsFileContent.add(pattern);
					
					HashMap<Card, String> cards = this.search_Cards_PatternBased(pattern);
					if(cards.isEmpty()){
						resultsFileContent.add("No cards found for pattern.");
					}
					else{
						for(Card card : cards.keySet()){
							resultsFileContent.add(card.id);
						}
					}
					resultsFileContent.add("");
				}
			}
			
			Files.write(resultsFile.toPath(), resultsFileContent, StandardCharsets.UTF_8);
		}
	}
}
