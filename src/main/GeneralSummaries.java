package main;

import java.io.File;
import java.util.ArrayList;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.TranslatorUtilities;
import cards.Card;
import configuration.LocalConf;

public class GeneralSummaries {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		GeneralSummaries dispatcher = new GeneralSummaries();
		
		//dispatcher.createTranslationReferenceFile_AllSets()
		//dispatcher.createTranslationReferenceFile_AllCards();
		//dispatcher.createPendingSetTranslationsFile_AllCards();
		//dispatcher.createTranslationProgressFile_AllSets();
		
		System.out.println("*** Finished ***");
	}
	
	public GeneralSummaries() throws Exception{
		this.conf = LocalConf.getInstance();
	}

	public void createTranslationReferenceFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			
			ArrayList<Card> allCards = HotcCleanFileParser.parseCards(cleanFile);
			File file = new File(this.conf.getTranslationPairsFolderPath() + cleanFile.getName());
			summaries.generateAbilityListFile_BaseSetReference(allCards, file);
			
		}
	}
	
	public void createTranslationReferenceFile_AllCards() throws Exception{
		
		Summaries summaries = new Summaries();
		File file = new File(this.conf.getGeneralResultsFolderPath() + "AllAbilitiesReference.txt");
		
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		summaries.generateAbilityListFile_TranslationReferences(allCards, file);
	}
	
	public void createPendingSetTranslationsFile_AllCards() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File workingFile = new File(this.conf.getGeneralResultsFolderPath() + "AllAbilities_WorkingOn.txt");
		
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		if(workingFile.exists()){
			utility.updateTranslationsPairsFullListWithPairsFile(workingFile);
		}
		
		summaries.generateAbilityListFile_PendingReferencelessTranslations(allCards, workingFile);
	}
	
	public void createTranslationProgressFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		File file = new File(this.conf.generalResultsFolder + "AllSetsProgress.txt");
		summaries.generateTranslationProgress(allCards, file, false);
	}
	
}
