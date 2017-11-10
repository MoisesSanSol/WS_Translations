package translations;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class Dispatcher {

	public static void generateSetSqlInserts(String file, String set, String grupo) throws Exception{
		ArrayList<Card> allSetCards = TextFileParser.parseCards(file);
		Translator translator = new Translator();
		ArrayList<Card> allSetTranslatedCards = translator.translateSet(allSetCards);
		OutputFormatter.sqlInsert(allSetCards, file, set, grupo, "EN");
		OutputFormatter.sqlInsert(allSetTranslatedCards, file + "_translated", set, grupo, "ES");
	}
	
	public static void readyNewHotcRawFiles() throws Exception{
		
		/*System.out.println("*** Get New HotC Raw Files ***");
		
		ArrayList<String> newRawFilesNames = GetHotcFiles.downloadNewHotcRawFiles();
		
		for(String fileName : newRawFilesNames){
			TextFileParser.generateCleanHotcFile(fileName.replace(".txt", ""));
		}*/
	}
	
	public static void abilityMasterList() throws Exception{
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(Conf.hotcCleanFilesFolder);
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File file : allFiles){
			ArrayList<Card> setcards = TextFileParser.parseCards(file);
			allCards.addAll(setcards);
		}
		
		OutputFormatter.generateAbilityMasterLists(allCards);
		OutputFormatter.generateAbilityFile(allCards, "AbilityMasterList");
	}
	
	public static void rawAbilityMasterList() throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "RawAbilityMasterList.txt";
		File file = new File(fullPathWrite);
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards); 
		
		OutputFormatter.generateAbilityListFile_WithIds(uniqueCards, file);
	}
	
	public static void abilities_MasterList() throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "AbilitiesMasterList_NoIds.txt";
		File file = new File(fullPathWrite);
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards); 
		
		OutputFormatter.generateAbilityListFile_NoIds(uniqueCards, file);
	}
	
	/*public static void justAbilities_SetList(String setName) throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "JustAbilities_" + setName + ".txt";
		File file = new File(fullPathWrite);
		
		ArrayList<Card> setCatds = TextFileParser.parseCards(setName);
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(setCatds); 
		
		OutputFormatter.generateJustAbilitiesListFile(uniqueCards, file);
	}*/
	
	
	public static void rawRemainingAbilityMasterList() throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "RawRemainingAbilityMasterList.txt";
		File file = new File(fullPathWrite);
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards); 
		
		OutputFormatter.generateRawRemainingAbilityListFile(uniqueCards, file);
		FileUpdater.updateRawAbilityFile_UniqueLines(file);
	}
	
	public static void rawTranslatedAbilityMasterList() throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "RawTranslatedAbilityMasterList.txt";
		File file = new File(fullPathWrite);
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards); 
		
		OutputFormatter.generateRawTranslatedAbilityListFile(uniqueCards, file);
	}
	
	public static void allRawAbilityMasterLists() throws Exception{
		String fullPathWrite = Conf.resultsFolder + "RawAbilityMasterList.txt";
		File file = new File(fullPathWrite);
		String fullPathWrite1 = Conf.resultsFolder + "RawRemainingAbilityMasterList.txt";
		File file1 = new File(fullPathWrite1);
		String fullPathWrite2 = Conf.resultsFolder + "RawTranslatedAbilityMasterList.txt";
		File file2 = new File(fullPathWrite2);
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards);
		
		//OutputFormatter.generateRawAbilityListFile(uniqueCards, file);
		OutputFormatter.generateRawRemainingAbilityListFile(uniqueCards, file1);
		OutputFormatter.generateRawTranslatedAbilityListFile(uniqueCards, file2);
	}
	
	public static void generateCorrectedFiles() throws Exception{
		
		/*File correctionsFile = new File(Conf.correctionFilesFolder + "Corrections.txt");
		TextFileParser.generateLineUpdatedFiles(correctionsFile);
		File partialCorrectionsFile = new File(Conf.correctionFilesFolder + "PartialCorrections.txt");
		TextFileParser.generateStartLineUpdatedFiles(partialCorrectionsFile);
		File replaceCorrectionsFile = new File(Conf.correctionFilesFolder + "ReplaceCorrections.txt");*/
		//FileUpdater.updateHotcCleanFiles_PrefixBasedReplacement();
		FileUpdater.updateHotcCleanFiles_RemoveLines();
		FileUpdater.updateHotcCleanFiles_StringBasedReplacement();
		FileUpdater.updateHotcCleanFiles_RegExpBasedReplacement();
		FileUpdater.updateHotcCleanFiles_LineBasedReplacement();
	}
	
	
	public static void getTranlationProgress(String set) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(set);
		ArrayList<Card> cards = Utilities.getNonParallelCards(allCards);
		OutputFormatter.abilityReport(cards, set);
		
	}
	
	public static void getOverlapReport(String set1, String set2) throws Exception{
		
		ArrayList<Card> allCards1 = TextFileParser.parseCards(set1);
		ArrayList<Card> cards1 = Utilities.getNonParallelCards(allCards1);
		ArrayList<Card> allCards2 = TextFileParser.parseCards(set2);
		ArrayList<Card> cards2 = Utilities.getNonParallelCards(allCards2);
		
		OutputFormatter.overlapReport(set1, cards1, set2, cards2);
		
	}

	public static void generateAllHotcCleanFiles() throws Exception{
		
		System.out.println("*** Regenerating Hotc Clean Files ***");
		
		File directorio = new File(Conf.hotcRawFilesFolder);
		File[] rawFiles = directorio.listFiles();
		
		for(File file : rawFiles){
			TextFileParser.generateCleanHotcFile(file.getName().replace(".txt", ""));
		}
	}
	
	public static void generateNewHotcCleanFiles() throws Exception{
		
		System.out.println("** Generating New Hotc Clean Files");
		
		File directorioRaw = new File(Conf.hotcRawFilesFolder);
		File[] rawFiles = directorioRaw.listFiles();
		
		File directorioClean = new File(Conf.hotcCleanFilesFolder);
		String[] cleanFilesArray = directorioClean.list();
		ArrayList<String> cleanFiles = new ArrayList<>(Arrays.asList(cleanFilesArray));
		
		for(File file : rawFiles){
			if(!cleanFiles.contains(file.getName())){
				TextFileParser.generateCleanHotcFile(file.getName().replace(".txt", ""));
				System.out.println("** Generating New Hotc Clean Files: " + file.getName());
			}
		}
	}
	
	public static void generateIdNamePairsFile() throws Exception{
		
		System.out.println("*** Generate Id Name Pairs File ***");
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(Conf.hotcCleanFilesFolder);
		ArrayList<Card> allCards = new ArrayList<Card>();
		
		for(File file : allFiles){
			ArrayList<Card> setcards = TextFileParser.parseCards(file);
			allCards.addAll(setcards);
		}
		
		OutputFormatter.generateIdNamePairsFile(allCards);
	}
	
	public static void downloadAllSetImagesFromYuyutei(String set) throws Exception{
		
		System.out.println("*** Download All Set Images for " + set + " From Yuyutei ***");
		
		DownloadHelper.downloadAllSetImages_Yuyutei(set);
	}
	
	public static void testing() throws Exception{
		//File file = new File(Conf.resultsFolder + "imagen.jpg");
		//DownloadHelper.downloadFile("http://yuyu-tei.jp/card_image/ws/front/gochiusa/10082.jpg", file);
		DownloadHelper.downloadAllSetImages_Yuyutei("gochiusaext");
	}
	
	public static void updateFiles_OrderPairs_TranslationPairsFiles() throws Exception{
		
		FileUpdater.updateTranslationPairFiles_Order();
		
	}
	
}
