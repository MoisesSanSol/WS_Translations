package staticweb;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import output.Summaries;
import parser.HotcCleanFileParser;
import cards.Card;
import translator.Translator;
import utilities.CardListUtilities;
import utilities.Utilities;
import configuration.LocalConf;

public class StaticWebHelper {
	
	LocalConf conf;
	
	HashMap<String,String> pairs;
	HashMap<String,String> rarityPairs;
	HashMap<String,ArrayList<String>> referencias;
	HashMap<String,ArrayList<String>> parallels;
	HashMap<String,ArrayList<String>> homonimas;
	
	public String setId;
	public String setName;
	
	public boolean isExtraBooster;
	public boolean isFoilRun;
	public boolean isLegacyEb;
	public boolean isLegacyTd;
	
	public int regularCardCount;
	public int extendedCardCount;
	public int promoCardCount;
	public int tdCardCount;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		staticWebHelper.createWebIndex_Main();
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebHelper() throws Exception{
		
		this.conf = LocalConf.getInstance();
		this.referencias = new HashMap<String,ArrayList<String>>();
		this.parallels = new HashMap<String,ArrayList<String>>();
		this.homonimas = new HashMap<String,ArrayList<String>>();
		
		this.setId = "";
		this.setName = "";
		
		this.isExtraBooster = false;
		this.isFoilRun = false;
		this.isLegacyTd = false;
		
		this.regularCardCount = 0;
		this.extendedCardCount = 0;
		this.promoCardCount = 0;
		this.tdCardCount = 0;
	}
	
	public void generateCardPages_FullSet(String setFileName) throws Exception{
		
		System.out.println("** Generate Card Pages (Full Set): " + setFileName);
		
		File setFile = new File(this.conf.gethotcCleanFilesFolderPath() + setFileName + ".txt");
		ArrayList<Card> allCards = HotcCleanFileParser.parseCards(setFile);
		this.generateCardPages_ArbitraryCards(allCards);
	}
	
	public void generateCardPages_ArbitraryCards(ArrayList<Card> allCards) throws Exception{
		
		System.out.println("** Generate Card Pages");
		
		Translator translator = new Translator();
		
		ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(allCards);
		this.pairs = CardListUtilities.getNameIdPairs(baseCards);
		this.rarityPairs = CardListUtilities.getIdRarityPairs(baseCards);
		this.homonimas = CardListUtilities.getHomonymIdPairs(baseCards);
		
		ArrayList<Card> translatedCards = translator.translateSet(allCards);
		ArrayList<Card> cleanCards = this.prepareCardsForWeb(translatedCards);
		
		ArrayList<String> templateContent = new ArrayList<>(Files.readAllLines(this.conf.staticWebPageTemplateFile.toPath(), StandardCharsets.UTF_8));
		
		for(Card card : cleanCards){
			this.generateCardPage(card, templateContent);
		}
		if(this.isExtraBooster){
			this.isFoilRun = true;
			for(Card card : cleanCards){
				this.generateCardPage(card, templateContent);
			}
		}
		
	}
	
	public void rotateClimax(ArrayList<Card> cards, String folderPath) throws Exception{
		
		ImagesHelper imagesHelper = new ImagesHelper();
		
		for(Card card : cards){
			if(card.type.equals("Climax")){
				System.out.println("* Found Climax: " + card.fileId);				
				File imageFile = new File(folderPath + card.fileId + ".png");
				imagesHelper.rotateWebFormatImage(imageFile);
				if(this.isExtraBooster){
					File imageFoilFile;
					if(this.isLegacyEb){
						imageFoilFile = new File(folderPath + card.fileId + "S-S.png");
					}
					else{
						imageFoilFile = new File(folderPath + card.fileId + "-S.png");
					}
					imagesHelper.rotateWebFormatImage(imageFoilFile);
				}
			}
		}
		
	}
	
