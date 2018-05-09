package main;

import java.io.File;
import java.util.ArrayList;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
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
		//dispatcher.createPendingSetTranslationsFile_AllCards_Raw();
		//dispatcher.createTranslationProgressFile_AllSets();
		
		dispatcher.createTranslationReferencesFile_Raw();
		//dispatcher.createTranslationReferencesFile_Clean();
		//dispatcher.createTranslationReferencesFile_Simplified();
		
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
	
	public void createPendingSetTranslationsFile_AllCards() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File workingFile = new File(this.conf.getGeneralResultsFolderPath() + "AllAbilities_WorkingOn.txt");
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		if(workingFile.exists()){
			utility.updateTranslationsPairsFullListWithPairsFile(workingFile);
		}
		
		summaries.generateAbilityListFile_PendingReferencelessTranslations(allCards, workingFile);
	}
	
	public void createPendingSetTranslationsFile_AllCards_Raw() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File workingFile = new File(this.conf.getGeneralResultsFolderPath() + "AllAbilities_WorkingOn.txt");
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		if(workingFile.exists()){
			utility.updateTranslationsPairsFullListWithPairsFile(workingFile);
		}
		
		summaries.generateAbilityListFile_PendingSetTranslations_Raw(allCards, workingFile);
	}
	
	public void createTranslationProgressFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		File file = new File(this.conf.generalResultsFolder + "AllSetsProgress.txt");
		summaries.generateTranslationProgress(allCards, file, false);
	}

	public void createTranslationReferencesFile_Raw() throws Exception{
		
		Summaries summaries = new Summaries();
		File file = new File(this.conf.getGeneralResultsFolderPath() + "AllTranslationReferences_Raw.txt");
		summaries.generateAbilityListFile_TranlationReferences_Raw(file);
	}
	
	public void createTranslationReferencesFile_Clean() throws Exception{
		
		Summaries summaries = new Summaries();
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		File file = new File(this.conf.getGeneralResultsFolderPath() + "AllTranslationReferences_Clean.txt");
		summaries.generateAbilityListFile_TranlationReferences_Clean(allCards, file);
	}
	
	public void createTranslationReferencesFile_Simplified() throws Exception{
		
		Summaries summaries = new Summaries();
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		File file = new File(this.conf.getGeneralResultsFolderPath() + "AllTranslationReferences_Simplified.txt");
		summaries.generateAbilityListFile_TranlationReferences_Simplified(allCards, file);
	}
}
