package hotcfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import configuration.LocalConf;
//import translations.Conf;
import download.DownloadHelper;

public class HotcRawFilesHelper {
	
	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();

		hotcRawFilesHelper.downloadNewHotcRawFiles();
		hotcRawFilesHelper.downloadPromoHotcRawFiles();
		
		System.out.println("*** Finished ***");
	}
	
	public HotcRawFilesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public ArrayList<String> getAvailableSetRefs() throws Exception{
		
		System.out.println("** Get Available Set Refs");
		
		ArrayList<String> availableSetRefs = new ArrayList<String>(); 
		
		Document mainPage = Jsoup.connect(conf.hotcTranslationMainUrl).maxBodySize(0).get();	
		Elements anchors = mainPage.select("a[href*=cardset]");
		
		for (Element anchor : anchors){
			String setRef = anchor.attr("href").replace("/code/cardlist.html?pagetype=ws&cardset=", "");
			availableSetRefs.add(setRef);
			
			System.out.println("* Set Ref: " + setRef);
		}
		
		return availableSetRefs;
	}
	
	public String getHotcRawFileName(String setTranslationRefUrl) throws Exception{
		
		System.out.println("** Get Raw File Name");
		
		String rawFileName = ""; 
		
		String setTranslationUrl = conf.hotcTranslationSetBaseUrl + setTranslationRefUrl;
		System.out.println("* Set Translation Url: " + setTranslationUrl);
		
		Document mainPage = Jsoup.connect(setTranslationUrl).maxBodySize(0).get();	
		Element anchor = mainPage.select("a[href*=translation]").first();
		String rawFilePageUrl = anchor.attr("href");
		
		rawFileName = rawFilePageUrl.replaceFirst("/translations/(.+?)\\.html", "$1");
		
		System.out.println("* File Name: " + rawFileName);
		
		return rawFileName;
	}
	
	public void downloadPromoHotcRawFiles() throws Exception{
		System.out.println("** Download Promo Hotc Raw Files");
		
		// Hardcoded promo file names (seem to be static)
		String[] promoRawFileNames = {"weib_promos","schwarz_promos"};
		
        for(String promoRawFileName : promoRawFileNames){
        		
    		System.out.println("* Promo file to download: " + promoRawFileName);
    		
    		File rawFile = new File(conf.gethotcRawFilesFolderPath() + promoRawFileName + ".txt");
			String rawFileUrl = conf.hotcTranslationFileBaseUrl + promoRawFileName + ".txt";
			
			DownloadHelper.downloadFile(rawFileUrl, rawFile);
			
    		Thread.sleep(5000);
		}
	}
	
	public void downloadNewHotcRawFiles() throws Exception{
		System.out.println("** Download New Hotc Raw Files");
		
		ArrayList<String> setRefs = this.getAvailableSetRefs();

		Properties reference = new Properties();
		InputStream input = new FileInputStream(conf.hotcRawFilesReferenceFile);

		reference.load(input);
		input.close();

		Set<?> keySet = reference.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		ArrayList<String> keyList = new ArrayList<String>(Arrays.asList(keyArray));

		
        for(String setRef : setRefs){
        	
        	if(keyList.contains(setRef)){
        		System.out.println("* Already downloaded: " + setRef + ", " + reference.getProperty(setRef));
        	}
        	else{
        		
        		Thread.sleep(conf.politeness);
        		
        		System.out.println("* New file to download: " + setRef);
        		String setUrl = conf.hotcTranslationSetBaseUrl + setRef;
        		String rawFileName = this.getHotcRawFileName(setUrl);
        		
        		File rawFile = new File(conf.gethotcRawFilesFolderPath() + rawFileName + ".txt");
    			String rawFileUrl = conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";
    			
    			DownloadHelper.downloadFile(rawFileUrl, rawFile);
    			reference.setProperty(setRef, rawFileName);
        	}
		}
		
        OutputStream output = new FileOutputStream(conf.hotcRawFilesReferenceFile);
		reference.store(output, "Updating with new series.");
		output.close();
	}
}
