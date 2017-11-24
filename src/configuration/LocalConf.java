package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import utilities.Utilities;

// Singleton Configuration class.
public class LocalConf {

	// URLs
	
	// Configuration file
	public String folderConfFile = "LocalConfiguration.properties";
	
	// Folders
	public File generalResultsFolder;
	
	// Singleton instance
	private static LocalConf instance;

	// Private (so only getInstance() can be used to instantiate the class) constructor
	private LocalConf() throws Exception{
		this.loadLocalConfiguration();
	}
	
	public static LocalConf getInstance() throws Exception{
      if(instance == null) {
          instance = new LocalConf();
       }
       return instance;
	}
	
	public void loadLocalConfiguration() throws Exception{

		InputStream folderConfInput = null;
		
		try {

			// Folders
			Properties folderConf = new Properties();
			
			String folderConfFilePath = "Files/Configuration/" + this.folderConfFile;
			folderConfInput = new FileInputStream(folderConfFilePath);

			folderConf.load(folderConfInput);
			
			String generalResultsFolderPath = folderConf.getProperty("generalResultsFolder");
			this.generalResultsFolder = new File(generalResultsFolderPath);
			Utilities.checkFolderExistence(this.generalResultsFolder);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (folderConfInput != null) {
				try {
					folderConfInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
