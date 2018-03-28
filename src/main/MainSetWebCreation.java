package main;

import java.io.File;
import java.util.ArrayList;

import output.Summaries;
import parser.HotcCleanFileParser;
import translator.TranslatorUtilities;
import cards.Card;
import configuration.LocalConf;

public class MainSetWebCreation {

	// For creating the web for a set.
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		String setFileName = "" + ".txt";
		String setLaPageId = "";
		String setYytPageId = "";

		LocalConf conf = LocalConf.getInstance();
		TranslatorUtilities transUtilities = new TranslatorUtilities();
		
		// Ensure that the clean hotc file is available
		File setHotcFile = new File(conf.gethotcCleanFilesFolderPath() + setFileName);
		
		// Ensure that translations pairs are available in the full pairs list

		
		// 
		
		System.out.println("*** Finished ***");
	}
}
