package parser;

import configuration.LocalConf;

public class GeneralFileParser {

	private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		GeneralFileParser generalFileParser = new GeneralFileParser();
		
		System.out.println("*** Finished ***");
	}
	
	public GeneralFileParser() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	
	//public HashMap<String,String> get
	
}
