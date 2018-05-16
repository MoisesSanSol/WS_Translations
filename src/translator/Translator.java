package translator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import configuration.LocalConf;
import cards.Card;

public class Translator {

	private LocalConf conf;
	
	public ArrayList<LineTranslation> lineTranslations;
	
	public Translator() throws Exception{
		this.conf = LocalConf.getInstance();
		this.lineTranslations = new ArrayList<LineTranslation>();
		this.loadPatterns(this.conf.translationPairsFullListFile);
	}
	
	private void loadPatterns(File translationPairs) throws Exception{
		
		List<String> content = new ArrayList<>(Files.readAllLines(translationPairs.toPath(), StandardCharsets.UTF_8));
				
		while(content.size() > 2){
			
			String patternLine = content.remove(0);
			String replacementLine = content.remove(0);
			content.remove(0); // Ignore line
			if(!replacementLine.equals("")){
				this.lineTranslations.add(new LineTranslation(patternLine, replacementLine));
			}
		}
	}
	
	public LineTranslation findAbilityTranslationPair(String ability){
		for(LineTranslation attempt : this.lineTranslations){
			
			Matcher m = attempt.pattern.matcher(ability);
			if(m.find()){
				return attempt;
			}
		}
		return null;
	}
	
	public ArrayList<Card> translateSet(ArrayList<Card> cards){
		ArrayList<Card> results = new ArrayList<Card>();
		
		for(Card card : cards){
			Card translated = this.translateCard(card);
			results.add(translated);
		}
		
		return results;
	}
	
	public ArrayList<Card> translateSetPrettyOutput(ArrayList<Card> cards){
		ArrayList<Card> results = new ArrayList<Card>();
		
		for(Card card : cards){
			Card translated = this.translateCardPrettyOutput(card);
			results.add(translated);
		}
		
		return results;
	}

	public Card translateCard(Card card){

		Card result = new Card(card);
		
		result.type = this.translateType(card.type);
		result.trigger = this.translateTrigger(card.trigger);
		result.trait1 = this.cleanTrait(card.trait1);
		result.trait2 = this.cleanTrait(card.trait2);
		result.color = this.translateColor(card.color);
		
		for(int i = 0; i < card.habs.size(); i++){
			for(LineTranslation attempt : this.lineTranslations){
				String ability = result.habs.get(i);
				if(attempt.matchesAbility(ability)){
					result.habs.set(i, attempt.translateAbility(ability));
					break;
				}
			}
		}
		
		return result;
	}
	
	public Card translateCardPrettyOutput(Card card){
		Card result = new Card(card);
		
		for(int i = 0; i < card.habs.size(); i++){

			for(LineTranslation attempt : this.lineTranslations){

				Matcher m = attempt.pattern.matcher(result.habs.get(i));
				if(m.find()){
					try{
						result.habs.set(i, m.replaceAll(attempt.replace).replaceAll("%%", "").replaceAll("@@", "").replaceAll("##", ""));
					}catch(Exception ex){
						System.out.println(m.pattern().toString());
						throw ex;
					}
					break;
				}
			}
		}

		return result;
	}
	
	public String translateAbility(String ability){
		for(LineTranslation attempt : this.lineTranslations){
			
			Matcher m = attempt.pattern.matcher(ability);
			if(m.find()){
				return m.replaceAll(attempt.replace);
			}
		}
		return ability;
	}
	
	public String translateAbilityPrettyOutput(String ability){
		for(LineTranslation attempt : this.lineTranslations){
			
			Matcher m = attempt.pattern.matcher(ability);
			if(m.find()){
				String replaced = m.replaceAll(attempt.replace);
				return replaced.replaceAll("%%", "").replaceAll("@@", "").replaceAll("##", "");
			}
		}
		return ability;
	}
	
	private String translateType(String type){
		String translatedType = type;
		
		switch(type){
			case "Character":
				translatedType = "Personaje";
				break;
			case "Event":
				translatedType = "Evento";
				break;
			case "Climax":
				translatedType = "Climax";
				break;
		}
		
		return translatedType;
	}
	
	private String translateTrigger(String trigger){
		String translatedTrigger = trigger;
		
		switch(trigger){
			case "None":
				translatedTrigger = "Sin Trigger";
				break;
			case "Soul":
				translatedTrigger = "1 Soul";
				break;
			case "Soul Bounce":
				translatedTrigger = "1 Soul, Return";
				break;
			case "Soul Shot":
				translatedTrigger = "1 Soul, Shot";
				break;
			case "Treasure":
				translatedTrigger = "Treasure";
				break;
			case "Salvage":
				translatedTrigger = "Comeback";
				break;
			case "Draw":
				translatedTrigger = "Book";
				break;
			case "Stock":
				translatedTrigger = "Pool";
				break;
			case "Soul Gate":
				translatedTrigger = "Gate, 1 Soul";
				break;
		}
		
		return translatedTrigger;
	}
	
	private String cleanTrait(String trait){
		String cleanedTrait = trait;
		
		if (trait.equals("None")){
			cleanedTrait = "Sin Trait";
		}
		else{
			cleanedTrait = trait.replaceAll(".+ \\((.+?)\\)", "$1");
		}
		
		return cleanedTrait;
	}
	
	private String translateColor(String color){
		String translatedColor = color;
		
		switch(color){
			case "Red":
				translatedColor = "Rojo";
				break;
			case "Yellow":
				translatedColor = "Amarillo";
				break;
			case "Blue":
				translatedColor = "Azul";
				break;
			case "Green":
				translatedColor = "Verde";
				break;
			case "Purple":
				translatedColor = "Morado";
				break;
		}
		
		return translatedColor;
	}
}
