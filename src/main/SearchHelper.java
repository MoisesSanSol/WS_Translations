package main;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import cards.Card;
import translator.LineTranslation;
import translator.TranslatorUtilities;
import utilities.Utilities;
import configuration.LocalConf;

public class SearchHelper {
	
	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		SearchHelper dispatcher = new SearchHelper();

		dispatcher.search_TranslationPairs_ReplaceBased();
		
		System.out.println("*** Finished ***");
	}
	
	public SearchHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public HashMap<String,String> search_TranslationPairs_ReplaceBased(String replaceStr) throws Exception{
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		
		HashMap<String,String> fullTranslationsPairs = utility.getTranslationsPairsFromFile_PairsFile(conf.translationPairsFullListFile);
		HashMap<String,ArrayList<String>> inverseTranslationsPairs = Utilities.getHashMap_ReverseHashMap(fullTranslationsPairs);

		for(String pattern : inverseTranslationsPairs.get(replaceStr)){
			pairs.put(pattern, replaceStr);
		}
		
		return pairs;
	}
	
	public void search_TranslationPairs_ReplaceBased() throws Exception{
		
		System.out.println("** Search Tranlation Pairs Replace Based");
		
		TranslatorUtilities utility = new TranslatorUtilities();
		
		String searchFilePath = this.conf.getGeneralResultsFolderPath() + "PairsSearch_Replace" + ".txt";
		File searchFile = new File(searchFilePath);
		String resultsFilePath = this.conf.getGeneralResultsFolderPath() + "PairsSearch_Replace_Results" + ".txt";
		File resultsFile = new File(resultsFilePath);
		
		if(Utilities.checkFileExistence(searchFile)){
			
			HashMap<String,String> translationPairs = new HashMap<String,String>();
			
			ArrayList<String> replacesStr = new ArrayList<String>(Files.readAllLines(searchFile.toPath(), StandardCharsets.UTF_8));
			
			while(replacesStr.size() > 0){
				
				String replaceStr = replacesStr.remove(0);
				
				if(!replaceStr.isEmpty()){
					translationPairs.putAll(this.search_TranslationPairs_ReplaceBased(replaceStr));
				}
			}
			
			utility.createFileFromTranslationPairs(translationPairs, resultsFile);
		}
	}
}
