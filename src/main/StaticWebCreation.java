package main;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;

import cards.Card;
import output.Summaries;
import parser.HotcCleanFileParser;
import staticweb.StaticWebHelper;
import translator.TranslatorUtilities;
import utilities.CardListUtilities;
import utilities.Utilities;
import configuration.LocalConf;
import download.DownloadHelper;

public class StaticWebCreation {

	private LocalConf conf;
	
	String setName = "KONOSUBA -God’s blessing on this wonderful world!";
	String setFileName = "konosuba_booster_pack";
	String setTdFileName = "konosuba_trial_deck";
	String setId = "KS/W49";
	String setFileId = "KS_W49";
	String setLaPageId = "328";
	String setYytPageId = "konosuba";
	//String promoFileName = "schwarz_promos";
	String promoFileName = "weib_promos";

	boolean isLegacy = false;
	
	int regularCardCount = 0;
	int extendedCardCount = 0;
	int promoCardCount = 0;
	int tdCardCount = 0;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebCreation dispatcher = new StaticWebCreation();
		
		/* Creating translations for set */
		//dispatcher.createSetTranslationRelatedFiles_Init();
		dispatcher.createSetTranslationRelatedFiles_Ongoing();
		
		/* Getting images for set web */
		//dispatcher.checkWebFolders();
		//dispatcher.createWebImages();
		
		/* Creating web pages */
		//dispatcher.createWebPages();
		
		/* Other shit that will end in other place */
		//dispatcher.createTranslationReferenceFile_AllSets();
		//dispatcher.createTranslationProgressFile_AllSets();
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebCreation() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	public void createSetTranslationRelatedFiles_Init() throws Exception{
		
		Summaries summaries = new Summaries();
		
		File setTranslationsFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Translations.txt");
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_WorkingOn.txt");
		File progressFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Progress.txt");
		
		ArrayList<Card> allCards = this.getAllSetCards();
		
		summaries.generateAbilityListFile_BaseSetReference(allCards, setTranslationsFile);
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		summaries.generateTranslationProgress(allCards, progressFile, false);
		
		Desktop.getDesktop().open(this.conf.generalResultsFolder);
	}
	
	public void createSetTranslationRelatedFiles_Ongoing() throws Exception{
		
		Summaries summaries = new Summaries();
		TranslatorUtilities utility = new TranslatorUtilities();
		
		File setTranslationsFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Translations.txt");
		File workingFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_WorkingOn.txt");
		File progressFile = new File(conf.getGeneralResultsFolderPath() + this.setFileName + "_Progress.txt");
		
		utility.updateTranslationsPairsFullListWithSetFile(workingFile);
		
		ArrayList<Card> allCards = this.getAllSetCards();
		
		summaries.generateAbilityListFile_BaseSetReference(allCards, setTranslationsFile);
		summaries.generateAbilityListFile_PendingSetTranslations(allCards, workingFile);
		summaries.generateTranslationProgress(allCards, progressFile, true);
		
		Desktop.getDesktop().open(this.conf.generalResultsFolder);
	}
	
