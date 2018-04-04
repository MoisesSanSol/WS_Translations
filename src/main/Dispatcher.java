package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;
import output.Summaries;
import parser.HotcCleanFileParser;
import translator.Translator;
import translator.TranslatorUtilities;
import configuration.LocalConf;

public class Dispatcher {

	private LocalConf conf;
	
	String setName = "";
	String setFileName = "is_the_order_a_rabbit_booster_pack";
	String setId = "";
	String setLaPageId = "";
	String setYytPageId = "";
	String promoFileName = "schwarz_promos";
	//String promoFileName = "weib_promos";
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Dispatcher dispatcher = new Dispatcher();
		
		//dispatcher.createSetTranslationRelatedFiles_Init();
		dispatcher.createSetTranslationRelatedFiles_Progress();
		
		//dispatcher.createTranslationReferenceFile_AllSets();
		//dispatcher.createTranslationProgressFile_AllSets();
		
		//dispatcher.prepareTranslationPairs("psycho-pass_extra_pack");
		
		/*HotcCleanFileParser hotcCleanFileParser = new HotcCleanFileParser();
		Summaries summaries = new Summaries();
		
		LocalConf conf = LocalConf.getInstance();
		
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File file : conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(hotcCleanFileParser.parseCards(file));
		}
		
		summaries.generateAbilityListFile_Ids(allCards, new File(conf.getGeneralResultsFolderPath() + "All.txt"));
		*/
		
		System.out.println("*** Finished ***");
	}
	
	public Dispatcher() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void createSetTranslationRelatedFiles_Init() throws Exception{
		
		Summaries summaries = new Summaries();
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		File setTranslationsFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Translations.txt");
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_WorkingOn.txt");
		File progressFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Progress.txt");
		
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		
		summaries.generateAbilityListFile_BaseSetReference(allCards, setTranslationsFile);
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		summaries.generateTranslationProgress(allCards, progressFile, false);
		
	}
	
	public void createSetTranslationRelatedFiles_Progress() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		File setTranslationsFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Translations.txt");
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_WorkingOn.txt");
		File progressFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Progress.txt");
		
		utility.updateTranslationsPairsFullListWithSetFile(workingFile);
		
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		
		summaries.generateAbilityListFile_BaseSetReference(allCards, setTranslationsFile);
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		summaries.generateTranslationProgress(allCards, progressFile, true);
		
	}
	
	public void createTranslationReferenceFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		HotcCleanFileParser parser = new HotcCleanFileParser();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			
			ArrayList<Card> allCards = parser.parseCards(cleanFile);
			File file = new File(this.conf.getTranslationPairsFolderPath() + cleanFile.getName());
			summaries.generateAbilityListFile_BaseSetReference(allCards, file);
			
		}
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
