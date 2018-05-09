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
import utilities.CardListUtilities;
import utilities.Utilities;

public class WsTcgResults {

	private LocalConf conf;

	private HashMap<String,String> currentResultsPages;
	
	private HashMap<String,String> idNamePairs;
	private HashMap<String,String> textTranslations;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");

		WsTcgResults wsTcgResults = new WsTcgResults();
		//wsTcgResults.generateWstcgResulstPages();
		wsTcgResults.updateWstcgResulstPages();
		
		System.out.println("*** Finished ***");
	}

	public WsTcgResults() throws Exception{
		
		this.conf = LocalConf.getInstance();
		this.idNamePairs = CardListUtilities.getIdNamePairs();
		this.currentResultsPages = WsTcgResultsHelper.getCurrentResultsPages();
		this.textTranslations = WsTcgResultsHelper.getTextTranslationPairs();
	}
	
	public void generateWstcgResulstPages() throws Exception{
	
		for(String page : this.currentResultsPages.keySet()){
			
			String pageFilePath = this.conf.getStaticWebFolderPath() + "\\WsTcgResults\\" + page + ".html";
			File pageFile = new File(pageFilePath);
			
			if(!pageFile.exists()){
				this.generateWstcgResulstPage(page);
			}
		}
	}
	
	public void updateWstcgResulstPages() throws Exception{
		
		for(String page : this.currentResultsPages.keySet()){
			
			String pageFilePath = this.conf.getStaticWebFolderPath() + "\\WsTcgResults\\" + page + ".html";
			File pageFile = new File(pageFilePath);
			Document doc = Jsoup.parse(pageFile, "UTF-8");
			this.generateWstcgResulstPage(doc);
		}
	}
	
	public void generateWstcgResulstPage(String page) throws Exception{
		String pageUrl = this.conf.wstcgResultsBaseUrl + page;
		
		Document doc;
		if(false){
			doc = Jsoup.connect(pageUrl).userAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Mobile Safari/537.36").maxBodySize(0).get();
		}
		else{
			doc = Jsoup.connect(pageUrl).maxBodySize(0).get();	
		}
		this.generateWstcgResulstPage(doc);
	}
	
	public void generateWstcgResulstPage(Document doc) throws Exception{
			
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

        for(String pattern : this.textTranslations.keySet()){
			String translation = this.textTranslations.get(pattern);
			//System.out.println("Key : " + key + ", Value : " + value);
			everything = everything.replaceAll(pattern, translation);
		}
		
        String pageFilePath = "";
        String fileName = doc.location().replaceAll(".+/", "");
        if(!fileName.contains(".html")){
        	pageFilePath = this.conf.getStaticWebFolderPath() + "\\WsTcgResults\\" + fileName + ".html";
        }else{
        	pageFilePath =  doc.location();
        }
		File pageFile = new File(pageFilePath);
		/*if(false){
			output = new File(conf.wsTcgResultsFolder.getAbsolutePath() + "\\m." + fileName + ".html");
		}
		else{
			output = new File(conf.wsTcgResultsFolder.getAbsolutePath() + "\\" + fileName + ".html");			
		}*/
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pageFile), "UTF-8"));
		writer.write(everything);
		writer.close();
	}
}
