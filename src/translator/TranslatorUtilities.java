package translator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.Translator.LineTranslation;
import configuration.LocalConf;

public class TranslatorUtilities {

	private LocalConf conf;
	
	public TranslatorUtilities() throws Exception{
		this.conf = LocalConf.getInstance();
	}

	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		TranslatorUtilities utility = new TranslatorUtilities();
		LocalConf conf = LocalConf.getInstance();
		
		//File file = new File(conf.getTranslationPairsFolderPath() + "gurren_lagann_booster_pack.txt");
		//File result = new File(conf.getGeneralResultsFolderPath() + "result2.txt");
		
		System.out.println("*** Finished ***");
	}
	
	public void updateTranslationsPairsFullListWithSetFile(File setTranslationPairsFile) throws Exception{
		
		HashMap<String,String> fullTranslationsPairs = this.getTranslationsPairsFromFile_PairsFile(conf.translationPairsFullListFile);
		HashMap<String,String> setTranslationsPairs = this.getTranslationsPairsFromFile_SetFile(setTranslationPairsFile);
		
		for(String pattern : setTranslationsPairs.keySet()){
			if(!fullTranslationsPairs.containsKey(pattern)){
				fullTranslationsPairs.put(pattern, setTranslationsPairs.get(pattern));
			}
		}
		
		this.createFileFromTranslationPairs(fullTranslationsPairs, this.conf.translationPairsFullListFile);
	}
	
	public HashMap<String,String> getTranslationsPairsFromFile_PairsFile(File translationPairs) throws Exception{
		
		HashMap<String,String> translationsPairs = new HashMap<String,String>();
		
		List<String> content = new ArrayList<>(Files.readAllLines(translationPairs.toPath(), StandardCharsets.UTF_8));
		
		while(content.size() > 2){
			
			String patternLine = content.remove(0);
			String replacementLine = content.remove(0);
			content.remove(0); // Ignore line
			if(!replacementLine.equals("")){
				if(!translationsPairs.containsKey(patternLine)){
					translationsPairs.put(patternLine, replacementLine);
				}
			}
			else{
				throw new Exception("Argh!");
			}
		}
		
		return translationsPairs;
	}
	
	public HashMap<String,String> getTranslationsPairsFromFile_SetFile(File translationPairs) throws Exception{
		
		HashMap<String,String> translationsPairs = new HashMap<String,String>();
		
		List<String> content = new ArrayList<>(Files.readAllLines(translationPairs.toPath(), StandardCharsets.UTF_8));
		
		while(content.size() > 3){
			
			content.remove(0); // Ignore set ability line
			String patternLine = content.remove(0);
			String replacementLine = content.remove(0);
			content.remove(0); // Ignore blank line
			if(!replacementLine.equals("")){
				if(!replacementLine.equals("***")){
					if(!translationsPairs.containsKey(patternLine)){
						translationsPairs.put(patternLine, replacementLine);
					}
					else{
						System.out.println("* Already exists: " + patternLine);
					}
				}
				else{
					System.out.println("* Ignoring WIP patters: " + patternLine);
				}
			}
			else{
				throw new Exception("Argh!");
			}
		}
		
		return translationsPairs;
	}
	
	public void createFileFromTranslationPairs(HashMap<String,String> translationPairs, File file) throws Exception{
		
		ArrayList<String> content = new ArrayList<>();
		ArrayList<String> keys = new ArrayList<>(translationPairs.keySet());
		Collections.sort(keys);
		
		for(String pattern : keys){
			
			content.add(pattern);
			content.add(translationPairs.get(pattern));
			content.add("");
			
		}
		
		Files.write(file.toPath(), content, StandardCharsets.UTF_8);
	}
	
}
