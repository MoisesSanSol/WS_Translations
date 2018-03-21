package staticweb;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import output.Summaries;
import parser.HotcCleanFileParser;
import cards.Card;
import translations.TextFileParser;
import translator.Translator;
import utilities.CardListUtilities;
import utilities.Utilities;
import configuration.LocalConf;

public class StaticWebHelper {
	
	LocalConf conf;
	
	HashMap<String,String> pairs;
	HashMap<String,ArrayList<String>> referencias;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		//staticWebHelper.generateStaticSetWeb("hina_logic_vol._1_extra_pack");
		//staticWebHelper.createEmptyIndex();
		//staticWebHelper.generateStaticSetPrPages("weib_promos");
		//staticWebHelper.generateStaticSetWeb("saekano_-_how_to_raise_a_boring_girlfriend_booster_pack");
		//staticWebHelper.createBoosterPackIndex();
		staticWebHelper.rotateClimax("saekano_-_how_to_raise_a_boring_girlfriend_booster_pack");
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebHelper() throws Exception{
		this.conf = LocalConf.getInstance();
		this.referencias = new HashMap<String,ArrayList<String>>();
	}
	
	public void generateStaticSetWeb(String setName) throws Exception{
		
		Translator translator = new Translator();
		
		ArrayList<Card> allCards = TextFileParser.parseCards(setName);
		ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(allCards);
		this.pairs = CardListUtilities.getNameIdPairs(baseCards);
		
		ArrayList<Card> translatedCards = translator.translateSet(baseCards);
		ArrayList<Card> cleanCards = this.prepareAbilitiesForWeb(translatedCards);
		
		String templatePath = this.conf.getGeneralResultsFolderPath() + "cards\\template.html";
		ArrayList<String> templateContent = new ArrayList<>(Files.readAllLines(new File(templatePath).toPath(), StandardCharsets.UTF_8));
		
		for(Card card : cleanCards){
			this.createCardWebPage(card, templateContent);
		}
		
	}
	
	public void rotateClimax(String setName) throws Exception{
		
		ImagesHelper imagesHelper = new ImagesHelper();
		
		ArrayList<Card> allCards = TextFileParser.parseCards(setName);
		
		for(Card card : allCards){
			if(card.type.equals("Climax")){
				String filenameFriendlyId = card.id.replace("/", "_");
				File imageFile = new File(this.conf.getGeneralResultsFolderPath() + "images//" + filenameFriendlyId + ".png");
				imagesHelper.rotateWebFormatImage(imageFile);
			}
		}
		
	}
	
