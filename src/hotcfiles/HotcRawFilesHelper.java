package hotcfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utilities.Utilities;
import configuration.LocalConf;
//import translations.Conf;
import download.DownloadHelper;

public class HotcRawFilesHelper {
	
	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcRawFilesHelper hotcRawFilesHelper = new HotcRawFilesHelper();
		
		System.out.println("*** Finished ***");
	}
	
	public HotcRawFilesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void checkIfHotcRawFileIsUpToDate(String rawFileName) throws Exception{
		
		System.out.println("** Check If Hotc Raw File Is Up To Date: " + rawFileName);
		String rawFileUrl = conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";

		File rawFile = new File(conf.gethotcRawFilesFolderPath() + rawFileName + ".txt");
		File tempFile = new File(conf.gethotcRawFilesFolderPath() + rawFileName + "_freshlyDownloaded.txt");
		
		DownloadHelper.downloadFile(rawFileUrl, tempFile);

		if(FileUtils.contentEquals(rawFile, tempFile)) {
			System.out.println("* Raw File Up To Date");
			tempFile.delete();
		}
		else {
			System.out.println("* Raw File Outdated");
		}
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
		HashMap<String,String> references = this.getDowloadedReferences();
		
        for(String setRef : setRefs){
        	
        	if(references.containsKey(setRef)){
        		System.out.println("* Already downloaded: " + setRef + ", " + references.get(setRef));
        	}
        	else{
        		
        		Thread.sleep(conf.politeness);
        		
        		System.out.println("* New file to download: " + setRef);
        		String setUrl = conf.hotcTranslationSetBaseUrl + setRef;
        		String rawFileName = this.getHotcRawFileName(setUrl);
        		
        		File rawFile = new File(conf.gethotcRawFilesFolderPath() + rawFileName + ".txt");
    			String rawFileUrl = conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";
    			
    			DownloadHelper.downloadFile(rawFileUrl, rawFile);
    			references.put(setRef, rawFileName);
        	}
		}
        this.storeDowloadedReferences(references);
	}
	
	public HashMap<String,String> getDowloadedReferences() throws Exception{
		Properties properties = new Properties();
		InputStream input = new FileInputStream(conf.hotcRawFilesReferenceFile);
		
		properties.load(input);
		input.close();

		HashMap<String, String> references = new HashMap<String,String>((Map)properties);
		
		return references;
	}
	
	public void storeDowloadedReferences(HashMap<String,String> references) throws Exception{
		Properties properties = new Properties();
		properties.putAll(references);

        OutputStream output = new FileOutputStream(conf.hotcRawFilesReferenceFile);
        properties.store(output, "Updating with new series.");
		output.close();
	}
	
	public void downloadAgainHotcRawFiles() throws Exception{
		
		System.out.println("** Download All Hotc Raw Files Again");
		
		HashMap<String,String> setRefs = this.getDowloadedReferences();

		String temporalFolderPath = conf.gethotcRawFilesFolderPath() + "JustDownloaded//";
		
		Utilities.checkFolderExistence(temporalFolderPath);
		
        for(String setRef : setRefs.keySet()){

    		System.out.println("* Ref to download: " + setRef);
        	
    		String rawFileName = setRefs.get(setRef);
        	
        	System.out.println("* File to download: " + rawFileName);

        	File rawFile = new File(temporalFolderPath + rawFileName + ".txt");
        	
        	if(!rawFile.exists()){
        	
	    		Thread.sleep(conf.politeness);
	    		
				String rawFileUrl = conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";
			
				DownloadHelper.downloadFile(rawFileUrl, rawFile);
        	}
        	else{
        		System.out.println("* File already downloaded: " + rawFileName);
        	}
		}
	}
	
	public void compareHotcRawFiles() throws Exception{
		
		System.out.println("** Compare All Hotc Raw Files for Updates");
		
		String temporalFolderPath = conf.gethotcRawFilesFolderPath() + "JustDownloaded//";
		File temporalFolder = new File(temporalFolderPath);
		
        for(File newHotcRawFile : temporalFolder.listFiles()){

        	System.out.println("* Comparing file: " + newHotcRawFile.getName());

        	File oldHotcRawFile = new File(conf.gethotcRawFilesFolderPath() + newHotcRawFile.getName());
        	
        	if(!FileUtils.contentEquals(oldHotcRawFile, newHotcRawFile)){
        		System.out.println("* Outadeted file: " + newHotcRawFile.getName());
        	}
        	else{
        		System.out.println("* File already up to date, deleting file: " + newHotcRawFile.getName());
        		newHotcRawFile.delete();
        	}
		}
	}
}
