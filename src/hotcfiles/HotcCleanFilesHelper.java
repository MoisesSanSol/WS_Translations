package hotcfiles;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import configuration.LocalConf;

public class HotcCleanFilesHelper {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcCleanFilesHelper hotcCleanFilesHelper = new HotcCleanFilesHelper();
		
		//hotcCleanFilesHelper.generateCleanHotcFile("");
		hotcCleanFilesHelper.generateCleanMissingHotcFiles();
		hotcCleanFilesHelper.generateCleanPromoHotcFiles();
		
		System.out.println("*** Finished ***");
	}
	
	public HotcCleanFilesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void generateCleanPromoHotcFiles() throws Exception{
		System.out.println("** Generate Clean Promo Hotc Files");
		
		// Hardcoded promo file names (seem to be static)
		String[] promoRawFileNames = {"weib_promos","schwarz_promos"};
		
        for(String promoRawFileName : promoRawFileNames){
        		
    		this.generateCleanHotcFile(promoRawFileName);
		}
	}
	
	public void generateCleanMissingHotcFiles() throws Exception{
		System.out.println("** Generate Clean Missing Hotc Files");
		
		File[] rawFiles = conf.hotcRawFilesFolder.listFiles();
		File[] cleanFiles = conf.hotcCleanFilesFolder.listFiles();

		for(File rawFile : rawFiles){
			boolean missing = true;
        	for(File cleanFile : cleanFiles){
        		if(rawFile.getName().equals(cleanFile.getName())){
        			missing = false;
        		}
        	}
    		if(missing) {
    			this.generateCleanHotcFile(rawFile.getName().replace(".txt", ""));
    		}
		}
	}
	
	public void generateCleanHotcFile(String rawFileName) throws Exception{
		
		System.out.println("** Unwraping Original File : " + rawFileName);
		
		String fullPathRawFile = this.conf.gethotcRawFilesFolderPath() + rawFileName + ".txt";
		File rawFile = new File(fullPathRawFile);
		
		String fullPathResultFile = this.conf.gethotcCleanFilesFolderPath() + rawFileName + ".txt";
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

		//TextFileParser.cleanDoubleLineBreaks(resultFile);
	}
	
	/*public void updateHotcCleanFiles_LineBasedReplacement() throws Exception{
		
		System.out.println("*** Update HotC Clean Files based on Line Replacement ***");

		File linesFile = new File(Conf.correctionFilesFolder + this.conf.lineBasedCorrectionFile + ".txt");
		HashMap<String,String> linePairs = TextFileParser.getHashMapFromFile(linesFile);
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(this.conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			boolean updated = false;
			
			for (int i = 0; i < fileContent.size(); i++) {
				for(String key : linePairs.keySet()) {
					if(fileContent.get(i).equals(key)){
						fileContent.set(i, linePairs.get(key));
				        System.out.println("Updated file: " + file.getName() + ", replaced: " + key + " with: " + linePairs.get(key) + " at line: " + i);
				        updated = true;
					}
				}
			}

			if(updated){
		        //System.out.println("Replacements made in file: " + file.getName());
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}*/
	/*
	public void updateHotcCleanFiles_RemoveLines() throws Exception{
		
		System.out.println("*** Update HotC Clean Files Remove Lines ***");

		File linesFile = new File(this.conf.correctionFilesFolder + this.conf.removeLinesCorrectionFile + ".txt");
		HashMap<String,ArrayList<String>> linesToBeRemoved = new HashMap<String,ArrayList<String>>();
		
		List<String> confFileContent = new ArrayList<>(Files.readAllLines(linesFile.toPath(), StandardCharsets.UTF_8));

		boolean getLineNumbers = false;
		ArrayList<String> lineNumbers = new ArrayList<String>();
		String fileName = "";
		
		while(confFileContent.size() > 0){
			
			String readLine = confFileContent.remove(0);
			
			if(readLine.equals("")){
				linesToBeRemoved.put(fileName, lineNumbers);
				lineNumbers = new ArrayList<String>();
				getLineNumbers = false;
			}
			else{
				if(getLineNumbers){
					lineNumbers.add(readLine);
				}
				else{
					fileName = readLine;
					getLineNumbers = true;
				}
			}
		}
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(this.conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){

			String currentFileName = file.getName().replace(".txt", ""); 
			
			if(linesToBeRemoved.containsKey(currentFileName)){
				
				List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
				ArrayList<String> lineNumbersToBeRemoved = linesToBeRemoved.get(currentFileName);			
				
				for(String lineNumberStr : lineNumbersToBeRemoved){
					int lineNumber = Integer.parseInt(lineNumberStr);
					fileContent.remove(lineNumber);
					System.out.println("Updated file: " + file.getName() + ", removed line: " + lineNumber);
				}
				
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}*/
	/*
	public void updateHotcCleanFiles_StringBasedReplacement() throws Exception{
		
		System.out.println("*** Update HotC Clean Files based on String Replacement ***");

		File prefixFile = new File(this.conf.correctionFilesFolder + this.conf.stringReplacementBasedCorrectionFile + ".txt");
		HashMap<String,String> stringPairs = TextFileParser.getHashMapFromFile(prefixFile);
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(this.conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			boolean updated = false;
			
			for (int i = 0; i < fileContent.size(); i++) {
				for(String key : stringPairs.keySet()) {
					if(fileContent.get(i).contains(key)){
						String corrected = fileContent.get(i).replace(key, stringPairs.get(key));
				        fileContent.set(i, corrected);
				        System.out.println("Updated file: " + file.getName() + ", replaced: " + key + " with: " + stringPairs.get(key) + " at line: " + i);
				        updated = true;
					}
				}
			}

			if(updated){
		        //System.out.println("Replacements made in file: " + file.getName());
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}*/
	/*
	public void updateHotcCleanFiles_RegExpBasedReplacement() throws Exception{
		
		System.out.println("*** Update HotC Clean Files based on Regular Expression Replacement ***");

		File prefixFile = new File(this.conf.correctionFilesFolder + this.conf.regExpBasedCorrectionFile + ".txt");
		HashMap<String,String> regExpPairs = TextFileParser.getHashMapFromFile(prefixFile);

		HashMap<Pattern,String> patternPairs = new HashMap<Pattern,String>();
		for(String key : regExpPairs.keySet()) {
			Pattern pattern = Pattern.compile(key);
			patternPairs.put(pattern, regExpPairs.get(key));
		}
		
		
		ArrayList<File> allFiles = Utilities.getFilesInFolder(this.conf.hotcCleanFilesFolder);
		
		for(File file : allFiles){
			
			List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
			
			boolean updated = false;
			
			for (int i = 0; i < fileContent.size(); i++) {
				for(Pattern pattern : patternPairs.keySet()) {
					Matcher matcher = pattern.matcher(fileContent.get(i));
					if(matcher.find()){
						String corrected = matcher.replaceAll(patternPairs.get(pattern));
				        fileContent.set(i, corrected);
				        System.out.println("Updated file: " + file.getName() + ", replaced: " + pattern.pattern() + " with: " + patternPairs.get(pattern) + " at line: " + i);
				        updated = true;
					}
				}
			}

			if(updated){
		        //System.out.println("Replacements made in file: " + file.getName());
				Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
			}
		}
	}*/
	/*
	public void updateTranslationPairFiles_Order() throws Exception{
		
		System.out.println("*** Update Translation Pair Files Order Pairs ***");
		
		for(String fileName : this.conf.translationPairsFiles){
		
			System.out.println("*** Updating File: " + fileName + " ***");
			
			String fullPathReadBase = this.conf.translationPairsFolder + fileName + ".txt";
			BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(fullPathReadBase), "UTF-8"));
			
			HashMap<String,String> trads = new HashMap<String,String>(); 
			
			while(readerBase.ready()){
				
				String patternLine = readerBase.readLine();
				String replacementLine = readerBase.readLine();
				readerBase.readLine(); // Ignore line
				trads.put(patternLine, replacementLine);
			}
			
			readerBase.close();
			
			ArrayList<String> ordered = new ArrayList<String>(trads.keySet());
			Collections.sort(ordered);
			
			String fullPathWrite = this.conf.translationPairsFolder + fileName + ".txt";
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
			
			for(String key : ordered){
				
				writer.write(key + "\r\n");
				writer.write(trads.get(key) + "\r\n");
				writer.write("\r\n");
			}	
			writer.close();
			
			HotcCleanFilesHelper.removeLastLineFromFile(new File(fullPathWrite));
		}
	}*/
	/*
	public void removeLastLineFromFile(File file) throws Exception{
		
		System.out.println("*** Remove Last Line From File ***");
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
		
        fileContent.remove(fileContent.size()-1);
		
		Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
	}*/
	/*
	public void orderLinesInFile(File file) throws Exception{
		
		System.out.println("*** Order Lines In File ***");
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
		
		Collections.sort(fileContent);
		
		Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
	}*/
	/*
	public void updateRawAbilityFile_UniqueLines(File file) throws Exception{
		
		System.out.println("*** Update Raw Ability File Unique Lines ***");
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
		List<String> filteredFileContent = new ArrayList<String>();
		
		for(String line : fileContent){
			String onlyAbility = line.replaceAll("\t.+", "").replaceAll("\"$", "").replaceAll("^\"", "");
			if(!filteredFileContent.contains(onlyAbility)){
				filteredFileContent.add(onlyAbility);
			}
		}
		
		Files.write(file.toPath(), filteredFileContent, StandardCharsets.UTF_8);
	}*/
	/*
	public void backUpFiles(){
		
	}*/
}
