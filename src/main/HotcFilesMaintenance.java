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
import cards.CardUtilities;
import configuration.LocalConf;
import download.DownloadHelper;

public class HotcFilesMaintenance {
	
	private LocalConf conf;
	private String[] promoRawFileNames = {"weib_promos","schwarz_promos"};
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcFilesMaintenance dispatcher = new HotcFilesMaintenance();
		
		dispatcher.checkHotcFilesUpdates();
		
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
	
	public void checkHotcFilesUpdates() throws Exception{
		
		System.out.println("** Check HotC Files Updates **");
		
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		HotcCleanFilesHelper hotcCleanFilesHelper = new HotcCleanFilesHelper();
		
		// Download all files
		//hotcRawFilesHelper.downloadAgainHotcRawFiles();
		
		// Compare files
		//hotcRawFilesHelper.compareHotcRawFiles();
		
		// Create clean files
		//hotcCleanFilesHelper.generateCleanUpdatedHotcFiles();

		// Get cards and search differences
		
		ArrayList<String> audit = new ArrayList<String>();
		
		String hotcCleanUpdatedFolderPath = conf.gethotcCleanFilesFolderPath() + "JustDownloaded//";
		File hotcCleanUpdatedFolder = new File(hotcCleanUpdatedFolderPath);
		
		for(File updatedFile : hotcCleanUpdatedFolder.listFiles()){
			audit.add("Checking file: " + updatedFile.getName());
			
			File oldCleanFile = new File(conf.gethotcCleanFilesFolderPath() + updatedFile.getName());
			
			ArrayList<Card> newCards = HotcCleanFileParser.parseCards(updatedFile);
			ArrayList<Card> oldCards = HotcCleanFileParser.parseCards(oldCleanFile);
			
			for(Card newCard : newCards){
				Card oldCard = oldCards.remove(0);
				String differences = CardUtilities.compareCards(newCard, oldCard);
				
				if(differences != null){
					audit.add(differences);
				}
			}
		}
		
		for(String auditLine : audit){
			System.out.println(auditLine);
		}
		
		/*String temporalFolderPath = conf.gethotcRawFilesFolderPath() + "JustDownloaded//";
		File temporalFolder = new File(temporalFolderPath);
		
        for(File newHotcRawFile : temporalFolder.listFiles()){
        	String rawFileUrl = conf.hotcTranslationFileBaseUrl + newHotcRawFile.getName();
        	Thread.sleep(conf.politeness);
			DownloadHelper.downloadFile(rawFileUrl, newHotcRawFile);
        }*/
	}
	
	
	/*public void checkHotcFilesUpdates() throws Exception{
		
		System.out.println("** Check HotC Files Updates - File : " + file);
		
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		
		hotcRawFilesHelper.checkIfHotcRawFileIsUpToDate("bang_dream!_booster_pack");
	}*/
	
}
