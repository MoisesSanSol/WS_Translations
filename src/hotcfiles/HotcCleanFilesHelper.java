package hotcfiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.Utilities;
import configuration.LocalConf;

public class HotcCleanFilesHelper {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		HotcCleanFilesHelper hotcCleanFilesHelper = new HotcCleanFilesHelper();
		
		System.out.println("*** Finished ***");
	}
	
	public HotcCleanFilesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void generateCleanPromoHotcFiles() throws Exception{
		System.out.println("** Generate Clean Promo Hotc Files");
		
		// Hardcoded promo file names (seem to be static)
		String[] promoRawFileNames = {"weib_promos","schwarz_promos"};
		
        for(String promoRawFileName : promoRawFileNames){
        		
    		this.generateCleanHotcFile(promoRawFileName);
		}
	}
	
	public void generateCleanMissingHotcFiles() throws Exception{
		System.out.println("** Generate Clean Missing Hotc Files");
		
		File[] rawFiles = conf.hotcRawFilesFolder.listFiles();
		File[] cleanFiles = conf.hotcCleanFilesFolder.listFiles();

		for(File rawFile : rawFiles){
			boolean missing = true;
        	for(File cleanFile : cleanFiles){
        		if(rawFile.getName().equals(cleanFile.getName())){
        			missing = false;
        		}
        	}
    		if(missing) {
    			this.generateCleanHotcFile(rawFile.getName().replace(".txt", ""));
    		}
		}
	}
	
	public void generateCleanHotcFile(String rawFileName) throws Exception{
		
		String fullPathRawFile = this.conf.gethotcRawFilesFolderPath() + rawFileName + ".txt";
		File rawFile = new File(fullPathRawFile);
		
		String fullPathResultFile = this.conf.gethotcCleanFilesFolderPath() + rawFileName + ".txt";
		File resultFile = new File(fullPathResultFile);
		
		this.generateCleanHotcFile(rawFile, resultFile);
	}
	
	
	public void generateCleanHotcFile(File rawFile, File resultFile) throws Exception{
		System.out.println("** Unwraping Original File : " + rawFile.getName());
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rawFile), "UTF-8"));
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8"));
		
		while(reader.ready()){
			
			String line = reader.readLine();
			
			if(line.startsWith("TEXT: ")){
				writer.write("TEXT: \r\n");
				String newLine = line.replace("TEXT: ", "");
				boolean loopBreak = false;
				while (!loopBreak){
					line = reader.readLine();
					if(line.equals("")){
						loopBreak = true;
						writer.write(newLine + "\r\n\r\n");
					}
					else if(line.startsWith("[S]") || line.startsWith("[A]") || line.startsWith("[C]")){
						writer.write(newLine + "\r\n");
						newLine = line;
					}
					else{
						newLine = newLine + " " + line;
					}
				}
				
			}
			else{
				writer.write(line + "\r\n");
			}
	
		}
		
		reader.close();
		writer.close();
		
		this.removeExtraLineBreaks(resultFile);
	}
	
	public void generateCleanUpdatedHotcFiles() throws Exception{
		System.out.println("** Generate Clean Updated Hotc Files");
		
		String hotcRawUpdatedFolderPath = conf.gethotcRawFilesFolderPath() + "JustDownloaded//";
		File hotcRawUpdatedFolder = new File(hotcRawUpdatedFolderPath);
		
		String hotcCleanUpdatedFolderPath = conf.gethotcCleanFilesFolderPath() + "JustDownloaded//";
		File hotcCleanUpdatedFolder = new File(hotcCleanUpdatedFolderPath);
		Utilities.checkFolderExistence(hotcCleanUpdatedFolder);
		
		File[] rawFiles = hotcRawUpdatedFolder.listFiles();

		for(File rawFile : rawFiles){
			
			File hotcCleanUpdatedFile = new File(hotcCleanUpdatedFolderPath + rawFile.getName());
			
   			this.generateCleanHotcFile(rawFile, hotcCleanUpdatedFile);
		}
	}
	
	public void removeExtraLineBreaks(File cleanFile) throws Exception{
		
		ArrayList<String> content = new ArrayList<String>(Files.readAllLines(cleanFile.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> newContent = new ArrayList<String>();
		
		while(!content.isEmpty()){
			String line = content.remove(0);
			if(line.isEmpty()){
				newContent.add(line);
				boolean checking = true;
				while(!content.isEmpty() && checking){
					String nextLine = content.remove(0);
					if(!nextLine.isEmpty()){
						newContent.add(nextLine);
						checking = false;
					}
				}
			}
			else{
				newContent.add(line);
			}
		}
		
		Files.write(cleanFile.toPath(), newContent, StandardCharsets.UTF_8);
	}
}