	public void createTranslationReferenceFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			
			ArrayList<Card> allCards = HotcCleanFileParser.parseCards(cleanFile);
			File file = new File(this.conf.getTranslationPairsFolderPath() + cleanFile.getName());
			summaries.generateAbilityListFile_BaseSetReference(allCards, file);
			
		}
	}
	
	public void createTranslationProgressFile_AllSets() throws Exception{
		
		Summaries summaries = new Summaries();
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File cleanFile : this.conf.hotcCleanFilesFolder.listFiles()){
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		File file = new File(this.conf.generalResultsFolder + "AllSetsProgress.txt");
		summaries.generateTranslationProgress(allCards, file, false);
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
		
		String imagesFolderPath = this.conf.getStaticWebFolderPath() + this.setFileId + "\\images\\";
		
		downloadHelper.downloadImages_LittleAkiba_SetGaps(this.setLaPageId, imagesFolderPath);
		if(this.isLegacy){
			downloadHelper.downloadImages_LittleAkiba_LegacyPromos(this.setLaPageId, imagesFolderPath);	
		}
		
		System.out.println("* Check missing images and proceed to yyt to fill gaps. Press enter to continue:");
		System.in.read();
		
		downloadHelper.downloadImages_Yuyutei_SetGaps(this.setYytPageId, imagesFolderPath);
		
		staticWebHelper.isExtraBooster = this.setFileName.contains("extra_pack");
		staticWebHelper.isLegacyEb = this.isLegacy;
		staticWebHelper.rotateClimax(this.getAllSetCards(), imagesFolderPath);
	}
	
	public void createWebPages() throws Exception{
		
		System.out.println("** Create Web Pages");
		
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		
		staticWebHelper.setId = this.setId;
		staticWebHelper.setName = this.setName;
		staticWebHelper.isLegacyTd = this.isLegacy;
		staticWebHelper.isLegacyEb = this.isLegacy;
		
		if(this.setFileName.contains("extra_pack")){
			ArrayList<Card> uniqueCards = this.getEbSetUniqueCards();
			staticWebHelper.generateCardPages_ArbitraryCards(uniqueCards);
			staticWebHelper.isExtraBooster = true;
			staticWebHelper.isLegacyEb = this.isLegacy;
			ArrayList<Card> baseCards = this.getEbSetBaseCards();
			staticWebHelper.generateCardPages_ArbitraryCards(baseCards);
		}
		else{
			ArrayList<Card> allCards = this.getAllSetCards();
			staticWebHelper.generateCardPages_ArbitraryCards(allCards);
		}
		
		staticWebHelper.regularCardCount = this.regularCardCount;
		staticWebHelper.extendedCardCount = this.extendedCardCount;
		staticWebHelper.promoCardCount = this.promoCardCount;
		staticWebHelper.tdCardCount = this.tdCardCount;
		
		staticWebHelper.createIndex_Main();
	}
	
	public ArrayList<Card> getAllSetCards() throws Exception{
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(allCards);
		this.regularCardCount = baseCards.size();
		
		if(!this.setTdFileName.equals("")){
			File setCleanTdFile = new File(conf.gethotcCleanFilesFolderPath() + this.setTdFileName + ".txt");
			ArrayList<Card> tdCards = HotcCleanFileParser.parseCards(setCleanTdFile);
			ArrayList<Card> baseTdCards = CardListUtilities.filterOutParallelCards(tdCards);
			this.tdCardCount = baseTdCards.size();
			allCards.addAll(tdCards);
		}
		
		File setCleanPromoFile = new File(conf.gethotcCleanFilesFolderPath() + this.promoFileName + ".txt");
		ArrayList<Card> promoCards = CardListUtilities.filterCards_FindSetPrs_All(HotcCleanFileParser.parseCards(setCleanPromoFile), this.setId);
		ArrayList<Card> basePrCards = CardListUtilities.filterOutParallelCards(promoCards);
		this.promoCardCount = basePrCards.size();
		allCards.addAll(promoCards);
		
		return allCards;
	}
	
	public ArrayList<Card> getEbSetBaseCards() throws Exception{
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(allCards);
		this.regularCardCount = baseCards.size();
		
		return baseCards;
	}
	
	public ArrayList<Card> getEbSetUniqueCards() throws Exception{
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		ArrayList<Card> uniqueCards = CardListUtilities.filterInParallelCards(allCards);
		
		if(!this.setTdFileName.equals("")){
			File setCleanTdFile = new File(conf.gethotcCleanFilesFolderPath() + this.setTdFileName + ".txt");
			ArrayList<Card> tdCards = HotcCleanFileParser.parseCards(setCleanTdFile);
			ArrayList<Card> baseTdCards = CardListUtilities.filterOutParallelCards(tdCards);
			this.tdCardCount = baseTdCards.size();
			uniqueCards.addAll(tdCards);
		}
		
		File setCleanPromoFile = new File(conf.gethotcCleanFilesFolderPath() + this.promoFileName + ".txt");
		ArrayList<Card> promoCards = CardListUtilities.filterCards_FindSetPrs_Pr(HotcCleanFileParser.parseCards(setCleanPromoFile), this.setId);
		ArrayList<Card> basePrCards = CardListUtilities.filterOutParallelCards(promoCards);
		this.promoCardCount = basePrCards.size();
		uniqueCards.addAll(promoCards);
		ArrayList<Card> extendedCards = CardListUtilities.filterCards_FindSetPrs_Extended(HotcCleanFileParser.parseCards(setCleanPromoFile), this.setId);
		ArrayList<Card> baseExtendedCards = CardListUtilities.filterOutParallelCards(extendedCards);
		this.extendedCardCount = baseExtendedCards.size();
		uniqueCards.addAll(baseExtendedCards);
		
		return uniqueCards;
	}
}