	public String getCardIdLine(Card card) throws Exception{
		String line = card.id + " " + card.rarity;
		if(this.parallels.containsKey(card.id)){
			String suffix = parallels.get(card.id).get(0).replaceAll("^.+?(\\D+)$", "$1");
			String rarity = suffix;
			if(rarity.equals("R")){rarity = "RRR";}
			if(rarity.equals("S")){rarity = "SR";}
			line = line + " (<a href='./" + card.fileId + suffix + ".html'>" + rarity + "</a>)";
		}
		else if(card.id.matches(".+\\D$")){
			if(card.id.matches("^(.+?)[a-z]")){
				line = line + " (";
				String baseId = card.id.replaceAll("^(.+?)\\D+$", "$1");
				ArrayList<String> variations = this.parallels.get(baseId);
				for(String variation : variations){
					if(!variation.equals(card.id)){
						String variationFileId = variation.replace("/", "_");
						String variationId = variation.replace(baseId, "");
						line = line + "<a href='./" + variationFileId + ".html'>" + variationId + "</a>,";
					}
				}
				line = line.replaceAll(",$", "");
				line = line + ")";
			}
			else{
				String baseId = card.id.replaceAll("^(.+?)\\D+$", "$1");
				String fileId = baseId.replace("/", "_");
				String rarity = this.rarityPairs.get(baseId);
				line = line + " (<a href='./" + fileId + ".html'>" + rarity + "</a>)";
			}
		}
		return line;
	}
	
	public void generateCardPage(Card card, ArrayList<String> baseTemplate) throws Exception{

		System.out.println("* Generate Card Page: " + card.id);
		
		ArrayList<String> template = (ArrayList<String>)baseTemplate.clone();
		
		String filenameFriendlyId = card.id.replace("/", "_");
		String filename = filenameFriendlyId;
		String setFileId = filename.split("-")[0];
		
		template.set(template.indexOf("[Card Id]"), card.id);
		template.set(template.indexOf("[Nombre]"), card.name);
		template.set(template.indexOf("[Nombre Jp]"), card.jpName);
		template.set(template.indexOf("[Card Id Line]"), this.getCardIdLine(card));

		template.set(template.indexOf("[Set Name]"), this.setName);
		
		String imageLine = "";
		if(this.isExtraBooster && !this.isFoilRun){
			imageLine = imageLine + "<img src='../images/" + filenameFriendlyId + ".png'></img>";
			if(this.isLegacyEb){
				imageLine = imageLine + "<br><a href='./" + card.fileId + "S-S.html'>Versión Foil</a>";
			}
			else{
				imageLine = imageLine + "<br><a href='./" + card.fileId + "-S.html'>Versión Foil</a>";
			}
		}
		else if(this.isExtraBooster && this.isFoilRun){
			if(this.isLegacyEb){
				imageLine = imageLine + "<img src='../images/" + filenameFriendlyId + "S-S.png'></img>";
				filename = filename + "S-S";
			}
			else{
				imageLine = imageLine + "<img src='../images/" + filenameFriendlyId + "-S.png'></img>";
				filename = filename + "-S";
			}
			imageLine = imageLine + "<br><a href='./" + card.fileId + ".html'>Versión Normal</a>";
		}
		else{
			imageLine = imageLine + "<img src='../images/" + filenameFriendlyId + ".png'></img>";
		}
		
		template.set(template.indexOf("[Image]"), imageLine);
		
		String caracteristicas = "";
		
		if(card.type.equals("Personaje")){
			caracteristicas = caracteristicas + "Personaje " + card.color;
			caracteristicas = caracteristicas + ", Nivel: " + card.level;
			caracteristicas = caracteristicas + ", Poder: " + card.power;
			caracteristicas = caracteristicas + ", Soul: " + card.soul;
			caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
			if(card.trait1.equals("Sin Trait")){
				caracteristicas = caracteristicas + ", Traits: Sin Traits.";
			}
			else if(card.trait2.equals("Sin Trait")){
				caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
				caracteristicas = caracteristicas + ">>.";
			}
			else{
				caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
				caracteristicas = caracteristicas + ">> y <<" + card.trait2;
				caracteristicas = caracteristicas + ">>.";
			}

		}
		else if(card.type.equals("Climax")){
			caracteristicas = caracteristicas + "Climax " + card.color;
			caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
			caracteristicas = caracteristicas + ".";
		}
		else if(card.type.equals("Evento")){
			caracteristicas = caracteristicas + "Evento " + card.color;
			caracteristicas = caracteristicas + ", Nivel: " + card.level;
			caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
			caracteristicas = caracteristicas + ".";
		}
		template.set(template.indexOf("[Caracteristicas]"), Utilities.escapeHtml(caracteristicas));

		String habilidades = "";
		for(int i = 0; i < card.habs.size(); i++){
			habilidades = habilidades + card.habs.get(i);
			if(i < card.habs.size()-1){
				habilidades = habilidades + "\r\n<br>\r\n";
			}
		}
		template.set(template.indexOf("[Habilidades]"), habilidades);
		
		boolean hasReferencias = false;
		String referenciada = "";
		
		if(this.referencias.containsKey(card.name)){
			hasReferencias = true;
			referenciada = "<tr>\r\n<td>\r\n";
			for(String nombre : this.referencias.get(card.name)){
				String nombreFriendlyId = this.pairs.get(nombre).replace("/", "_");
				referenciada = referenciada + "* Esta carta es referenciada en las habilidades de '<a href='./" + nombreFriendlyId + ".html'>" + nombre + "</a></a>'";
				if(!(this.referencias.get(card.name).indexOf(nombre) == this.referencias.get(card.name).size() - 1)){
					referenciada = referenciada + "\r\n<br>\r\n";
				}
			}
			referenciada = referenciada + "\r\n</td>\r\n</tr>";
		}
		
		if(this.homonimas.containsKey(card.name)){
			hasReferencias = true;
			if(referenciada.equals("")){
				referenciada = referenciada + "\r\n<br>\r\n";
			}
			referenciada = referenciada + "<tr>\r\n<td>\r\n";
			for(String id : this.homonimas.get(card.name)){
				String fileId = id.replace("/", "_");
				referenciada = referenciada + "* Esta carta tiene el mismo nombre que '<a href='./" + fileId + ".html'>" + id + "</a></a>'";
				if(!(this.homonimas.get(card.name).indexOf(id) == this.homonimas.get(card.name).size() - 1)){
					referenciada = referenciada + "\r\n<br>\r\n";
				}
			}
			referenciada = referenciada + "\r\n</td>\r\n</tr>";
		}
		
		if(hasReferencias){
			template.set(template.indexOf("[Referencias]"), referenciada);
		}
		else{
			template.remove(template.indexOf("[Referencias]"));
		}
		
		String cardsPath = this.conf.getGeneralResultsFolderPath() + setFileId + "\\cards\\"; 
		Files.write(new File(cardsPath + filename + ".html").toPath(), template, StandardCharsets.UTF_8);
	
	}
	
