package main;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import cards.Card;
import output.Summaries;
import parser.HotcCleanFileParser;
import staticweb.ImagesHelper;
import staticweb.StaticWebHelper;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import utilities.Utilities;
import configuration.LocalConf;
import download.DownloadHelper;

public class StaticWebCreation {

	String setFileId = "MB_S10";
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebCreation dispatcher = new StaticWebCreation();

		/* Getting Started */
		//dispatcher.checkWebFolders();
		
		/* Creating translations for set */
		//dispatcher.createSetTranslationRelatedFiles();
		dispatcher.createSetTranslationWorkingFile();
		
		/* Getting images for set web */
		//dispatcher.createWebImages();
		
		/* Creating web pages */
		//dispatcher.createWebPages();
		
		System.out.println("*** Finished ***");
	}

	String setName = "";
	String setFileName = "";
	String setTdFileName = "";
	String setId = "";
	String setLaPageId = "";
	String setYytPageId = "";
	String promoFileName = "";

	boolean isExtraBooster = false;
	boolean isLegacyEb = false;
	boolean isLegacySp = false;
	boolean isLegacyTd = false;
	
	int regularCardCount = 0;
	int extendedCardCount = 0;
	int promoCardCount = 0;
	int tdCardCount = 0;

	private LocalConf conf;
	
	public StaticWebCreation() throws Exception{
		this.conf = LocalConf.getInstance();
		this.loadSetInfo();
	}
	
	public void loadSetInfo() throws Exception{

		Properties setReference = new Properties();
		
		String setReferenceFilePath = this.conf.getReferenceFilesFolderPath() + this.setFileId + ".txt";
		InputStream setReferenceFileStream = new FileInputStream(setReferenceFilePath);

		setReference.load(setReferenceFileStream);

		this.setName = setReference.getProperty("setName");
		this.setFileName = setReference.getProperty("setFileName");
		this.setTdFileName = setReference.getProperty("setTdFileName");
		this.setId = setReference.getProperty("setId");
		this.setLaPageId = setReference.getProperty("setLaPageId");
		this.setYytPageId = setReference.getProperty("setYytPageId");
		this.promoFileName = setReference.getProperty("promoFileName");
		this.isLegacyEb = Boolean.parseBoolean(setReference.getProperty("isLegacyEb"));
		this.isLegacySp = Boolean.parseBoolean(setReference.getProperty("isLegacySp"));
		this.isLegacyTd = Boolean.parseBoolean(setReference.getProperty("isLegacyTd"));

		this.isExtraBooster = this.setFileName.contains("extra_pack");
		
		if(this.setFileId.contains("_W")){
			this.promoFileName = "weib_promos";
		}
		else{
			this.promoFileName = "schwarz_promos";
		}
	}
	
	public void createSetTranslationWorkingFile() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileId + "_WorkingOn.txt");
	
		if(workingFile.exists()){
			utility.updateTranslationsPairsFullListWithSetFile(workingFile);
		}
		
		ArrayList<Card> allCards = this.getAllSetCards();
		
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		
		Desktop.getDesktop().open(this.conf.generalResultsFolder);
	}
	
	public void createSetTranslationRelatedFiles() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File setTranslationsFile = new File(conf.getGeneralResultsFolderPath() + this.setFileId + "_Translations.txt");
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileId + "_WorkingOn.txt");
		File progressFile = new File(conf.getGeneralResultsFolderPath() + this.setFileId + "_Progress.txt");
		
		if(workingFile.exists()){
			utility.updateTranslationsPairsFullListWithSetFile(workingFile);
		}
		
		ArrayList<Card> allCards = this.getAllSetCards();
		
		summaries.generateAbilityListFile_TranslatedSetReference(allCards, setTranslationsFile);
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		summaries.generateTranslationProgress(allCards, progressFile, progressFile.exists());
		
		Desktop.getDesktop().open(this.conf.generalResultsFolder);
	}
	
	public void checkWebFolders() throws Exception{
		File baseFolder = new File(this.conf.getStaticWebFolderPath() + this.setFileId);
		File cardsFolder = new File(this.conf.getStaticWebFolderPath() + this.setFileId + "\\cards");
		File imagesFolder = new File(this.conf.getStaticWebFolderPath() + this.setFileId + "\\images");
		Utilities.checkFolderExistence(baseFolder);
		Utilities.checkFolderExistence(cardsFolder);
		Utilities.checkFolderExistence(imagesFolder);
	}
	
	public void createWebImages() throws Exception{
		
		System.out.println("** Create Web Images");
		
		DownloadHelper downloadHelper = new DownloadHelper();
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		ImagesHelper imagesHelper = new ImagesHelper();
		
		String imagesFolderPath = this.conf.getStaticWebFolderPath() + this.setFileId + "\\images\\";
		
		imagesHelper.renameCotdImagesToWebFormat(this.setFileId);
		
		downloadHelper.isLegacyEb = this.isLegacyEb;
		
		if(!this.setLaPageId.equals("NotYet")){
			downloadHelper.downloadImages_LittleAkiba_SetGaps(this.setLaPageId, imagesFolderPath);
			if(this.isLegacySp){
				downloadHelper.downloadImages_LittleAkiba_LegacyPromos(this.setLaPageId, imagesFolderPath);	
			}
			
			System.out.println("* Check missing images and proceed to yyt to fill gaps. Press enter to continue:");
			System.in.read();
		}
		
		downloadHelper.downloadImages_Yuyutei_SetGaps(this.setYytPageId, imagesFolderPath);
		
		staticWebHelper.isExtraBooster = this.isExtraBooster;
		staticWebHelper.isLegacyEb = this.isLegacyEb;
		staticWebHelper.rotateClimax(this.getAllSetCards(), imagesFolderPath);
	}
	
	public void createWebPages() throws Exception{
		
		System.out.println("** Create Web Pages");
		
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		
		staticWebHelper.setId = this.setId;
		staticWebHelper.setName = this.setName;
		staticWebHelper.isExtraBooster = this.isExtraBooster;
		staticWebHelper.isLegacyEb = this.isLegacyEb;
		
		ArrayList<Card> allCards = this.getAllSetCards();
		staticWebHelper.generateCardPages_ArbitraryCards(allCards);
		
		staticWebHelper.regularCardCount = this.regularCardCount;
		staticWebHelper.extendedCardCount = this.extendedCardCount;
		staticWebHelper.promoCardCount = this.promoCardCount;
		staticWebHelper.tdCardCount = this.tdCardCount;
		
		staticWebHelper.createIndex_Main();
	}
	
	public ArrayList<Card> getAllSetCards() throws Exception{
		
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		if(!this.setFileName.equals("")){
			
			File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");

			ArrayList<Card> setCards = HotcCleanFileParser.parseCards(setCleanFile);
			ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(setCards);
			ArrayList<Card> multipleImageCards = CardListUtilities.filterInMultipleImageCards(setCards);
			ArrayList<Card> parallelCards = CardListUtilities.filterInParallelCards(setCards);
			
			if(this.isExtraBooster){
				for(Card card : baseCards){
					card.hasEbFoil = true;
				}
				for(Card card : multipleImageCards){
					card.hasEbFoil = true;
				}
			}
			
			if(this.isLegacySp){
				for(Card card : parallelCards){
					if(card.rarity.equals("SP")){
						card.isLegacySp = true;
						String cardBaseId = card.id.replace("SP", "");
						Card baseCard = CardListUtilities.filterCards_FindCard_ById(baseCards, cardBaseId);
						card.isLegacySp = true;
						baseCard.isLegacySp = true;
						card.rarity = baseCard.rarity;
					}
				}
			}
			
			this.regularCardCount = baseCards.size();
			allCards.addAll(baseCards);
			allCards.addAll(multipleImageCards);
			allCards.addAll(parallelCards);
		}
		if(!this.setTdFileName.equals("")){
			File setCleanTdFile = new File(conf.gethotcCleanFilesFolderPath() + this.setTdFileName + ".txt");
			ArrayList<Card> tdCards = HotcCleanFileParser.parseCards(setCleanTdFile);
			ArrayList<Card> baseTdCards = CardListUtilities.filterOutParallelCards(tdCards);
			this.tdCardCount = baseTdCards.size();
			allCards.addAll(tdCards);
		}
		
		File setCleanPromoFile = new File(conf.gethotcCleanFilesFolderPath() + this.promoFileName + ".txt");
		ArrayList<Card> allPromoCards = HotcCleanFileParser.parseCards(setCleanPromoFile);
		ArrayList<Card> promoCards = CardListUtilities.filterCards_FindSetPrs_Pr(allPromoCards, this.setId);
		ArrayList<Card> basePrCards = CardListUtilities.filterOutParallelCards(promoCards);
		this.promoCardCount = CardListUtilities.getMaxPrNumber(basePrCards);
		allCards.addAll(promoCards);
		ArrayList<Card> extendedCards = CardListUtilities.filterCards_FindSetPrs_Extended(allPromoCards, this.setId);
		ArrayList<Card> baseExtendedCards = CardListUtilities.filterOutParallelCards(extendedCards);
		this.extendedCardCount = baseExtendedCards.size();
		allCards.addAll(baseExtendedCards);
		
		return allCards;
	}
}
