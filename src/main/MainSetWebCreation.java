package main;

import java.io.File;
import java.util.ArrayList;

import output.Summaries;
import parser.HotcCleanFileParser;
import staticweb.StaticWebHelper;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import cards.Card;
import configuration.LocalConf;

public class MainSetWebCreation {

	// For creating the web for a set.
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		String setName = "Psycho-Pass";
		String setFileName = "psycho-pass_extra_pack";
		String setId = "PP/SE14";
		String setLaPageId = "";
		String setYytPageId = "";
		String promoFileName = "schwarz_promos"; 
		
		LocalConf conf = LocalConf.getInstance();
		TranslatorUtilities transUtilities = new TranslatorUtilities();
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		
		// Ensure that the clean hotc file is available
		//File setHotcFile = new File(conf.gethotcCleanFilesFolderPath() + setFileName);
		
		// Ensure that translations pairs are available in the full pairs list

		
		// Get Images
		
		/* Create set index, then manually adjust irregularities and promos */
		
		//staticWebHelper.generateIndex_ExtraBooster(setId, setName, 40);
		
		/* Create cards pages */
		//staticWebHelper.isExtraBoosterCard = true;
		//staticWebHelper.isFoilCard = true;
		//staticWebHelper.generateCardPages_FullSet(setFileName);

		/* Create promo cards pages */
		File file = new File(LocalConf.getInstance().gethotcCleanFilesFolderPath() + promoFileName +".txt");
		staticWebHelper.generateCardPages_ArbitraryCards(CardListUtilities.filterCards_FindSetPrs_All(HotcCleanFileParser.parseCards(file), setId));
		
		
		System.out.println("*** Finished ***");
	}
}
