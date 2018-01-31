package translations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import cards.Card;

public class Utilities {

	public static String escapeForSql(String sql){
		String result = sql.replace("\\","\\\\");
		result = result.replace("'","\\'");
		return result;
	}
	
	public static ArrayList<File> getFilesInFolder(String path){
		
		ArrayList<File> files = new ArrayList<File>();
		
		File folder = new File(path);
		File[] allFiles = folder.listFiles();

		for (File file : allFiles) {
		    if (file.isFile()) {
		    	files.add(file);
		    }
		}
		
		return files;
	}
	
	public static ArrayList<String> getFileNamesInFolder(String path){
		
		ArrayList<File> files = Utilities.getFilesInFolder(path);
		ArrayList<String> fileNames = new ArrayList<String>();
		
		for(File file : files){
			fileNames.add(file.getName());
		}
		
		return fileNames;
	}
	
	
	public static void getSetEquivalences() throws Exception{
		ArrayList<File> allFiles =  Utilities.getFilesInFolder("D:\\WorkShop\\Translations\\TranslationPlainFiles\\HotcCleanFiles");
		for (File file : allFiles) {
		    if (file.isFile()) {
				BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

				String fullPathWrite = Conf.defaultFolder + "\\ResultTemp.txt";
				Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite, true), "UTF-8"));
				writer.write(file.getName().replace(".txt", "") + "\t");
				String line = readerBase.readLine();
				line = line.replaceAll("(.+?) Extra Pack Translation", "Extra Booster\t$1");
				line = line.replaceAll("(.+?) Trial Deck Translation", "Trial Deck\t$1");
				line = line.replaceAll("(.+?) Booster Pack Translation", "Booster Pack\t$1");
				line = line.replaceAll("(.+?) Power Up Set Translation", "Power Up Set\t$1");
				line = line.replaceAll("(.+?) Extra Trial Translation", "Extra Trial\t$1");
				writer.write(line + "");
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = readerBase.readLine();
				line = line.replaceAll("Card No.: (.+?)-.+?  Rarity: .+", "\t$1");
				writer.write(line + "\n");
				
				readerBase.close();
				writer.close();
		    }
		}
		
	}
	
	public static void fullAbilityList() throws Exception{
		
		String fullPathWrite = Conf.defaultFolder + "\\ResultTemp.txt";
		
		File result = new File(fullPathWrite);
		if (result.exists())
			result.delete();
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result, true), "UTF-8"));
		
		ArrayList<File> allFiles =  Utilities.getFilesInFolder("D:\\WorkShop\\Translations\\TranslationPlainFiles\\HotcCleanFiles");
		
		HashMap<String,ArrayList<String>> all = new HashMap<String,ArrayList<String>>();  
		
		for (File file : allFiles) {
		    if (file.isFile()) {
		    	
		    	ArrayList<Card> cards = TextFileParser.parseCards(file);
		    	
		    	for(Card card : cards){
		    		
		    		for(String hab: card.habs){
		    			if(all.containsKey(hab)){
		    				ArrayList<String> ids = all.get(hab);
		    				ids.add(card.id);
		    				all.put(hab, ids);
		    			}
		    			else{
		    				ArrayList<String> ids = new ArrayList<String>();
		    				ids.add(card.id);
		    				all.put(hab, ids);
		    			}
		    		}
		    	}
		    	
		    }
		}
		
		for(String hab : all.keySet()){
			writer.write(hab + "\t");
			for(String id : all.get(hab)){
				writer.write(id + ";");
			}
			writer.write("\n");
		}

		writer.close();
	}
	
	public static ArrayList<String> getAbilitiesFromcards (ArrayList<Card> cards){
		ArrayList<String> abilities = new ArrayList<String>();
		
		for(Card card : cards){
			for(String ability : card.habs){
				if(!abilities.contains(ability)){
					abilities.add(ability);
				}
			}
		}
		
		return abilities;
	}
	
	// One shot utilities
	
	public static void splitImagesBySide() throws Exception{
		
		System.out.println("*** splitImagesBySide **");
		
		ArrayList<String> already = new ArrayList<String>();
		
		String basePathOld = "C:\\Users\\Moises BSS\\Desktop\\PruebasTemporales\\images";
		String basePathNew = "C:\\Users\\Moises BSS\\Desktop\\PruebasTemporales\\imagesNew";
		
		File mainFolder = new File(basePathOld);
		
		File[] allFiles = mainFolder.listFiles();

		for (File folder : allFiles) {
	        
			String folderName = folder.getName();			
			//System.out.println(folderName);
			
			boolean isWeiss = folderName.matches(".{2,3}?_WE?\\d\\d");
			boolean isSchwarz = folderName.matches(".{2,3}?_SE?\\d\\d");

			String newFolder = folderName;
			
			if(isWeiss){
				newFolder = "W_Images";
			}
			else if(isSchwarz){
				newFolder = "S_Images";
			}
			else{
				newFolder = "Others//" + folderName;
			}
			
			File[] allImages = folder.listFiles();
			
			for (File image : allImages) {
		        //System.out.println(file.getName());AG_SPR-P01
				//System.out.println(newFolder);
				//wsscrapping.Utilities.outputFolder(basePathNew + "\\" + newFolder);
				String newPathName = basePathNew + "\\" + newFolder + "\\" + image.getName();
				File newFile = new File(newPathName);
				Files.copy(image.toPath(), newFile.toPath());
			}
			
		}
		
	}
	
	public static ArrayList<Card> getNonParallelCards(ArrayList<Card> allCards){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.matches(".+\\da?")){
					cards.add(card);
				}
			}
		}
		
		return cards;
	}
	
	public static void getTranslationPairsFiles() throws Exception{
		for(String fileName : Conf.translationPairsFiles){
			URL website = new URL(Conf.remoteFilesURL + fileName + ".txt");
			InputStream in = website.openStream();
			File file = new File(Conf.resultsFolder + fileName + ".txt");
		    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public static TreeMap<String,ArrayList<String>> abilityMasterList(ArrayList<Card> cards) throws Exception{
		
		HashMap<String,ArrayList<String>> allAbilities = new HashMap<String,ArrayList<String>>();
		
		for(Card card : cards){
			for(String ability : card.habs){
				if(!allAbilities.containsKey(ability)){
					ArrayList<String> cardIds = new ArrayList<String>();
					cardIds.add(card.id);
					allAbilities.put(ability, cardIds);
				}
				else{
					allAbilities.get(ability).add(card.id);
				}
			}
		}
		
		TreeMap<String,ArrayList<String>> allAbilitiesSorted = new TreeMap<String,ArrayList<String>>(allAbilities);
		
		return allAbilitiesSorted;
	}
}
