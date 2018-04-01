package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;
import output.Summaries;
import parser.HotcCleanFileParser;
import translator.TranslatorUtilities;
import configuration.LocalConf;

public class Dispatcher {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Dispatcher dispatcher = new Dispatcher();
		
		dispatcher.prepareTranslationPairs("psycho-pass_extra_pack");
		
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
	
	// From Working 
	public void prepareTranslationPairs(String setName) throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities transUtilities = new TranslatorUtilities();
		
		File workingFile = new File(conf.getTranslationPairsFolderPath() + "currentlyWorkingOn.txt");
		transUtilities.updateTranslationsPairsFullListWithSetFile(workingFile);
		
		File setFile =  new File(conf.gethotcCleanFilesFolderPath() + setName + ".txt");
		File setPairsFile =  new File(conf.getTranslationPairsFolderPath() + setName + ".txt");
		
		ArrayList<Card> cards = HotcCleanFileParser.parseCards(setFile);
		summaries.generateAbilityListFile_SetTranslationPairs(cards, setPairsFile);
		
	}
	
}
