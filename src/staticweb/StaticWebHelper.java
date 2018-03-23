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
	HashMap<String,ArrayList<String>> parallels;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		StaticWebHelper staticWebHelper = new StaticWebHelper();
		//staticWebHelper.generateStaticSetWeb("hina_logic_vol._1_extra_pack");
		//staticWebHelper.createEmptyIndex();
		//staticWebHelper.generateStaticSetPrPages("weib_promos");
		//staticWebHelper.generateStaticSetWeb("saekano_-_how_to_raise_a_boring_girlfriend_trial_deck");
		//staticWebHelper.createTrialDeckIndex();
		//staticWebHelper.createBoosterPackIndex();
		//staticWebHelper.rotateClimax("saekano_-_how_to_raise_a_boring_girlfriend_trial_deck");
		
		HotcCleanFileParser parser = new HotcCleanFileParser();
		File file = new File(LocalConf.getInstance().gethotcCleanFilesFolderPath() + "saekano_-_how_to_raise_a_boring_girlfriend_booster_pack.txt");
		staticWebHelper.prepareCardsForWeb(parser.parseCards(file));
		//staticWebHelper.generateStaticSetWeb(CardListUtilities.filterCards_FindSetPrs(parser.parseCards(file),"SHS/W56"));
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebHelper() throws Exception{
		this.conf = LocalConf.getInstance();
		this.referencias = new HashMap<String,ArrayList<String>>();
	}
	
	public void generateStaticSetWeb(String setName) throws Exception{
		
		ArrayList<Card> allCards = TextFileParser.parseCards(setName);
		this.generateStaticSetWeb(allCards);		
	}
	
	public void generateStaticSetWeb(ArrayList<Card> allCards) throws Exception{
		
		Translator translator = new Translator();
		
		ArrayList<Card> baseCards = CardListUtilities.filterOutParallelCards(allCards);
		this.pairs = CardListUtilities.getNameIdPairs(baseCards);
		
		ArrayList<Card> translatedCards = translator.translateSet(allCards);
		ArrayList<Card> cleanCards = this.prepareCardsForWeb(translatedCards);
		
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
	
	public String getCardIdLine(Card card) throws Exception{
		String line = card.id + " " + card.rarity;
		if(this.parallels.containsKey(card.id)){
			line = line + "(<a href='./" + ".html'>SP</a>)";
		}
		else if(card.id.matches(".+\\D$")){
			String baseId = card.id.replaceAll("^(.+?)\\D+$", "$1");
		}
		return line;
	}
	
	public void createCardWebPage(Card card, ArrayList<String> baseTemplate) throws Exception{

		ArrayList<String> template = (ArrayList<String>)baseTemplate.clone();
		
		String filenameFriendlyId = card.id.replace("/", "_");
		
		template.set(template.indexOf("[Card Id]"), card.id);
		template.set(template.indexOf("[Image]"), "<img src='../images/" + filenameFriendlyId + ".png'></img>");
		template.set(template.indexOf("[Nombre]"), card.name);
		template.set(template.indexOf("[Nombre Jp]"), card.jpName);
		template.set(template.indexOf("[Card Id Line]"), this.getCardIdLine(card));

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
	
	public ArrayList<Card> prepareCardsForWeb(ArrayList<Card> cards) throws Exception{
		
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
						if(!origenes.contains(card.name)){
							origenes.add(card.name);
							this.referencias.put(refName, origenes);
						}
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
	
	public void createBoosterPackIndex() throws Exception{
		
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
	
	public void createTrialDeckIndex() throws Exception{
		
		System.out.println("* Create Empty Index");

		String seriesFullId = "SHS/W56-T";
		String seriesFullIdFriendly = "SHS_W56-T";
		String productType = "Trial Deck Plus";
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
		for(int i = 1; i <= 2; i++){
			
			newFileContent.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				
				String paddedCount = String.format("%02d", count);;
				
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
}