	public ArrayList<Card> prepareCardsForWeb(ArrayList<Card> cards) throws Exception{
		
		this.referencias = new HashMap<String,ArrayList<String>>();
		
		for(Card card : cards){
			for(int i = 0; i < card.habs.size(); i++){
				String baseAbility = card.habs.get(i);
				String ability = Utilities.escapeHtml(baseAbility);
				while(ability.contains("@@")){
					String refName = ability.split("@@")[1];
					String baseName = baseAbility.split("@@")[1];
					String refId = this.pairs.get(baseName);
					if(refId == null){
						System.out.println("! RefName missing: " + refName);
						System.out.println("! in ability: " + ability);
						throw (new Exception("prepareCardsForWeb Exception"));
					}
					String urlRefId = refId.replace("/", "_");
					String referencia = "<a href='./" + urlRefId + ".html'>" + refName + "</a>";
					ability = ability.replace("@@" + refName + "@@", referencia);
					if(this.referencias.containsKey(baseName)){
						ArrayList<String> origenes = referencias.get(baseName);
						if(!origenes.contains(card.name)){
							origenes.add(card.name);
							this.referencias.put(baseName, origenes);
						}
					}
					else{
						ArrayList<String> origenes = new ArrayList<String>();
						origenes.add(card.name);
						this.referencias.put(baseName, origenes);
					}
				}
				ability = ability.replace("##", "");
				ability = ability.replace("%%", "");
				card.habs.set(i, ability);
			}
			
			if(card.id.matches(".+\\D$")){
				String baseId = card.id.replaceAll("^(.+?)\\D+$", "$1");
				if(this.parallels.containsKey(baseId)){
					ArrayList<String> parallels = this.parallels.get(baseId);
					if(!parallels.contains(card.id)){
						parallels.add(card.id);
						this.parallels.put(baseId, parallels);
					}
				}
				else{
					ArrayList<String> parallels = new ArrayList<String>();
					parallels.add(card.id);
					this.parallels.put(baseId, parallels);
				}
			}
			
		}
		
		return cards;
	}
	
