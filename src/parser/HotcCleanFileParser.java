package parser;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import configuration.LocalConf;
import cards.Card;

public class HotcCleanFileParser {

	public static ArrayList<Card> parseCards() throws Exception{

		ArrayList<Card> allCards = new ArrayList<Card>();
		
		LocalConf conf = LocalConf.getInstance(); 
		
		for(File cleanFile : conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		return allCards;
	}
	
	public static ArrayList<Card> parseCards(File hotcCleanFile) throws Exception{
		
    	System.out.println("*** Parsing Set File: " + hotcCleanFile.getName() + " ***");
		
		ArrayList<Card> cards = new ArrayList<Card>();

		List<String> content = new ArrayList<>(Files.readAllLines(hotcCleanFile.toPath(), StandardCharsets.UTF_8));
		
		// Skip header
		while (!content.remove(0).startsWith("=")){
			// Do nothing
		}
		
		while(content.size() > 3){
		
			content.remove(0); // White line
					
			String nameLine = content.remove(0);
			String jpNameLine = content.remove(0);
			String idLine = content.remove(0);
			String colorLine = content.remove(0);
			String levelLine = content.remove(0);
			String traitLine = content.remove(0);
			String triggerLine = content.remove(0);
			
			ArrayList<String> habLines = new ArrayList<String>();
					
			// Flavor ignored
			while (!content.remove(0).startsWith("TEXT:")){
				// Do nothing
			}
	
			String habLine;
			while (!((habLine = content.remove(0)).equals(""))){
				habLines.add(habLine);
			}
			
			Card card = new Card(nameLine, jpNameLine, idLine, colorLine, levelLine, traitLine, triggerLine, habLines);
			cards.add(card);
	
			content.remove(0); //= line
			
		}

		return cards;
	}
	
	public static boolean isExtraBooster(File hotcCleanFile) throws Exception{
		
		boolean isExtraBooster = false;
		
		List<String> content = new ArrayList<>(Files.readAllLines(hotcCleanFile.toPath(), StandardCharsets.UTF_8));
		String infoLine = content.get(0);
		
		isExtraBooster = infoLine.contains("Extra Pack"); 
		
		return isExtraBooster;
	}
	
}
