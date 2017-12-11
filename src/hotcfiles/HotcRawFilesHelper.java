package hotcfiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import configuration.LocalConf;
import translations.Conf;
import download.DownloadHelper;

public class HotcRawFilesHelper {
	
	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		
		hotcRawFilesHelper.downloadPromoHotcRawFiles();
		
		System.out.println("*** Finished ***");
	}
	
	public HotcRawFilesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public static ArrayList<String> getAvailableSetRefs() throws Exception{
		
		System.out.println("** Get Available Set Refs");
		
		ArrayList<String> availableSetRefs = new ArrayList<String>(); 
		
		Document mainPage = Jsoup.connect(Conf.hotcTranslationMainUrl).maxBodySize(0).get();	
		Elements anchors = mainPage.select("a[href*=cardset]");
		
		for (Element anchor : anchors){
			String setRef = anchor.attr("href").replace("/code/cardlist.html?pagetype=ws&cardset=", "");
			availableSetRefs.add(setRef);
			
			System.out.println("* Set Ref: " + setRef);
		}
		
		return availableSetRefs;
	}
	
	public static String getHotcRawFileName(String setTranslationRefUrl) throws Exception{
		
		System.out.println("** Get Raw File Name");
		
		String rawFileName = ""; 
		
		String setTranslationUrl = Conf.hotcTranslationSetBaseUrl + setTranslationRefUrl;
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
    		
    		File rawFile = new File(Conf.hotcRawFilesFolder + promoRawFileName + ".txt");
			String rawFileUrl = Conf.hotcTranslationFileBaseUrl + promoRawFileName + ".txt";
			
			DownloadHelper.downloadFile(rawFileUrl, rawFile);
			
    		Thread.sleep(5000);
		}
	}
	
	public void downloadNewHotcRawFiles() throws Exception{
		System.out.println("** Download New Hotc Raw Files");
		
		ArrayList<String> setRefs = HotcRawFilesHelper.getAvailableSetRefs();

		Properties reference = new Properties();
		InputStream input = new FileInputStream(Conf.hotcRawFilesReferenceFile);

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
        		
        		Thread.sleep(5000);
        		
        		System.out.println("* New file to download: " + setRef);
        		String setUrl = Conf.hotcTranslationSetBaseUrl + setRef;
        		String rawFileName = HotcRawFilesHelper.getHotcRawFileName(setUrl);
        		
        		File rawFile = new File(Conf.hotcRawFilesFolder + rawFileName + ".txt");
    			String rawFileUrl = Conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";
    			
    			DownloadHelper.downloadFile(rawFileUrl, rawFile);
    			reference.setProperty(setRef, rawFileName);
        	}
		}
		
        OutputStream output = new FileOutputStream(Conf.hotcRawFilesReferenceFile);
		reference.store(output, "Updating with new series.");
		output.close();
	}
}