	public void createIndex_Main() throws Exception{
		
		System.out.println("* Create Empty Index for Set: " + this.setName);

		String setFileId = this.setId.replace("/", "_");
		String indexPath = this.conf.getGeneralResultsFolderPath() + setFileId + "\\";
		File newFile = new File(indexPath + "index.html");
		
		setFileId = setFileId + "-";
		
		String productType = "Booster Pack";
		String padding = "%03d";
		
		if(this.isExtraBooster){
			productType = "Extra Booster";
			padding = "%02d";
		}
		
		List<String> newFileContent = new ArrayList<>();
		
		newFileContent.add("<meta charset=\"utf-8\">");
		newFileContent.add("<head>");
		newFileContent.add("<title></title>");
		newFileContent.add("</head>");
		newFileContent.add("<body>");
		newFileContent.add("<div align=center style=\"font-size:150%\"><b>");
		newFileContent.add(productType + ": " + this.setName);
		newFileContent.add("</b></div>");
		newFileContent.add("<table border=2 width=100%>");
		
		int count = 1;
		int rows = (int) Math.ceil((double)this.regularCardCount / (double)10);
		
		for(int i = 1; i <= rows; i++){
			
			newFileContent.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				newFileContent.add("<td width=10%  align=center>");
				if(count <= this.regularCardCount){
					String paddedCount = String.format(padding, count);
					newFileContent.add(this.getCardIndexLine(paddedCount));
				}
				newFileContent.add("</td>");
				count++;
			}
			
			newFileContent.add("</tr>");
		}
		
		newFileContent.add("</table>");
		
		if(this.extendedCardCount > 0 || this.promoCardCount > 0){
			newFileContent.addAll(this.createIndex_PromotionalLines());
		}
		
		if(this.tdCardCount > 0){
			newFileContent.addAll(this.createIndex_TrialDeck());
		}
		
		newFileContent.add("</body>");
		
