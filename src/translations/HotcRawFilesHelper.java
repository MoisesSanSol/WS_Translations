package translations;

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



public class HotcRawFilesHelper {
	
	// General configuration
	/*private static int politeness = 10000;
	private static String baseURL = "http://www.heartofthecards.com/";
	private static String tradURL = "code/cardlist.html?pagetype=ws";
	
	public static void main(String[] args) throws Exception {
		System.out.println("*** Starting ***");

		//GetHotcFiles.getSetPages();
		//GetHotcFiles.getTranlationFile("lol");;
		//GetHotcFiles.downloadTransFile("http://www.heartofthecards.com/translations/accel_world_trial_deck.txt");
		
		System.out.println("*** Finished ***");
	}

	public static ArrayList<String> downloadNewHotcRawFiles() throws Exception{
		
		System.out.println("*** Add New HotC Files ***");
		
		ArrayList<String> newFilesNames = new ArrayList<String>(); 
		
		ArrayList<String> filesNames = Utilities.getFileNamesInFolder(Conf.hotcRawFilesFolder);
		HashMap<String,String> filesUrls = GetHotcFiles.getHotcRawFilesUrls();
		
		for(String key : filesUrls.keySet()){
			if(!filesNames.contains(key)){
				GetHotcFiles.downloadRawFile(filesUrls.get(key));
				newFilesNames.add(key);
				System.out.println("*** New HotC File: " + key + " ***");
			}
		}
		return newFilesNames;
	}
	
	public static void downloadAllHotcRawFiles() throws Exception{
		
		System.out.println("*** Add New HotC Files ***");
		
		HashMap<String,String> fileUrls = GetHotcFiles.getHotcRawFilesUrls();
		
		for(String key : fileUrls.keySet()){
			GetHotcFiles.downloadRawFile(fileUrls.get(key));
		}
	}
	
	private static HashMap<String,String> getHotcRawFilesUrls() throws Exception{
		
		HashMap<String,String> filesUrls = new HashMap<String,String>();
		
		// Connect to main card page
		UserAgent mainUserAgent = new UserAgent();
		mainUserAgent.visit(baseURL + tradURL);
		System.out.println("Scrapping page: " + baseURL);
		//System.out.println(mainUserAgent.doc.outerHTML());
		
		// Finding links
		Elements anchors = mainUserAgent.doc.findEvery("<a href=.+?cardset.+");
		
		// Looping sets
		for (Element anchor : anchors ) {
			
			String setUrl = anchor.getAtString("href");
			System.out.println("Found set page anchor url: " + setUrl);
			String fileUrl = GetHotcFiles.getRawFileUrl(setUrl);
			String fileName = fileUrl.replaceAll(".+/", "");
			filesUrls.put(fileName, fileUrl);
		}
		
		return filesUrls;
	}
	
	public static String getRawFileUrl(String url)throws Exception{
		
		UserAgent mainUserAgent = new UserAgent();
		mainUserAgent.visit(url);
		System.out.println("Scrapping page: " + url);
		//System.out.println(mainUserAgent.doc.outerHTML());
		
		Element anchor = mainUserAgent.doc.findFirst("<a href=.+?translation.+");
		String fileUrl = anchor.getAtString("href");
		System.out.println("Found text file url: " + fileUrl);
		
		return fileUrl;
	}
	
	public static void downloadRawFile(String url) throws Exception{
		
		try {
		    Thread.sleep(politeness);
		} catch ( java.lang.InterruptedException ie) {
		    System.out.println(ie);
		}
		
		String fileName = url.replaceAll(".+/", "");
		
		HandlerForBinary handlerForBinary = new HandlerForBinary();
		UserAgent userAgent = new UserAgent();
		
		String handlerType = "text/plain";
		
		userAgent.setHandler(handlerType, handlerForBinary);
		userAgent.visit(url);
		
		System.out.println("Downloading text file " + fileName + " from: " + url);
		
		FileOutputStream output = null;
		output = new FileOutputStream(Conf.hotcRawFilesFolder + fileName);
		output.write(handlerForBinary.getContent());
		output.close();
	}*/
	
	
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
	
	public static void test() throws Exception{
		
		System.out.println("** HotcRawFilesHelper Test");
		
		/*Properties reference = new Properties();
		OutputStream output = new FileOutputStream(Conf.hotcRawFilesReferenceFile);
		
		ArrayList<String> setRefs = HotcRawFilesHelper.getAvailableSetRefs();

		for (String setRef : setRefs){
			
			Thread.sleep(5000);
			
			String rawFileName = HotcRawFilesHelper.getRawFileName(setRef);

			reference.setProperty(setRef, rawFileName);
		}

		reference.store(output, "Full direct dump.");
		output.close();*/
		
		Properties reference = new Properties();
		InputStream input = new FileInputStream(Conf.hotcRawFilesReferenceFile);

		reference.load(input);
		input.close();

		Set<?> keys = reference.keySet();
		String[] keyArray = keys.toArray(new String[keys.size()]);
		
        for(String key : keyArray){
        	
			Thread.sleep(5000);
        	
			String rawFileName = reference.getProperty(key);
			
			File rawFile = new File(Conf.hotcRawFilesFolder + rawFileName + ".txt");
			String rawFileUrl = Conf.hotcTranslationFileBaseUrl + rawFileName + ".txt";
			
			DownloadHelper.downloadFile(rawFileUrl, rawFile);
			
		}
	}

	public static void downloadNewHotcRawFiles() throws Exception{
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
        		//System.out.println("* Already downloaded: " + setRef + ", " + reference.getProperty(setRef));
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
