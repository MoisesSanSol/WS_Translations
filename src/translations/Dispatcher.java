package translations;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

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
	
	public static void excelForCollaborator(String set) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(set);
		ArrayList<Card> cards = Utilities.getNonParallelCards(allCards);
		ExcelHelper.createCollaboratorExcel(cards, set);
	}
	
	public static void excelForCollaboratorEB(String set) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(set);
		ArrayList<Card> cards = Utilities.getNonParallelCards(allCards);
		ExcelHelper.createCollaboratorExcel_EB(cards, set);
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
		
		OutputFormatter.generateRawAbilityListFile(uniqueCards, file);
	}
	
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
		
		OutputFormatter.generateRawAbilityListFile(uniqueCards, file);
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
	
}