		Files.write(newFile.toPath(), newFileContent, StandardCharsets.UTF_8);
	}
	
	public ArrayList<String> createIndex_PromotionalLines() throws Exception{
		
		String padding = "%03d";
		
		if(this.isExtraBooster){
			padding = "%02d";
		}
		
		ArrayList<String> promoLines = new ArrayList<String>();
		
		promoLines.add("<div align=center style=\"font-size:150%\"><b>");
		promoLines.add("Promotional Cards");
		promoLines.add("</b></div>");
		promoLines.add("<table border=2 width=100%>");

		int count = this.regularCardCount + 1;
		int rows = (int) Math.ceil((double)this.extendedCardCount / (double)10);
		
		for(int i = 1; i <= rows; i++){
			
			promoLines.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				promoLines.add("<td width=10%  align=center>");				
				if(count <= this.regularCardCount + this.extendedCardCount){
					String paddedCount = String.format(padding, count);
					promoLines.add(this.getCardIndexLine(paddedCount));
				}
				promoLines.add("</td>");
				count++;
			}
			
			promoLines.add("</tr>");
		}
		
		count = 1;
		rows = (int) Math.ceil((double)this.promoCardCount / (double)10);
		
		for(int i = 1; i <= rows; i++){
			
			promoLines.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				promoLines.add("<td width=10%  align=center>");				
				if(count <= this.promoCardCount){
					String paddedCount = String.format("%02d", count);
					promoLines.add(this.getCardIndexLine("P" + paddedCount));
				}
				promoLines.add("</td>");
				count++;
			}
			
			promoLines.add("</tr>");
		}
		
		promoLines.add("</table>");
		
		return promoLines;
	}
	
	public ArrayList<String> createIndex_TrialDeck() throws Exception{
		
		ArrayList<String> tdLines = new ArrayList<String>();
		
		tdLines.add("<div align=center style=\"font-size:150%\"><b>");
		tdLines.add("<a id='trial'>");
		String tdHeader = "Trial Deck";
		if(!this.isLegacyTd){tdHeader = tdHeader + " Plus";}
		tdLines.add(tdHeader);
		tdLines.add("</b></div>");
		tdLines.add("<table border=2 width=100%>");

		int count = 1;
		int rows = (int) Math.ceil((double)this.tdCardCount / (double)10);
		
		for(int i = 1; i <= rows; i++){
			
			tdLines.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				tdLines.add("<td width=10%  align=center>");				
				if(count <= this.tdCardCount){
					String paddedCount = String.format("%02d", count);
					tdLines.add(this.getCardIndexLine("T" + paddedCount));
				}
				tdLines.add("</td>");
				count++;
			}
			
			tdLines.add("</tr>");
		}
		
		tdLines.add("</table>");
		
		return tdLines;
	}
	
	public String getCardIndexLine(String paddedCount) throws Exception{
		
		String line = "";
		String setBaseFileId = this.setId.replace("/", "_");
		String setFileId = this.setId.replace("/", "_") + "-";
		
		File targetCardPage = new File(this.conf.getGeneralResultsFolderPath() + setBaseFileId + "\\cards\\" + setFileId + paddedCount + ".html");
		File targetCardImage = new File(this.conf.getGeneralResultsFolderPath() + setBaseFileId + "\\images\\" + setFileId + paddedCount + ".png");
		if(targetCardPage.exists()){
			line = "<a href='./cards/" + setFileId + paddedCount + ".html'><img src='./images/" + setFileId + paddedCount + ".png' width=100% height=auto'></img></a>" + this.setId + "-" + paddedCount;
		}
		else{
			if(targetCardImage.exists()){
				line = "<img style='filter: grayscale(100%);' src='./images/" + setFileId + paddedCount + ".png' width=100% height=auto'></img>" + this.setId + "-" + paddedCount;	
			}
			else{
				// Maybe it is a variation -> quick and dirty recheck
				targetCardPage = new File(this.conf.getGeneralResultsFolderPath() + setBaseFileId + "\\cards\\" + setFileId + paddedCount + "a.html");
				targetCardImage = new File(this.conf.getGeneralResultsFolderPath() + setBaseFileId + "\\images\\" + setFileId + paddedCount + "a.png");
				if(targetCardPage.exists()){
					line = "<a href='./cards/" + setFileId + paddedCount + "a.html'><img src='./images/" + setFileId + paddedCount + "a.png' width=100% height=auto'></img></a>" + this.setId + "-" + paddedCount;
				}
				else{
					if(targetCardImage.exists()){
						line = "<img style='filter: grayscale(100%);' src='./images/" + setFileId + paddedCount + "a.png' width=100% height=auto'></img>" + this.setId + "-" + paddedCount;	
					}
				}
			}
		}
		
		return line;
	}
	
	public void createWebIndex_Main() throws Exception{
		
		System.out.println("* Create Empty Index for Web: " + this.setName);
		
		String productImagesPath = this.conf.getStaticWebFolderPath() + "//ProductImages//";
		String imageReferencePath = productImagesPath + "imageReferences.txt";
		String webIndexPath = this.conf.getStaticWebFolderPath() + "index.html";
		
		File completedIndexFile = new File(this.conf.getStaticWebFolderPath() + "completedIndex.html");
		Document doc = Jsoup.parse(completedIndexFile, "UTF-8");
		
		ArrayList<String> imageReferences = new ArrayList<>(Files.readAllLines(new File(imageReferencePath).toPath(), StandardCharsets.UTF_8));
		
		List<String> newFileContent = new ArrayList<>();
		
		newFileContent.add("<meta charset=\"utf-8\">");
		newFileContent.add("<head>");
		newFileContent.add("<title>Traducciones por Producto</title>");
		newFileContent.add("</head>");
		newFileContent.add("<body>");
		newFileContent.add("<a href='./completedIndex.html'><b>");
		newFileContent.add("Lista de Traducciones Completadas");
		newFileContent.add("</b></a><br><br>");
		newFileContent.add("<div style='font-size:150%'><b>");
		newFileContent.add("Traducciones por Producto: ");
		newFileContent.add("</b></div><br>");

		for(String reference : imageReferences){
			if(!reference.isEmpty()){
				String[] references = reference.split("\t");
				if(references[3].equals("Useful")) {
					String cssQuery = "[src*=" + references[2] + "]";
					Elements imgs = doc.select(cssQuery);
					if(imgs.size() > 0) {
						String href = imgs.first().parent().parent().attr("href");
						newFileContent.add("<a href='" + href + "'><div style='display:inline-block;'><div style='display:table;width:200px;height:200px;border:thin black solid;text-align:center;'><div style='display:table-cell;vertical-align:middle;'><img style='display:inline-block;max-height:190px;max-width:190px;vertical-align:middle;' src='./ProductImages/" + references[2] + "'></img></div></div></div></a>");
					}
					else {
						newFileContent.add("<div style='display:inline-block;'><div style='display:table;width:200px;height:200px;border:thin black solid;text-align:center;'><div style='display:table-cell;vertical-align:middle;'><img style='display:inline-block;max-height:190px;max-width:190px;vertical-align:middle;filter:grayscale(100%);' src='./ProductImages/" + references[2] + "'></img></div></div></div>");
					}
				}
			}
		}
		
		newFileContent.add("</body>");
		
		Files.write(new File(webIndexPath).toPath(), newFileContent, StandardCharsets.UTF_8);
	}
}
