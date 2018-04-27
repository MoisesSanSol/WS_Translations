package main;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.LineTranslation;
import translator.Translator;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import utilities.Utilities;
import cards.Card;
import configuration.LocalConf;

public class StaticWebMaintenance {
	
	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebMaintenance dispatcher = new StaticWebMaintenance();

		//dispatcher.prepareSetTranslationsPairForFixes();
		//dispatcher.checkAllTranlationPairs();
		//dispatcher.fixTranlationPairs();
		
		//dispatcher.searchCardsByPattern("[S] [Counter] BACKUP (d+?), Level (d+?) [(1) Discard a card from your hand to the Waiting Room]");
				
		System.out.println("*** Finished ***");
	}
	
	public StaticWebMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	public void prepareSetTranslationsPairForFixes() throws Exception{
		
		System.out.println("** Prepare Set Translations Pair For Fixes");
		
		StaticWebCreation dispatcher = new StaticWebCreation();
		Summaries summaries = new Summaries();
		
		String fixesFilePath = this.conf.getGeneralResultsFolderPath() + "TranlationPairFixes" + ".txt";
		File fixesFile = new File(fixesFilePath);
		
		ArrayList<Card> setCards = dispatcher.getAllSetCards();
		summaries.generateAbilityListFile_TranslatedSetForCorrections(setCards, fixesFile);		
	}
	
	public HashMap<Card, String> searchCardsByPattern(LineTranslation helper) throws Exception{
		
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
	
	public HashMap<Card, String> searchCardsByPattern(String pattern) throws Exception{
		
		System.out.println("** Search Cards By Ability");
		
		LineTranslation helper = new LineTranslation(pattern,"Irrelevant");
		
		HashMap<Card, String> foundCards = this.searchCardsByPattern(helper); 
		
		return foundCards;
	}
	
	public void fixTranlationPairs() throws Exception{
		
		System.out.println("** Fix Tranlation Pairs");
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		String fixesFilePath = this.conf.getGeneralResultsFolderPath() + "TranlationPairFixes" + ".txt";
		File fixesFile = new File(fixesFilePath);
		
		if(Utilities.checkFileExistence(fixesFile)){
			
			HashMap<String,String> translationsPairs = utility.getTranslationsPairsFromFile_PairsFile(this.conf.translationPairsFullListFile);
			
			ArrayList<String> fixes = new ArrayList<String>(Files.readAllLines(fixesFile.toPath(), StandardCharsets.UTF_8));
			ArrayList<String> fixesResults = new ArrayList<String>();
			
			while(fixes.size() > 3){
				
				String originalPattern = fixes.remove(0);
				fixesResults.add(originalPattern);
				String newPattern = fixes.remove(0);
				fixesResults.add(newPattern);
				String newReplace = fixes.remove(0);
				fixesResults.add(newReplace);
				fixes.remove(0); // Blank line separator
				fixesResults.add("");
				
				if(translationsPairs.containsKey(originalPattern)){
					fixesResults.add("Pair being corrected found in pairs.");
					System.out.println("Pair being corrected found in pairs.");
					
					String originalReplace = translationsPairs.remove(originalPattern);
					LineTranslation helper = new LineTranslation(originalPattern, originalReplace);
					LineTranslation translator = new LineTranslation(newPattern, newReplace);
					
					HashMap<Card, String> impactedCards = this.searchCardsByPattern(helper);
					for(Card card : impactedCards.keySet()){
						fixesResults.add("Found in Card: " + card.id);
						String ability = impactedCards.get(card);
						String oldLine = helper.translateAbility(ability);
						String newLine = translator.translateAbility(ability);
						this.fixCardPage(card, oldLine, newLine);
					}
					
				}
				

				translationsPairs.put(newPattern, newReplace);

				fixesResults.add("");
			}
			
			utility.updateTranslationsPairsFullListWithPairs(translationsPairs);
			
			String fixesResultsFilePath = this.conf.getGeneralResultsFolderPath() + "TranlationPairFixesResults" + ".txt";
			File fixesResultsFile = new File(fixesResultsFilePath);
			
			Files.write(fixesResultsFile.toPath(), fixesResults, StandardCharsets.UTF_8);
		}
	}
	
	public void checkAllTranlationPairs() throws Exception{
		
		System.out.println("** Check All Tranlation Pairs");
		
		Translator translator = new Translator();
		
		String fixesFilePath = this.conf.getGeneralResultsFolderPath() + "TranlationPairFixes" + ".txt";
		File fixesFile = new File(fixesFilePath);
		
		Utilities.checkFileExistence(fixesFile);
		ArrayList<String> errors = new ArrayList<String>();
		
		ArrayList<String> abilities = CardListUtilities.getAbilities_AllSorted();
		
		for(String ability : abilities){
			
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			
			if(lineTranslation != null){
				
				try{
					lineTranslation.translateAbility(ability);
				}
				catch(Exception ex){
					errors.add(ex.getMessage());
					errors.add(lineTranslation.patternString);
					errors.add(lineTranslation.patternString);
					errors.add(lineTranslation.replace);
					errors.add("");
				}
			}
			
		}
			
		Files.write(fixesFile.toPath(), errors, StandardCharsets.UTF_8);
	}
	
	
	public void fixCardPage(Card card, String oldLine, String newLine) throws Exception{
		
		String fileName = card.fileId;
		String setFileId = card.fileId.split("-")[0];
		String fullFilePath = this.conf.getStaticWebFolderPath() + setFileId + "\\cards\\" + fileName + ".html";
		File cardPage = new File(fullFilePath);
		
		if(cardPage.exists()){
		
			ArrayList<String> pageContent = new ArrayList<>(Files.readAllLines(cardPage.toPath(), StandardCharsets.UTF_8));
			
			if(oldLine.contains("@@")){
				
				String escapedOldPattern = "^\\Q" + oldLine.replaceAll("@@", "\\\\E(.+?)\\\\Q") + "\\E$";
				for(String line : pageContent){
					if(line.matches(escapedOldPattern)){
						String href = line.replaceFirst(escapedOldPattern, "$1");
						String realNewLine = newLine.replace("'@@", "'" + href).replace("@@'", "</a>'");
						pageContent.set(pageContent.indexOf(line), realNewLine);
					}
				}
			}
			else{
				pageContent.set(pageContent.indexOf(oldLine), newLine);
			}
			
			Files.write(cardPage.toPath(), pageContent, StandardCharsets.UTF_8);
		}
	}
}
