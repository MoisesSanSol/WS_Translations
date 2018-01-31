package translations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cards.Card;

public class TextFileParser {

	public static ArrayList<Card> getAllCards() throws Exception{
		
    	System.out.println("*** Get All Cards ***");
		
    	ArrayList<File> allFiles = Utilities.getFilesInFolder(Conf.hotcCleanFilesFolder);
    	ArrayList<Card> allCards = new ArrayList<Card>();
    	
    	for(File file : allFiles){
    		ArrayList<Card> setcards = TextFileParser.parseCards(file);
    		allCards.addAll(setcards);
    	}
    	
    	return allCards;
	}
	
	public static ArrayList<Card> parseCards(String setName) throws Exception{
		
    	System.out.println("*** Parsing Set : " + setName + " ***");
		
    	String fullPathSetFile = Conf.hotcCleanFilesFolder + setName + ".txt";
    	File setFile = new File(fullPathSetFile);
    	
    	return TextFileParser.parseCards(setFile);
	}

	public static ArrayList<Card> parseCards(File mainFile) throws Exception{
		
    	System.out.println("*** Parsing Set File: " + mainFile.getName() + " ***");
		
		ArrayList<Card> cards = new ArrayList<Card>();
		
		BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(mainFile), "UTF-8"));
		
		// Skip header
		while (!readerBase.readLine().startsWith("=")){
			// Do nothing
		}
		
		boolean keep = true;
		
		while(keep){
			
			readerBase.readLine();
			String line = readerBase.readLine();
			
			if (line == null){
				keep = false;
			}
			else{
				
				String nameLine = line;
				String jpNameLine = readerBase.readLine();
				String idLine = readerBase.readLine();
				String colorLine = readerBase.readLine();
				String levelLine = readerBase.readLine();
				String traitLine = readerBase.readLine();
				String triggerLine = readerBase.readLine();
				ArrayList<String> habLines = new ArrayList<String>();
				
				// Flavor ignored
				while (!readerBase.readLine().equals("TEXT: ")){
					// Do nothing
				}
				
				boolean loopBreak = false;
				habLines.add(readerBase.readLine());
				while (!loopBreak){
					line = readerBase.readLine();
					if(line.equals("")){
						loopBreak = true;
						readerBase.readLine();
					}
					else{
						habLines.add(line);
					}
				}
				
				Card card = new Card(nameLine, jpNameLine, idLine, colorLine, levelLine, traitLine, triggerLine, habLines);
				cards.add(card);
			}
		}
		
		readerBase.close();
		
		return cards;
	}
	
	public static void generateCleanHotcFile(String rawFileName) throws Exception{
		
		System.out.println("*** Unwraping Original File : " + rawFileName + " ***");
		
		String fullPathRawFile = Conf.hotcRawFilesFolder + rawFileName + ".txt";
		File rawFile = new File(fullPathRawFile);
		
		String fullPathResultFile = Conf.hotcCleanFilesFolder + rawFileName + ".txt";
		File resultFile = new File(fullPathResultFile);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rawFile), "UTF-8"));
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8"));
		
		while(reader.ready()){
			
			String line = reader.readLine();
			
			if(line.startsWith("TEXT: ")){
				writer.write("TEXT: \r\n");
				String newLine = line.replace("TEXT: ", "");
				boolean loopBreak = false;
				while (!loopBreak){
					line = reader.readLine();
					if(line.equals("")){
						loopBreak = true;
						writer.write(newLine + "\r\n\r\n");
					}
					else if(line.startsWith("[S]") || line.startsWith("[A]") || line.startsWith("[C]")){
						writer.write(newLine + "\r\n");
						newLine = line;
					}
					else{
						newLine = newLine + " " + line;
					}
				}
				
			}
			else{
				writer.write(line + "\r\n");
			}
	
		}
		
		reader.close();
		writer.close();

		TextFileParser.cleanDoubleLineBreaks(resultFile);
	}
	
	public static void cleanDoubleLineBreaks(File file) throws Exception{
		
		System.out.println("*** Cleaning Line Jumps from File : " + file.getName() + " ***");
		
		String fullPathResultFile = Conf.defaultFolder + "\\" + file.getName().replace(".txt", "_Temp.txt");
		
		File resultFile = new File(fullPathResultFile);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8"));
		
		while(reader.ready()){
			
			String line = reader.readLine();
			writer.write(line + "\r\n");
			
			if(line.equals("")){
				
				if(reader.ready()){
					
					boolean keepChecking = true;
					
					while(keepChecking){
						
						String nextLine = reader.readLine();
						
						if(!nextLine.equals("")){
							writer.write(nextLine + "\r\n");
							keepChecking = false;
						}
					}
				}
			}
		}
		
		reader.close();
		writer.close();
		
		Files.move(resultFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	public static String getSetName(File mainFile) throws Exception{
		
		System.out.println("*** Getting Set Name from File: " + mainFile.getName() + " ***");
		
		String result = mainFile.getName();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(mainFile), "UTF-8"));
		
		String line = reader.readLine();
		reader.close();
		
		line = line.replaceAll("(.+?) Extra Pack Translation", "$1 - Extra Booster");
		line = line.replaceAll("(.+?) Trial Deck Translation", "$1 - Trial Deck");
		line = line.replaceAll("(.+?) Booster Pack Translation", "$1 - Booster Pack");
		line = line.replaceAll("(.+?) Power Up Set Translation", "$1 - Power Up Set");
		line = line.replaceAll("(.+?) Extra Trial Translation", "$1 - Extra Trial");
		
		result = line;
		
		return result;
	}
	
	public static void generateLineUpdatedFiles(File corrections) throws Exception{
		
		System.out.println("*** Generate Line Replacement based Updated Files from " + corrections.getName() + "***");
		
		HashMap<String,String> replacements = new HashMap<String,String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(corrections), "UTF-8"));
		
		String replacement = reader.readLine().replaceAll("^\"(.+)\"\t.+$", "$1");
		//System.out.println("Replacing :");
		
		while(reader.ready()){
			String line = reader.readLine().replaceAll("^\"(.+)\"\t.+$", "$1");
			if(line.equals("")){
				replacement = reader.readLine().replaceAll("^\"(.+)\"\t.+$", "$1");
			}
			else{
				replacements.put(line, replacement);
			}
		}
		
		reader.close();
		
		//System.out.println("with: " + replacement);
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(Conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			boolean updated = false;
			
			for (int i = 0; i < fileContent.size(); i++) {
				if(replacements.containsKey(fileContent.get(i))){
			        fileContent.set(i, replacements.get(fileContent.get(i)));
			        updated = true;
				}
			}

			if(updated){
		        System.out.println("Replacements made in file: " + file.getName());
				//File newFile = new File(Conf.resultsFolder + file.getName());
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}
	
	public static void generateReplacementUpdatedFiles(File replaceCorrections) throws Exception{
		
		System.out.println("*** Generate Replacement based Updated Files from " + replaceCorrections.getName() + "***");
		
		HashMap<String,String> replacements = new HashMap<String,String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(replaceCorrections), "UTF-8"));
		
		while(reader.ready()){
			replacements.put(reader.readLine(), reader.readLine());
		}
		
		reader.close();
		
		//System.out.println("with: " + replacement);
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(Conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			boolean updated = false;
			
			for (int i = 0; i < fileContent.size(); i++) {
				for(String key : replacements.keySet()) {
					if(fileContent.get(i).contains(key)){
						String corrected = fileContent.get(i).replace(key, replacements.get(key));
				        fileContent.set(i, corrected);
				        updated = true;
					}
				}
			}

			if(updated){
		        System.out.println("Replacements made in file: " + file.getName());
				//File newFile = new File(Conf.resultsFolder + file.getName());
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}
	
	public static HashMap<String,String> getHashMapFromFile(File file) throws Exception{
		
		System.out.println("*** Get Pairs From " + file.getName() + " ***");
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		String target = reader.readLine();
		
		while(reader.ready()){
			String key = reader.readLine();
			if(key.equals("")){
				target = reader.readLine();
			}
			else{
				map.put(key, target);
			}
		}
		
		reader.close();
		
		return map;
	}
	
}
