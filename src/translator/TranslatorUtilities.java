package translator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.Translator.LineTranslation;
import configuration.LocalConf;

public class TranslatorUtilities {

	private LocalConf conf;
	
	public ArrayList<LineTranslation> lineTranslations;
	
	public TranslatorUtilities() throws Exception{
		this.conf = LocalConf.getInstance();
	}

	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		TranslatorUtilities utility = new TranslatorUtilities();
		LocalConf conf = LocalConf.getInstance();
		
		File file = new File(conf.getTranslationPairsFolderPath() + "saekano_-_how_to_raise_a_boring_girlfriend_booster_pack.txt");
		utility.setTranslationsPairsInFile(utility.getTranslationsPairsFromFile(file));
		
		System.out.println("*** Finished ***");
	}
	
	public void addTranslationsPairsFromSet(File setTranslationPairsFile) throws Exception{
		
		HashMap<String,String> fullTranslationsPairs = this.getTranslationsPairsFromFile(conf.translationPairsFullListFile);
		HashMap<String,String> setTranslationsPairs = this.getTranslationsPairsFromFile(setTranslationPairsFile);
		
		for(String pattern : setTranslationsPairs.keySet()){
			if(!fullTranslationsPairs.containsKey(pattern)){
				fullTranslationsPairs.put(pattern, setTranslationsPairs.get(pattern));
			}
		}
		
	}
	
	public HashMap<String,String> getTranslationsPairsFromFile(File translationPairs) throws Exception{
		
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
	
	public void setTranslationsPairsInFile(HashMap<String,String> translationPairs) throws Exception{
		
		ArrayList<String> content = new ArrayList<>();
		
		for(String pattern : translationPairs.keySet()){
			
			content.add(pattern);
			content.add(translationPairs.get(pattern));
			content.add("");
			
		}
		
		Files.write(this.conf.translationPairsFullListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
}
