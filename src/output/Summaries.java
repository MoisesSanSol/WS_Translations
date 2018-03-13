package output;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

import parser.HotcCleanFileParser;
import configuration.LocalConf;
import cards.Card;

public class Summaries {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Summaries summaries = new Summaries();
		HotcCleanFileParser parser = new HotcCleanFileParser();
		File file = new File(summaries.conf.gethotcCleanFilesFolderPath() + "saekano_-_how_to_raise_a_boring_girlfriend_booster_pack.txt");
		File result = new File(summaries.conf.getGeneralResultsFolderPath() + "saekano_-_how_to_raise_a_boring_girlfriend_booster_pack.txt");
		summaries.generateAbilityListFile_NoIds(parser.parseCards(file), result);
		
		System.out.println("*** Finished ***");
	}
	
	public Summaries() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void generateAbilityListFile_NoIds(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (No Ids) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilities = new ArrayList<String>(); 
		
		for(Card card : cards){
			for(String ability : card.habs){
				if(!abilities.contains(ability)){
					abilities.add(ability);
				}
			}
		}
		
		Collections.sort(abilities);

		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
}
