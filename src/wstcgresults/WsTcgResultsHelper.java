package wstcgresults;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import configuration.LocalConf;

public class WsTcgResultsHelper {

	
	public static HashMap<String,String> getTextTranslationPairs() throws Exception{
		
		HashMap<String,String> textTranslationPairs = new HashMap<String,String>();
	
		LocalConf conf = LocalConf.getInstance();
		
		Properties properties = new Properties();
		InputStream input = new FileInputStream(conf.getTranslationPairsFolderPath() + "wstcgResultsTranlationPairs.properties");
		properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
		input.close();

		textTranslationPairs = new HashMap<String,String>((Map)properties);
		
		return textTranslationPairs;
	}
	
	
	public static HashMap<String,String> getCurrentResultsPages() throws Exception{
		
		HashMap<String,String> currentResultsPages = new HashMap<String,String>();
		
		LocalConf conf = LocalConf.getInstance();
		
		Properties properties = new Properties();
		InputStream input = new FileInputStream("D:\\Workshop\\Translations\\TranslationPlainFiles\\Resources\\currentWstcgResultsPages.properties");
		
		properties.load(input);
		input.close();

		currentResultsPages = new HashMap<String,String>((Map)properties);
		
		return currentResultsPages;
	}
}
