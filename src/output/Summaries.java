package output;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

import parser.HotcCleanFileParser;
import translator.Translator;
import translator.Translator.LineTranslation;
import utilities.CardListUtilities;
import configuration.LocalConf;
import cards.Card;

public class Summaries {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Summaries summaries = new Summaries();
		HotcCleanFileParser parser = new HotcCleanFileParser();
		File file = new File(summaries.conf.gethotcCleanFilesFolderPath() + "gurren_lagann_booster_pack.txt");
		File result = new File(summaries.conf.getGeneralResultsFolderPath() + "gurren_lagann_booster_pack.txt");
		summaries.generateAbilityListFile_BaseSetReference(parser.parseCards(file), result);
		
		System.out.println("*** Finished ***");
	}
	
	public Summaries() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void generateAbilityListFile_NoIds(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (No Ids) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilities = CardListUtilities.getAbilities_Sorted(cards);

		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
	public void generateAbilityListFile_BaseSetReference(ArrayList<Card> cards, File file) throws Exception{
		
		System.out.println("*** Generate Ability List (Base Set Reference) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilitiesBase = CardListUtilities.getAbilities_Sorted(cards);

		ArrayList<String> abilities = new ArrayList<String>();
		
		Translator translator = new Translator();
		
		for(String ability: abilitiesBase){
			abilities.add(ability);
			LineTranslation lineTranslation = translator.findAbilityTranslationPair(ability);
			if(lineTranslation == null){
				abilities.add(ability.replaceAll("::(.+?)::", "::(.+?)::").replaceAll("\"(.+?)\"", "\"(.+?)\""));
				abilities.add("***");
			}
			else{
				abilities.add(lineTranslation.patternString);
				abilities.add(lineTranslation.replace);
			}
			abilities.add("");
		}
		
		Files.write(file.toPath(), abilities, StandardCharsets.UTF_8);
	}
	
}
