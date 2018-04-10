package main;

import java.io.File;
import java.util.ArrayList;

import parser.HotcCleanFileParser;
import translator.LineTranslation;
import cards.Card;
import configuration.LocalConf;

public class StaticWebMaintenance {
	
	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebMaintenance dispatcher = new StaticWebMaintenance();
		
		dispatcher.searchCardsByPattern("[A] [(1)] At the start of Encore Step, if there are no other Rested Characters in your Front Row, you may pay cost. If so, Rest this.");
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	public void searchCardsByPattern(String pattern) throws Exception{
		
		System.out.println("** Search Cards By Ability");
		
		ArrayList<Card> allCards = new ArrayList<Card>();  
		
		for(File hotcCleanFile : conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(hotcCleanFile));
		}
		
		LineTranslation helper = new LineTranslation(pattern, "Irrelevant");
		
		ArrayList<String> foundCards = new ArrayList<String>(); 
		
		for(Card card : allCards){
			for(String ability :card.habs){
				if(helper.matchesPattern(ability)){
					foundCards.add(card.id);
					System.out.println("Found in Card: " + card.id);
				}
			}
		}
	}
}
