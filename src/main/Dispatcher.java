package main;

import java.io.File;
import java.util.ArrayList;

import cards.Card;
import output.Summaries;
import parser.HotcCleanFileParser;
import configuration.LocalConf;

public class Dispatcher {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Dispatcher dispatcher = new Dispatcher();
		
		HotcCleanFileParser hotcCleanFileParser = new HotcCleanFileParser();
		Summaries summaries = new Summaries();
		
		LocalConf conf = LocalConf.getInstance();
		
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File file : conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(hotcCleanFileParser.parseCards(file));
		}
		
		summaries.generateAbilityListFile_Ids(allCards, new File(conf.getGeneralResultsFolderPath() + "All.txt"));
		
		System.out.println("*** Finished ***");
	}
	
	public Dispatcher() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
}
