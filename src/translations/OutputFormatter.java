package translations;

import hotcfiles.HotcCleanFilesHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import cards.Card;
import translations.Translator.LineTranslation;

public class OutputFormatter {

	public static void abilityReport(ArrayList<Card> originals, String outputName) throws Exception{
		
		System.out.println("*** Create Reports for Set : " + outputName + " ***");
		
		Translator translator = new Translator();
		ArrayList<Card> translated = translator.translateSetPrettyOutput(originals);
		
		HashMap<String,String> fullyTranslated = new HashMap<String,String>();
		Set<String> stillPending = new HashSet<String>();
		
		String fullPathWrite = Conf.resultsFolder + "\\" + outputName + "_abilityReport.txt";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		String fullPathWrite2 = Conf.resultsFolder + "\\" + outputName + "_setCompletionReport.txt";
		Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite2), "UTF-8"));
		
		int habCountTotal = 0;
		int habTranslatedCountTotal = 0;
		int translatedCount = 0;
		
		for(int i = 0; i < originals.size(); i++){
			
			int habTranslatedCount = 0;
			
			for(int j = 0; j < originals.get(i).habs.size(); j++){
				
				habCountTotal++;
				
				String old =  originals.get(i).habs.get(j);
				String trans = translated.get(i).habs.get(j);
				
				if(old.equals(trans)){
					stillPending.add(old);
				}
				else{
					fullyTranslated.put(old, trans);
					habTranslatedCount++;
					habTranslatedCountTotal++;
				}
			}
			if(habTranslatedCount == originals.get(i).habs.size()){
				writer2.write("✓\t");
				translatedCount++;
			}
			else{
				writer2.write("X\t");
			}
			writer2.write(habTranslatedCount + "/" + originals.get(i).habs.size() + "\t");
			writer2.write(originals.get(i).id + "\r\n");
		}

		writer2.write("Total: " + translatedCount + "/" + originals.size() + " (" + habTranslatedCountTotal + "/" + habCountTotal + ")\r\n");
		
		List<String> habList = new ArrayList<String>(stillPending); 
		Collections.sort(habList);
		
		for (int i = 0; i < habList.size(); i++) {
			writer.write("\"" + habList.get(i) + "\"\r\n");
			writer.write("->\r\n\r\n");
		}
		
		writer.write("******** Translated ********\r\n\r\n");
		
		List<String> habList2 = new ArrayList<String>(fullyTranslated.keySet()); 
		Collections.sort(habList2);
		
		for (int i = 0; i < habList2.size(); i++) {
			writer.write("\"" + habList2.get(i) + "\"\r\n");
			writer.write("->\"" + fullyTranslated.get(habList2.get(i)) + "\"\r\n\r\n");
		}
		
		writer.close();
		writer2.close();
	}
	
	public static void generateSqlSetUpdateFile(ArrayList<Card> cards, String fileName, String set, String setAgrupado, String language) throws Exception{
		
		System.out.println("*** Getting SQL Insert File for set: " + set + " ***");
		
		String fullPathWrite = Conf.defaultFolder + "\\" + fileName + "_insert.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		String queryHeader = "insert into WST_CARDS (CARD_ID,CARD_NAME,CARD_RARITY,CARD_COLOR,CARD_SIDE,CARD_TYPE,CARD_LEVEL,CARD_COST,CARD_POWER,CARD_SOUL,CARD_TRIGGER,CARD_TRAIT_1,CARD_TRAIT_2,CARD_HABS,SET_FILE,SET_NAME,SET_GRUPO,LANGUAGE) VALUES (";
		
		for(int i = 0; i < cards.size(); i++){
			
			String query = queryHeader;
			
			query = query + "'" + cards.get(i).id + "',";
			query = query + "'" + Utilities.escapeForSql(cards.get(i).name) + "',";
			query = query + "'" + cards.get(i).rarity + "',";
			query = query + "'" + cards.get(i).color + "',";
			query = query + "'" + cards.get(i).side + "',";
			query = query + "'" + cards.get(i).type + "',";
			query = query + "'" + cards.get(i).level + "',";
			query = query + "'" + cards.get(i).cost + "',";
			query = query + "'" + cards.get(i).power + "',";
			query = query + "'" + cards.get(i).soul + "',";
			query = query + "'" + cards.get(i).trigger + "',";
			query = query + "'" + cards.get(i).trait1 + "',";
			query = query + "'" + cards.get(i).trait2 + "','";
			
			for(String hab : cards.get(i).habs){
				query = query + "###" + Utilities.escapeForSql(hab);
			}
			query = query + "','" + fileName + "',";
			query = query + "'" + set + "',";
			query = query + "'" + setAgrupado + "',";
			query = query + "'" + language + "');\r\n";
			
			writer.write(query);
		}
		
		writer.close();
	}
	
	public static void sqlInsert(ArrayList<Card> cards, String fileName, String set, String setAgrupado, String language) throws Exception{
		
		System.out.println("*** Getting SQL Insert File for set: " + set + " ***");
		
		String fullPathWrite = Conf.defaultFolder + "\\" + fileName + "_insert.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		String queryHeader = "insert into WST_CARDS (CARD_ID,CARD_NAME,CARD_RARITY,CARD_COLOR,CARD_SIDE,CARD_TYPE,CARD_LEVEL,CARD_COST,CARD_POWER,CARD_SOUL,CARD_TRIGGER,CARD_TRAIT_1,CARD_TRAIT_2,CARD_HABS,SET_FILE,SET_NAME,SET_GRUPO,LANGUAGE) VALUES (";
		
		for(int i = 0; i < cards.size(); i++){
			
			String query = queryHeader;
			
			query = query + "'" + cards.get(i).id + "',";
			query = query + "'" + Utilities.escapeForSql(cards.get(i).name) + "',";
			query = query + "'" + cards.get(i).rarity + "',";
			query = query + "'" + cards.get(i).color + "',";
			query = query + "'" + cards.get(i).side + "',";
			query = query + "'" + cards.get(i).type + "',";
			query = query + "'" + cards.get(i).level + "',";
			query = query + "'" + cards.get(i).cost + "',";
			query = query + "'" + cards.get(i).power + "',";
			query = query + "'" + cards.get(i).soul + "',";
			query = query + "'" + cards.get(i).trigger + "',";
			query = query + "'" + cards.get(i).trait1 + "',";
			query = query + "'" + cards.get(i).trait2 + "','";
			
			for(String hab : cards.get(i).habs){
				query = query + "###" + Utilities.escapeForSql(hab);
			}
			query = query + "','" + fileName + "',";
			query = query + "'" + set + "',";
			query = query + "'" + setAgrupado + "',";
			query = query + "'" + language + "');\r\n";
			
			writer.write(query);
		}
		
		writer.close();
	}
	
	
	public static void sqlInsertAllInOne(ArrayList<Card> cards, String set) throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "\\all_in_one_insert.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite, true), "UTF-8"));
		
		String queryHeader = "insert into WST_CARDS (CARD_ID,CARD_NAME,CARD_RARITY,CARD_COLOR,CARD_SIDE,CARD_TYPE,CARD_LEVEL,CARD_COST,CARD_POWER,CARD_SOUL,CARD_TRIGGER,CARD_TRAIT_1,CARD_TRAIT_2,CARD_HABS, SET_NAME, LANGUAGE) VALUES (";
		
		for(int i = 0; i < cards.size(); i++){
			
			String query = queryHeader;
			
			query = query + "'" + cards.get(i).id + "',";
			query = query + "'" + Utilities.escapeForSql(cards.get(i).name) + "',";
			query = query + "'" + cards.get(i).rarity + "',";
			query = query + "'" + cards.get(i).color + "',";
			query = query + "'" + cards.get(i).side + "',";
			query = query + "'" + cards.get(i).type + "',";
			query = query + "'" + cards.get(i).level + "',";
			query = query + "'" + cards.get(i).cost + "',";
			query = query + "'" + cards.get(i).power + "',";
			query = query + "'" + cards.get(i).soul + "',";
			query = query + "'" + cards.get(i).trigger + "',";
			query = query + "'" + Utilities.escapeForSql(cards.get(i).trait1) + "',";
			query = query + "'" + Utilities.escapeForSql(cards.get(i).trait2) + "','";
			
			for(String hab : cards.get(i).habs){
				query = query + "###" + Utilities.escapeForSql(hab);
			}
			query = query + "','" + set + "',";
			query = query + "'EN');\r\n";
			
			writer.write(query);
		}
		
		writer.close();
	}

	public static void translationPairsSqlInsert() throws Exception{
		
		String fullPathWrite = Conf.resultsFolder + "\\translationPairsSqlInsert.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		Translator trans = new Translator(); 
		
		for(LineTranslation line : trans.lineTranslations){
			
			writer.write("INSERT INTO WST_TRANSLATION_PAIR(TP_MATCH, TP_TRANSLATION, TP_GROUP, TP_LANGUAGE) VALUES ('");
			writer.write(Utilities.escapeForSql(line.pattern.toString()));
			writer.write("','");
			writer.write(Utilities.escapeForSql(line.replace));
			writer.write("','");
			writer.write(line.type);
			writer.write("','ES');\r\n");
		}
		
		writer.close();
	}
	
/*	public static void abilityMasterMatchsSqlUpdate() throws Exception{
			
		System.out.println("*** Translation Matchs SQL Update ***");
	
		String fullPathWrite = Conf.resultsFolder + "\\abilityMasterMatchsSqlUpdate.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		Translator trans = new Translator(); 
		
		ArrayList<String> abilityMasterList = TextFileParser.abilityMasterList();
		
		for(String ability : abilityMasterList){
			
			boolean matched = false;
			String match = "No Match";
			
			for(LineTranslation attempt : trans.lineTranslations){
				
				Matcher m = attempt.pattern.matcher(ability);
				if(m.find()){
					match = attempt.pattern.toString();
					matched = true;
					break;
				}
			}
			/*if(matched){
				writer.write("* Matched ability: " + ability + "\r\n");
				writer.write("* With translation match: " + match + "\r\n");
			}*
			writer.write("UPDATE WST_ABILITY_MASTER SET AM_MATCH = '");
			writer.write(Utilities.escapeForSql(match));
			writer.write("' WHERE AM_TEXT = '");
			writer.write(Utilities.escapeForSql(ability));
			writer.write("';\r\n");
		
		}
		
		writer.close();
	}*/
	
	public static void abilityMasterNewCardstUpdate(ArrayList<Card> cards) throws Exception{
		System.out.println("*** Insert/Update Ability Master List with New Cards ***");
		
		String fullPathWrite = Conf.resultsFolder + "\\abilityMasterNewCardsSqlUpdate.sql";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		for(Card card : cards){

			for(String ability : card.habs){
			
				writer.write("UPDATE WST_ABILITY_MASTER SET AM_CARDS =  CONCAT(AM_CARDS, '");
				writer.write(card.id + ";");
				writer.write("') WHERE AM_TEXT = '");
				writer.write(Utilities.escapeForSql(ability));
				writer.write("';\r\n");
				writer.write("INSERT INTO WST_ABILITY_MASTER (AM_TEXT, AM_CARDS) SELECT '");
				writer.write(Utilities.escapeForSql(ability));
				writer.write("', '");
				writer.write(card.id + ";");
				writer.write("' FROM WST_ABILITY_MASTER WHERE NOT EXISTS (SELECT * FROM WST_ABILITY_MASTER WHERE AM_TEXT = '");
				writer.write(Utilities.escapeForSql(ability));
				writer.write("') LIMIT 1;\r\n");
			}
		}
		
		writer.close();
	}
	
	public static void generateAbilityFile(ArrayList<Card> cards, String fileName) throws Exception{
		System.out.println("*** Generate Ability List Txt for " + fileName + " ***");
		
		String fullPathWrite = Conf.resultsFolder + fileName + ".txt";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));

		TreeMap<String,ArrayList<String>> allAbilitiesSorted = Utilities.abilityMasterList(cards);
		
		for(String key : allAbilitiesSorted.keySet()){
			writer.write("\"" + key + "\"");
			writer.write("\t");
			for(String ability : allAbilitiesSorted.get(key) ){
				writer.write(ability + ";");
			}
			writer.write("\r\n");
		}
		
		writer.close();
	}

	public static void generateAbilityListFile_WithGroupedIds(ArrayList<Card> cards, File file) throws Exception{
		System.out.println("*** Generate Ability List (with grouped Ids) Txt for " + file.getName() + " ***");
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

		for(Card card : cards){
			for(String ability : card.habs){
				writer.write("\"" + ability + "\"");
				writer.write("\t");
				writer.write(card.id + ";");
				writer.write("\r\n");
			}
		}
		writer.close();
		HotcCleanFilesHelper.orderLinesInFile(file);
	}
	
	public static void generateAbilityListFile_WithIds(ArrayList<Card> cards, File file) throws Exception{
		System.out.println("*** Generate Ability List (with Ids) Txt for " + file.getName() + " ***");
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

		for(Card card : cards){
			for(String ability : card.habs){
				writer.write("\"" + ability + "\"");
				writer.write("\t");
				writer.write(card.id + ";");
				writer.write("\r\n");
			}
		}
		writer.close();
		HotcCleanFilesHelper.orderLinesInFile(file);
	}
	
	public static void generateAbilityListFile_NoIds(ArrayList<Card> cards, File file) throws Exception{
		System.out.println("*** Generate Ability List (No Ids) Txt for " + file.getName() + " ***");
		
		ArrayList<String> abilities = new ArrayList<String>(); 
		
		for(Card card : cards){
			for(String ability : card.habs){
				if(!abilities.contains(ability)){
					abilities.add(ability);
				}
			}
		}
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

		for(String ability : abilities){
			writer.write("\"" + ability + "\"");
			writer.write("\r\n");
		}
		
		writer.close();
		HotcCleanFilesHelper.orderLinesInFile(file);
	}
	
	
	public static void generateRawRemainingAbilityListFile(ArrayList<Card> cards, File file) throws Exception{
		System.out.println("*** Generate Ability List Txt for " + file.getName() + " ***");
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		
		Translator translator = new Translator();
		
		for(Card card : cards){
			for(String ability : card.habs){
				String translated = translator.translateAbility(ability);
				if(translated.equals(ability)){
					writer.write("\"" + ability + "\"");
					writer.write("\t");
					writer.write(card.id + ";");
					writer.write("\r\n");
				}
			}
		}
		writer.close();
		HotcCleanFilesHelper.orderLinesInFile(file);
	}
	
	public static void generateRawTranslatedAbilityListFile(ArrayList<Card> cards, File file) throws Exception{
		System.out.println("*** Generate Ability List Txt for " + file.getName() + " ***");
		
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		
		Translator translator = new Translator();
		
		for(Card card : cards){
			for(String ability : card.habs){
				String translated = translator.translateAbility(ability);
				if(!translated.equals(ability)){
					writer.write("\"" + ability + "\"");
					writer.write("\t");
					writer.write(card.id + ";");
					writer.write("\r\n");
				}
			}
		}
		writer.close();
		HotcCleanFilesHelper.orderLinesInFile(file);
	}
	
	public static void generateLinesFile(ArrayList<String> lines, String fileName) throws Exception{
		System.out.println("*** Generate Lines Based Txt Files for " + fileName + " ***");
		
		String fullPathWrite = Conf.resultsFolder + fileName + ".txt";
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));
		
		for(String line : lines){
			writer.write(line);
			writer.write("\r\n");
		}
		
		writer.close();
	}
	
	public static void generateAbilityMasterLists(ArrayList<Card> cards) throws Exception{
		System.out.println("*** Ability Master Lists***");
		
		Set<String> actAbilitiesSet = new HashSet<String>();
		Set<String> autoAbilitiesSet = new HashSet<String>();
		Set<String> autoCostedAbilitiesSet = new HashSet<String>();
		Set<String> contAbilitiesSet = new HashSet<String>();
		Set<String> otherAbilitiesSet = new HashSet<String>();
		
		for(Card card : cards){
			for(String ability : card.habs){
				
				String abilityPatron = ability.replaceAll("::.+?::", "<<>>").replaceAll("'.*?\".*?'", "''").replaceAll("\".+?\"", "''");
				abilityPatron = "\"" + abilityPatron + "\"";
				
				
				if(ability.startsWith("[A] [")){
					autoCostedAbilitiesSet.add(abilityPatron);
				}
				else if(ability.startsWith("[A]")){
					autoAbilitiesSet.add(abilityPatron);
				}
				else if(ability.startsWith("[C]")){
					contAbilitiesSet.add(abilityPatron);
				}
				else if(ability.startsWith("[S]")){
					actAbilitiesSet.add(abilityPatron);
				}
				else{
					otherAbilitiesSet.add(abilityPatron);
				}
			}
		}
		
		ArrayList<String> actAbilities = new ArrayList<String>(actAbilitiesSet);
		Collections.sort(actAbilities);
		OutputFormatter.generateLinesFile(actAbilities, "ActAbilityList");
		
		ArrayList<String> autoAbilities = new ArrayList<String>(autoAbilitiesSet);
		Collections.sort(autoAbilities);
		OutputFormatter.generateLinesFile(autoAbilities, "AutoAbilityList");
		
		ArrayList<String> autoCostedAbilities = new ArrayList<String>(autoCostedAbilitiesSet);
		Collections.sort(autoCostedAbilities);
		OutputFormatter.generateLinesFile(autoCostedAbilities, "AutoCostedAbilityList");
		
		ArrayList<String> contAbilities = new ArrayList<String>(contAbilitiesSet);
		Collections.sort(contAbilities);
		OutputFormatter.generateLinesFile(contAbilities, "ContAbilityList");
		
		ArrayList<String> otherAbilities = new ArrayList<String>(otherAbilitiesSet);
		Collections.sort(otherAbilities);
		OutputFormatter.generateLinesFile(otherAbilities, "OtherAbilityList");
	}
	
	public static void translationPairsPretty() throws Exception{
		for(String fileName : Conf.translationPairsFiles){
			File fileRead = new File(Conf.translationPairsFolder + fileName + ".txt");
			File fileWrite = new File(Conf.resultsFolder + fileName + "_HumanStyle.txt");
			
			BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(fileRead), "UTF-8"));
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWrite), "UTF-8"));
			
			while(readerBase.ready()){
				
				String patternLine = readerBase.readLine();
				String replacementLine = readerBase.readLine();
				readerBase.readLine(); // Ignore line
				
				String prettyPatterLine = patternLine.replace("(.+?)", "TEXTO").replace("(\\d+)", "NUMERO").replace("(\\d)", "DIGITO").replace("\\", "");
				String prettyReplacementLine = replacementLine.replaceAll("%%\\$(\\d)%%", "TRAIT_1").replaceAll("##\\$(\\d)##", "NOMBRE_PARCIAL_$1").replaceAll("@@\\$(\\d)@@", "NOMBRE_EXACTO_$1").replaceAll("\\$(\\d)", "NUMERO_$1");
				
				writer.write(prettyPatterLine + "\r\n");
				writer.write(prettyReplacementLine + "\r\n");
				writer.write("\r\n");
				
			}
			
			readerBase.close();
			writer.close();
		}
	}
	
	public static void overlapReport(String set1name, ArrayList<Card> set1cards, String set2name, ArrayList<Card> set2cards) throws Exception{
		
		System.out.println("*** Create Overlap Reports for sets : " + set1name + " & " + set2name + " ***");
		
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		Set<String> overlap = new HashSet<String>();
		
		for(Card card : set1cards){
			set1.addAll(card.habs);
		}
		for(Card card : set2cards){
			set2.addAll(card.habs);
		}
		
		for(String hab : set1){
			if(set2.contains(hab)){
				overlap.add(hab);
				set2.remove(hab);
			}
		}
		for(String hab : overlap){
			set1.remove(hab);
		}
		
		Translator translator = new Translator();
		
		
		String fullPathWrite1 = Conf.resultsFolder + "\\" + set1name + "_Remaining.txt";
		Writer writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite1), "UTF-8"));
		List<String> habList1 = new ArrayList<String>(set1); 
		Collections.sort(habList1);
		for (String hab : habList1) {
			
			String translated = translator.translateAbilityPrettyOutput(hab);
			
			if(hab.equals(translated)){
				writer1.write("\r\n");
				writer1.write("\"" + hab + "\"\r\n");
				writer1.write("\r\n");
			}
			else{
				//writer1.write("\"" + translated + "\"\r\n");
			}
		}
		writer1.close();
		
		String fullPathWrite2 = Conf.resultsFolder + "\\" + set2name + "_Remaining.txt";
		Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite2), "UTF-8"));
		List<String> habList2 = new ArrayList<String>(set2); 
		Collections.sort(habList2);
		for (String hab : habList2) {
			
			String translated = translator.translateAbilityPrettyOutput(hab);
			
			if(hab.equals(translated)){
				writer2.write("\"" + hab + "\"\r\n");
				writer2.write("\r\n");
				writer2.write("\r\n");
			}
			else{
				//writer2.write("\"" + translated + "\"\r\n");
			}
		}
		writer2.close();
		
		String fullPathWriteO = Conf.resultsFolder + "\\" + "" + "overlap.txt";
		Writer writerO = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWriteO), "UTF-8"));
		List<String> habListO = new ArrayList<String>(overlap); 
		Collections.sort(habListO);
		for (String hab : habListO) {
			writerO.write("\"" + hab + "\"\r\n");
			
			String translated = translator.translateAbilityPrettyOutput(hab);
			
			if(hab.equals(translated)){
				writerO.write("\r\n");
			}
			else{
				writerO.write("\"" + translated + "\"\r\n");
			}
			writerO.write("\r\n");
		}
		writerO.close();
	}
	
	public static void generatePatternsMasterList() throws Exception{
		
		for(String fileName : Conf.translationPairsFiles){
			File fileRead = new File(Conf.translationPairsFolder + fileName + ".txt");
			File fileWrite = new File(Conf.resultsFolder + fileName + "_Patterns.txt");
			
			BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(fileRead), "UTF-8"));
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWrite), "UTF-8"));
			
			while(readerBase.ready()){
				
				String patternLine = readerBase.readLine();
				String replacementLine = readerBase.readLine();
				readerBase.readLine(); // Ignore line
				
				String prettyPatterLine = patternLine.replace("(.+?)", "TEXTO").replace("(\\d+)", "NUMERO").replace("(\\d)", "DIGITO").replace("\\", "");
				//String prettyReplacementLine = replacementLine.replaceAll("%%\\$(\\d)%%", "TRAIT_1").replaceAll("##\\$(\\d)##", "NOMBRE_PARCIAL_$1").replaceAll("@@\\$(\\d)@@", "NOMBRE_EXACTO_$1").replaceAll("\\$(\\d)", "NUMERO_$1");
				
				writer.write(prettyPatterLine + "\r\n");
				//writer.write(prettyReplacementLine + "\r\n");
				//writer.write("\r\n");
				
			}
			
			readerBase.close();
			writer.close();
		}
	}
	public static void generateTranslationsMasterList() throws Exception{
		
		for(String fileName : Conf.translationPairsFiles){
			File fileRead = new File(Conf.translationPairsFolder + fileName + ".txt");
			File fileWrite = new File(Conf.resultsFolder + fileName + "_Trans.txt");
			
			BufferedReader readerBase = new BufferedReader(new InputStreamReader(new FileInputStream(fileRead), "UTF-8"));
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWrite), "UTF-8"));
			
			while(readerBase.ready()){
				
				String patternLine = readerBase.readLine();
				String replacementLine = readerBase.readLine();
				readerBase.readLine(); // Ignore line
				
				//String prettyPatterLine = patternLine.replace("(.+?)", "TEXTO").replace("(\\d+)", "NUMERO").replace("(\\d)", "DIGITO").replace("\\", "");
				String prettyReplacementLine = replacementLine.replaceAll("%%\\$(\\d)%%", "TRAIT_1").replaceAll("##\\$(\\d)##", "NOMBRE_PARCIAL_$1").replaceAll("@@\\$(\\d)@@", "NOMBRE_EXACTO_$1").replaceAll("\\$(\\d)", "NUMERO_$1");
				
				//writer.write(prettyPatterLine + "\r\n");
				writer.write(prettyReplacementLine + "\r\n");
				//writer.write("\r\n");
				
			}
			
			readerBase.close();
			writer.close();
		}
	}
	
	public static void generateIdNamePairsFile(ArrayList<Card> cards) throws Exception{
		
		System.out.println("*** Generate Id Name Pairs File ***");
		
		File fileWrite = new File(Conf.resultsFolder + "IdNamePairs.txt");
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileWrite), "UTF-8"));
		
		Card firstCard = cards.remove(0);
		writer.write(firstCard.name + "\r\n");
		writer.write(firstCard.id + "");
		
		for(Card card : cards){
			writer.write("\r\n\r\n");
			writer.write(card.name + "\r\n");
			writer.write(card.id + "");
		}
		
		writer.close();
	}
	
	public static void generateUselessPairsFile() throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.getAllCards();
		ArrayList<Card> uniqueCards = Utilities.getNonParallelCards(allCards); 
		ArrayList<String> abilities = Utilities.getAbilitiesFromcards(uniqueCards);
		
		Translator translator = new Translator();
		
		for(LineTranslation translation : translator.lineTranslations){
			boolean inUse = false;
			for(String ability : abilities){
				
				Matcher m = translation.pattern.matcher(ability);
				if(m.find()){
					inUse = true;
					break;
				}
			}
			if(!inUse){
				System.out.println("Not in use: " + translation.pattern.toString());
			}
		}
	}
}