	public void createCardWebPage(Card card, ArrayList<String> baseTemplate) throws Exception{

		ArrayList<String> template = (ArrayList<String>)baseTemplate.clone();
		
		String filenameFriendlyId = card.id.replace("/", "_");
		
		template.set(template.indexOf("[Card Id]"), card.id);
		template.set(template.indexOf("[Image]"), "<img src='../images/" + filenameFriendlyId + ".png'></img>");
		template.set(template.indexOf("[Nombre]"), card.name);
		template.set(template.indexOf("[Nombre Jp]"), card.jpName);
		template.set(template.indexOf("[Card Id Line]"), card.id + " " + card.rarity);

		String caracteristicas = "";
		
		if(card.type.equals("Personaje")){
			caracteristicas = caracteristicas + "Personaje " + card.color;
			caracteristicas = caracteristicas + ", Nivel: " + card.level;
			caracteristicas = caracteristicas + ", Poder: " + card.power;
			caracteristicas = caracteristicas + ", Soul: " + card.soul;
			caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
			caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
			caracteristicas = caracteristicas + ">> y <<" + card.trait2;
			caracteristicas = caracteristicas + ">>.";
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
		
		if(this.referencias.containsKey(card.name)){
			
			String referenciada = "<tr>\r\n<td>\r\n";
			for(String nombre : this.referencias.get(card.name)){
				String nombreFriendlyId = this.pairs.get(nombre).replace("/", "_");
				referenciada = referenciada + "* Esta carta es referenciada en las habilidades de '<a href='./" + nombreFriendlyId + ".html'>" + nombre + "</a></a>'";
				if(!(this.referencias.get(card.name).indexOf(nombre) == this.referencias.get(card.name).size() - 1)){
					referenciada = referenciada + "\r\n<br>\r\n";
				}
			}
			referenciada = referenciada + "\r\n</td>\r\n</tr>";
			
			template.set(template.indexOf("[Referencias]"), referenciada);
		}
		else{
			template.remove(template.indexOf("[Referencias]"));
		}
		
		String cardsPath = this.conf.getGeneralResultsFolderPath() + "cards\\"; 
		Files.write(new File(cardsPath + filenameFriendlyId + ".html").toPath(), template, StandardCharsets.UTF_8);

		
	}
	
	public ArrayList<Card> prepareAbilitiesForWeb(ArrayList<Card> cards) throws Exception{
		
		this.referencias = new HashMap<String,ArrayList<String>>();
		
		for(Card card : cards){
			for(int i = 0; i < card.habs.size(); i++){
				String ability = card.habs.get(i);
				ability = Utilities.escapeHtml(ability);
				while(ability.contains("@@")){
					String[] splitting =  ability.split("@@");
					String refName = splitting[1];
					String refId = this.pairs.get(refName);
					String urlRefId = refId.replace("/", "_");
					String referencia = "<a href='./" + urlRefId + ".html'>" + refName + "</a>";
					ability = ability.replace("@@" + refName + "@@", referencia);
					if(this.referencias.containsKey(refName)){
						ArrayList<String> origenes = referencias.get(refName);
						origenes.add(card.name);
						this.referencias.put(refName, origenes);
					}
					else{
						ArrayList<String> origenes = new ArrayList<String>();
						origenes.add(card.name);
						this.referencias.put(refName, origenes);
					}
				}
				ability = ability.replace("##", "");
				ability = ability.replace("%%", "");
				card.habs.set(i, ability);
			}
		}
		
		return cards;
	}
	
	private void generateStaticSetWebOld(String setName) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(setName);
		ArrayList<Card> rawCards = CardListUtilities.filterOutParallelCards(allCards);
		Translator translator = new Translator();
		ArrayList<Card> cards = translator.translateSet(rawCards);
		
		this.pairs = CardListUtilities.getNameIdPairs(cards);
		
		for(Card card : cards){
			
			String templatePath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\template.html";
			
			List<String> templateContent = new ArrayList<>(Files.readAllLines(new File(templatePath).toPath(), StandardCharsets.UTF_8));

			String filenameFriendlyId = card.id.replace("/", "_");
			
			templateContent.set(templateContent.indexOf("[Card Id]"), card.id);
			templateContent.set(templateContent.indexOf("[Image]"), "<img src='../images/" + filenameFriendlyId + ".png'></img>");
			templateContent.set(templateContent.indexOf("[Nombre]"), card.name);
			templateContent.set(templateContent.indexOf("[Nombre Jp]"), card.jpName);
			templateContent.set(templateContent.indexOf("[Card Id Line]"), card.id + " " + card.rarity);

			String arteAlternativo = "<a href='./" + filenameFriendlyId + "-S.html'>Arte Alternativo Foil</a>"; 
			templateContent.set(templateContent.indexOf("[Arte Alternativo]"), arteAlternativo);
			
			String caracteristicas = "";
			
			if(card.type.equals("Personaje")){
				caracteristicas = caracteristicas + "Personaje " + card.color;
				caracteristicas = caracteristicas + ", Nivel: " + card.level;
				caracteristicas = caracteristicas + ", Poder: " + card.power;
				caracteristicas = caracteristicas + ", Soul: " + card.soul;
				caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
				caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
				caracteristicas = caracteristicas + ">> y <<" + card.trait2;
				caracteristicas = caracteristicas + ">>.";
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
			templateContent.set(templateContent.indexOf("[Caracteristicas]"), Utilities.escapeHtml(caracteristicas));

			String habilidades = "";
			for(int i = 0; i < card.habs.size(); i++){
				habilidades = habilidades + this.processAbility(card.habs.get(i), card.name);
				if(i < card.habs.size()-1){
					habilidades = habilidades + "\r\n<br>\r\n";
				}
			}
			templateContent.set(templateContent.indexOf("[Habilidades]"), habilidades);
			
			String cardsPath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\"; 
			Files.write(new File(cardsPath + filenameFriendlyId + ".html").toPath(), templateContent, StandardCharsets.UTF_8);
		}
		
		this.addReferencias(setName, cards);
		this.duplicateExtraBoosterAlternateCards(setName, cards);
	}
	
	private String processAbility(String habilidad, String name) throws Exception{
		
		habilidad = Utilities.escapeHtml(habilidad);
		
		habilidad = habilidad.replaceAll("##", "").replaceAll("%%", "");
		
		while(habilidad.contains("@@")){
			String cardName = habilidad.split("@@")[1];
			String cardId = this.pairs.get(cardName);
			String filenameFriendlyId = cardId.replace("/", "_");
			String link = "<a href='./lel.html'><a href='./" + filenameFriendlyId + ".html'>" + cardName + "</a>";
			habilidad = habilidad.replace("@@" + cardName + "@@", link);
			
			ArrayList<String> referencia;
			if(this.referencias.containsKey(cardName)){
				referencia = this.referencias.get(cardName);
			}
			else{
				referencia = new ArrayList<String>();
			}
			if(!referencia.contains(name)){
				referencia.add(name);
			}
			this.referencias.put(cardName, referencia);
		}
		
		
		
		return habilidad;
	}
	
	private void addReferencias(String setName, ArrayList<Card> cards) throws Exception{
		
		for(Card card : cards){

			String filenameFriendlyId = card.id.replace("/", "_");
			
			String cardsPath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\";
			
			List<String> cardPageContent = new ArrayList<>(Files.readAllLines(new File(cardsPath + filenameFriendlyId + ".html").toPath(), StandardCharsets.UTF_8));
			
			if(this.referencias.containsKey(card.name)){
			
				String referenciada = "<tr>\r\n<td>\r\n";
				for(String nombre : this.referencias.get(card.name)){
					String nombreFriendlyId = this.pairs.get(nombre).replace("/", "_");
					referenciada = referenciada + "* Esta carta es referenciada en las habilidades de '<a href='./" + nombreFriendlyId + ".html'>" + nombre + "</a></a>'";
					if(!(this.referencias.get(card.name).indexOf(nombre) == this.referencias.get(card.name).size() - 1)){
						referenciada = referenciada + "\r\n<br>\r\n";
					}
				}
				referenciada = referenciada + "\r\n</td>\r\n</tr>";
				
				cardPageContent.set(cardPageContent.indexOf("[Referenciada]"), referenciada);
			}
			else{
				cardPageContent.set(cardPageContent.indexOf("[Referenciada]"), "");
			}
			Files.write(new File(cardsPath + filenameFriendlyId + ".html").toPath(), cardPageContent, StandardCharsets.UTF_8);
		}
	}
	
	private void duplicateExtraBoosterAlternateCards(String setName, ArrayList<Card> cards) throws Exception{
		
		for(Card card : cards){

			String filenameFriendlyId = card.id.replace("/", "_");
			
			String cardsPath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\";
			
			List<String> cardPageContent = new ArrayList<>(Files.readAllLines(new File(cardsPath + filenameFriendlyId + ".html").toPath(), StandardCharsets.UTF_8));
			
			cardPageContent.set(cardPageContent.indexOf("<img src='../images/" + filenameFriendlyId + ".png'></img>"), "<img src='../images/" + filenameFriendlyId + "-S.png'></img>");
			cardPageContent.set(cardPageContent.indexOf("<a href='./" + filenameFriendlyId + "-S.html'>Arte Alternativo Foil</a>"), "<a href='./" + filenameFriendlyId + ".html'>Arte Alternativo Normal</a>");

			Files.write(new File(cardsPath + filenameFriendlyId + "-S.html").toPath(), cardPageContent, StandardCharsets.UTF_8);
		}
	}
	
	private void createBoosterPackIndex() throws Exception{
		
		System.out.println("* Create Empty Index");

		String seriesFullId = "SHS/W56-";
		String seriesFullIdFriendly = "SHS_W56-";
		String productType = "Booster Pack";
		String seriesName = "[Saekano] How to Raise a Boring Girlfriend";
		
		String indexPath = this.conf.getGeneralResultsFolderPath();
		File newFile = new File(indexPath + "index.html");
		
		List<String> newFileContent = new ArrayList<>();
		
		newFileContent.add("<meta charset=\"utf-8\">");
		newFileContent.add("<head>");
		newFileContent.add("<title></title>");
		newFileContent.add("</head>");
		newFileContent.add("<body>");
		newFileContent.add("<div align=center style=\"font-size:150%\"><b>");
		newFileContent.add(productType + ": " + seriesName);
		newFileContent.add("</b></div>");
		newFileContent.add("<table border=2 width=100%>");
		
		int count = 1;
		for(int i = 1; i <= 10; i++){
			
			newFileContent.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				
				String paddedCount = String.format("%03d", count);;
				
				newFileContent.add("<td width=10%  align=center>");
				newFileContent.add("<a href='./cards/" + seriesFullIdFriendly + paddedCount + ".html'><img src='./images/" + seriesFullIdFriendly + paddedCount + ".png' width=100% height=auto'></img></a>" + seriesFullId + paddedCount);
				newFileContent.add("</td>");
				
				count++;
			}
			
			newFileContent.add("</tr>");
		}
		
		newFileContent.add("</table>");
		newFileContent.add("</body>");
		
		Files.write(newFile.toPath(), newFileContent, StandardCharsets.UTF_8);
	}
	
