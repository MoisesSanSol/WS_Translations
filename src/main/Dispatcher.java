package main;

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

public class Dispatcher {

	private LocalConf conf;
	
	String setName = "Devil Survivor 2 Anime";
	String setFileName = "devil_survivor_2_anime_extra_pack";
	String setTdFileName = "";
	String setId = "DS2/SE16";
	String setFileId = "DS2_SE16";
	String setLaPageId = "129";
	String setYytPageId = "ds2ext";
	String promoFileName = "schwarz_promos";
	//String promoFileName = "weib_promos";
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		Dispatcher dispatcher = new Dispatcher();
		
		/* Creating translations for set */
		//dispatcher.createSetTranslationRelatedFiles_Init();
		//dispatcher.createSetTranslationRelatedFiles_Ongoing();
		
		/* Getting images for set web */
		dispatcher.checkWebFolders();
		dispatcher.createWebImages_NoCotd();
		
		/* Creating web pages */
		//dispatcher.createWebPages();
		
		/* Other shit that will end in other place */
		//dispatcher.createTranslationReferenceFile_AllSets();
		//dispatcher.createTranslationProgressFile_AllSets();
		
		System.out.println("*** Finished ***");
	}
	
	public Dispatcher() throws Exception{
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
		File baseFolder = new File(this.conf.getGeneralResultsFolderPath() + this.setFileId);
		File cardsFolder = new File(this.conf.getGeneralResultsFolderPath() + this.setFileId + "\\cards");
		File imagesFolder = new File(this.conf.getGeneralResultsFolderPath() + this.setFileId + "\\images");
		Utilities.checkFolderExistence(baseFolder);
		Utilities.checkFolderExistence(cardsFolder);
		Utilities.checkFolderExistence(imagesFolder);
	}
	
	public void createWebImages_NoCotd() throws Exception{
		
		System.out.println("** Create Web Images (No Cotd)");
		
		DownloadHelper downloadHelper = new DownloadHelper();
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		
		String imagesFolderPath = this.conf.getGeneralResultsFolderPath() + this.setFileId + "\\images\\";
		
		downloadHelper.downloadImages_LittleAkiba_SetGaps(this.setLaPageId, imagesFolderPath);
		
		System.out.println("* Check missing images and proceed to yyt to fill gaps. Press enter to continue:");
		System.in.read();
		
		downloadHelper.downloadImages_Yuyutei_SetGaps(this.setYytPageId, imagesFolderPath);
		
		staticWebHelper.rotateClimax(this.getAllSetCards(), imagesFolderPath);
	}
	
	public void createWebPages() throws Exception{
		
		System.out.println("** Create Web Pages");
		
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		
		staticWebHelper.setId = this.setId;
		staticWebHelper.setName = this.setName;
		staticWebHelper.isLegacyTd = true;
		
		staticWebHelper.regularCardCount = 100;
		staticWebHelper.extendedCardCount = 5;
		staticWebHelper.promoCardCount = 17;
		staticWebHelper.tdCardCount = 21;
		
		ArrayList<Card> allCards = this.getAllSetCards();
		
		staticWebHelper.generateCardPages_ArbitraryCards(allCards);
		staticWebHelper.createIndex_Main();
		
	}
	
	public ArrayList<Card> getAllSetCards() throws Exception{
		
		File setCleanFile = new File(conf.gethotcCleanFilesFolderPath() + this.setFileName + ".txt");
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setCleanFile);
		if(!this.setTdFileName.equals("")){
			File setCleanTdFile = new File(conf.gethotcCleanFilesFolderPath() + this.setTdFileName + ".txt");
			allCards.addAll(HotcCleanFileParser.parseCards(setCleanTdFile));
		}
		File setCleanPromoFile = new File(conf.gethotcCleanFilesFolderPath() + this.promoFileName + ".txt");
		ArrayList<Card> promoCards = CardListUtilities.filterCards_FindSetPrs(HotcCleanFileParser.parseCards(setCleanPromoFile), this.setId);
		allCards.addAll(promoCards);
		return allCards;
	}
}
