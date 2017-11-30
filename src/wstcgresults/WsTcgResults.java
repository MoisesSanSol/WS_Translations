package wstcgresults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import configuration.LocalConf;
import translations.TextFileParser;
import utilities.Utilities;

public class WsTcgResults {

	
	private static String urlBase = "http://ws-tcg.com/deckrecipe/";
	//static String urlPage = "detail/recipe_wgp2017_nagoya_01";
	
	private HashMap<String,String> idNamePairs;
	
	public WsTcgResults() throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		Utilities.checkFolderExistence(conf.wsTcgResultsFolder);
		File pairsFile = new File(conf.wsTcgResultsFolder.getAbsolutePath() + "\\IdNamePairs.txt"); 
		this.idNamePairs = TextFileParser.getHashMapFromFile(pairsFile);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");

		WsTcgResults wsTcgResults = new WsTcgResults();
		
		//wsTcgResults.generateUpdatedResultsPage(WsTcgResults.urlBase + WsTcgResults.urlPage, true);
		//wsTcgResults.generateUpdatedResultsPage(WsTcgResults.urlBase + WsTcgResults.urlPage, false);
		
		wsTcgResults.generateAllResultPagesFor("recipe_wgp2017");
		
		System.out.println("*** Finished ***");
	}
	
	public void generateAllResultPagesFor(String what) throws Exception{
		
		System.out.println("** generateAllResultPagesFor");
		
		Document doc = Jsoup.connect(WsTcgResults.urlBase).maxBodySize(0).get();	
		Elements links = doc.select("a[href*=" + what + "]");
		
		for(Element link : links){
			String url = link.attr("abs:href");
			System.out.println("* url: " + url);
			this.generateUpdatedResultsPage(url, true);
			this.generateUpdatedResultsPage(url, false);
		}
	}
	
	public void generateUpdatedResultsPage(String url, boolean mobile) throws Exception{
		
		LocalConf conf = LocalConf.getInstance();
		
		Document doc;
		if(mobile){
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Mobile Safari/537.36").maxBodySize(0).get();
		}
		else{
			doc = Jsoup.connect(url).maxBodySize(0).get();	
		}
		
		/*File inputFile = new File(conf.generalResultsFolder.getAbsolutePath() + "\\recipe_wgp2017_tokyo_01_orig.html");
		Document doc = Jsoup.parse(inputFile, "UTF-8");
		//System.out.println(doc.html());*/
		
		Elements numeros = doc.select("td.cardnum");
		
		for(Element numero : numeros){
			
			String cantidad = numero.text().replace("枚", "");			
			numero.text(cantidad);
			
		}
		
		Elements cartas = doc.select("td.cardno");
		
		for(Element carta : cartas){
			
			String id = carta.text();
			if(this.idNamePairs.containsKey(id)){
				String nombreEn = this.idNamePairs.get(id);
				Element row = carta.parent();
				Element nombre = row.select("td.cardname").first().select("a").first();
				nombre.attr("href", "http://www.heartofthecards.com/code/cardlist.html?card=WS_" + id);
				nombre.attr("target", "'_blank'");
				nombre.text(nombreEn);
			}
		}
		
		Elements csss = doc.select("[rel=stylesheet]");
		
		for(Element css : csss){
			
			String href = css.attr("abs:href");
			css.attr("href", href);
		}
		
		Elements elesrcs = doc.select("[src]");
		
		for(Element elesrc : elesrcs){
			
			String src = elesrc.attr("abs:src");
			elesrc.attr("src", src);
		}
		
		String everything = doc.outerHtml();

		Properties prop = new Properties();
		InputStream input = new FileInputStream(conf.wsTcgResultsFolder.getAbsolutePath() + "\\TranlationPairs.properties");
		prop.load(new InputStreamReader(input, Charset.forName("UTF-8")));
		input.close();

		Set<?> keys = prop.keySet();
		String[] keyArray = keys.toArray(new String[keys.size()]);
		
        for(String key : keyArray){
			String value = prop.getProperty(key);
			//System.out.println("Key : " + key + ", Value : " + value);
			everything = everything.replaceAll(key, value);
		}
		
		File output;
		String fileName = url.replaceAll(".+/", "");
		if(mobile){
			output = new File(conf.wsTcgResultsFolder.getAbsolutePath() + "\\m." + fileName + ".html");
		}
		else{
			output = new File(conf.wsTcgResultsFolder.getAbsolutePath() + "\\" + fileName + ".html");			
		}
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
		writer.write(everything);
		writer.close();
	}
	
}
