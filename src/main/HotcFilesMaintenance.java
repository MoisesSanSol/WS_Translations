package main;

import hotcfiles.HotcCleanFilesHelper;
import hotcfiles.HotcRawFilesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import output.Summaries;
import parser.HotcCleanFileParser;
import utilities.CardListUtilities;
import utilities.FileUtilities;
import cards.Card;
import configuration.LocalConf;
import download.DownloadHelper;

public class HotcFilesMaintenance {
	
	private LocalConf conf;
	private String[] promoRawFileNames = {"weib_promos","schwarz_promos"};
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcFilesMaintenance dispatcher = new HotcFilesMaintenance();

		//dispatcher.updateHotcPromoFiles();
		dispatcher.checkHotCFilesUpdates(null);
				
		System.out.println("*** Finished ***");
	}
	
	public HotcFilesMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	public void updateHotcPromoFiles() throws Exception{
		
		System.out.println("** Update HotC Promo Files");
		
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		HotcCleanFilesHelper hotcCleanFilesHelper = new HotcCleanFilesHelper();
		
		ArrayList<Card> currentPromoCards = this.getPromoCards();
		
		hotcRawFilesHelper.downloadPromoHotcRawFiles();
		hotcCleanFilesHelper.generateCleanPromoHotcFiles();
		
		ArrayList<Card> updatedPromoCards = this.getPromoCards();
		
		HashMap<String,String> differences = CardListUtilities.compareCards(currentPromoCards, updatedPromoCards);
		
		ArrayList<String> updatePromoResults = new ArrayList<String>();
		
		for(String cardId : differences.keySet()){
			System.out.println(cardId);
			System.out.println(differences.get(cardId));
			updatePromoResults.add(cardId);
			updatePromoResults.add(differences.get(cardId));
		}
		
		FileUtilities.saveGenericFile(updatePromoResults, "promoUpdateResults");
	}
	
	public ArrayList<Card> getPromoCards() throws Exception{
		
		ArrayList<Card> promoCards = new ArrayList<Card>();
		
		for(String promoRawFileName : this.promoRawFileNames){
        	
        	File hotcCleanFile = new File(conf.gethotcCleanFilesFolderPath() + promoRawFileName + ".txt");
        	promoCards.addAll(HotcCleanFileParser.parseCards(hotcCleanFile));
		}
		
		return promoCards;
	}
	
	public void checkHotCFilesUpdates(String file) throws Exception{
		
		System.out.println("** Check HotC Files Updates - File : " + file);
		
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		
		hotcRawFilesHelper.checkIfHotcRawFileIsUpToDate("bang_dream!_booster_pack");
	}
}