	private void generateStaticSetPrPages(String setName) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(setName);
		ArrayList<Card> rawCards = CardListUtilities.filterOutParallelCards(allCards);
		Translator translator = new Translator();
		ArrayList<Card> cards = translator.translateSet(rawCards);
		
		this.pairs = CardListUtilities.getNameIdPairs(cards);
		
		for(Card card : cards){
			
			if(card.id.startsWith("HLL")){
			
			String templatePath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\template.html";
			
			List<String> templateContent = new ArrayList<>(Files.readAllLines(new File(templatePath).toPath(), StandardCharsets.UTF_8));

			String filenameFriendlyId = card.id.replace("/", "_");
			
			templateContent.set(templateContent.indexOf("[Card Id]"), card.id);
			templateContent.set(templateContent.indexOf("[Image]"), "<img src='../images/" + filenameFriendlyId + ".png'></img>");
			templateContent.set(templateContent.indexOf("[Nombre]"), card.name);
			templateContent.set(templateContent.indexOf("[Nombre Jp]"), card.jpName);
			templateContent.set(templateContent.indexOf("[Card Id Line]"), card.id + " " + card.rarity);

			String arteAlternativo = "<a href='./" + filenameFriendlyId + "-S.html'>Arte Alternativo Foil</a>"; 
			templateContent.set(templateContent.indexOf("[Arte Alternativo]"), arteAlternativo);
			
			String caracteristicas = "";
			
			if(card.type.equals("Personaje")){
				caracteristicas = caracteristicas + "Personaje " + card.color;
				caracteristicas = caracteristicas + ", Nivel: " + card.level;
				caracteristicas = caracteristicas + ", Poder: " + card.power;
				caracteristicas = caracteristicas + ", Soul: " + card.soul;
				caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
				caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
				caracteristicas = caracteristicas + ">> y <<" + card.trait2;
				caracteristicas = caracteristicas + ">>.";
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
			templateContent.set(templateContent.indexOf("[Caracteristicas]"), Utilities.escapeHtml(caracteristicas));

			String habilidades = "";
			for(int i = 0; i < card.habs.size(); i++){
				habilidades = habilidades + this.processAbility(card.habs.get(i), card.name);
				if(i < card.habs.size()-1){
					habilidades = habilidades + "\r\n<br>\r\n";
				}
			}
			templateContent.set(templateContent.indexOf("[Habilidades]"), habilidades);
			
			String cardsPath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\"; 
			Files.write(new File(cardsPath + filenameFriendlyId + ".html").toPath(), templateContent, StandardCharsets.UTF_8);
			}
		}
		
		//this.addReferencias(setName, cards);
		//this.duplicateExtraBoosterAlternateCards(setName, cards);
	}
}
