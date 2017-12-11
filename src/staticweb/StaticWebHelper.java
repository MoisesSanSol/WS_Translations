package staticweb;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import translations.Card;
import translations.TextFileParser;
import translations.Translator;
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
		staticWebHelper.generateStaticSetWeb("hina_logic_vol._1_extra_pack");
		
		System.out.println("*** Finished ***");
	}
	
	public StaticWebHelper() throws Exception{
		this.conf = LocalConf.getInstance();
		this.referencias = new HashMap<String,ArrayList<String>>();
	}
	
	public void generateStaticSetWeb(String setName) throws Exception{
		
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

			String arteAlternativo = "<a href='./" + filenameFriendlyId + "-S.html'>Arte Alternativo</a>"; 
			templateContent.set(templateContent.indexOf("[Arte Alternativo]"), arteAlternativo);
			
			String caracteristicas = "";
			
			caracteristicas = caracteristicas + "Personaje " + card.color;
			caracteristicas = caracteristicas + ", Nivel: " + card.level;
			caracteristicas = caracteristicas + ", Poder: " + card.power;
			caracteristicas = caracteristicas + ", Soul: " + card.soul;
			caracteristicas = caracteristicas + ", Trigger: " + card.trigger;
			caracteristicas = caracteristicas + ", Traits: <<" + card.trait1;
			caracteristicas = caracteristicas + ">> y <<" + card.trait2;
			caracteristicas = caracteristicas + ">>.";
			templateContent.set(templateContent.indexOf("[Caracteristicas]"), Utilities.escapeHtml(caracteristicas));

			String habilidades = "";
			for(int i = 0; i < card.habs.size(); i++){
				habilidades = habilidades + this.ProcessAbility(card.habs.get(i), card.name);
				if(i < card.habs.size()-1){
					habilidades = habilidades + "\r\n<br>\r\n";
				}
			}
			templateContent.set(templateContent.indexOf("[Habilidades]"), habilidades);
			
			String cardsPath = this.conf.getGeneralResultsFolderPath() + "hinaext1.0\\cards\\"; 
			Files.write(new File(cardsPath + filenameFriendlyId + ".html").toPath(), templateContent, StandardCharsets.UTF_8);
		}
		
		this.addReferencias(setName, cards);
	}
	
	private String ProcessAbility(String habilidad, String name) throws Exception{
		
		habilidad = Utilities.escapeHtml(habilidad);
		
		habilidad = habilidad.replaceAll("##", "##").replaceAll("%%", "%%");
		
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
			referencia.add(name);
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
}
