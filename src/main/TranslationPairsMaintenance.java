package main;

import configuration.LocalConf;

public class TranslationPairsMaintenance {
	
private LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		TranslationPairsMaintenance dispatcher = new TranslationPairsMaintenance();

		
		
		System.out.println("*** Finished ***");
	}
	
	public TranslationPairsMaintenance() throws Exception{
		this.conf = LocalConf.getInstance();
		
	}
	
	
}
