package translations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;



public class GetHotcFiles {
	
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
}
