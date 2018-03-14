package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cards.Card;

public class CardListUtilities {

	public static HashMap<String,String> getIdNamePairs(ArrayList<Card> cards){
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		for(Card card: cards){
			pairs.put(card.id, card.name);
		}
		
		return pairs;
	}
	
	public static HashMap<String,String> getNameIdPairs(ArrayList<Card> cards){
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		for(Card card: cards){
			pairs.put(card.name, card.id);
		}
		
		return pairs;
	}
	
	public static ArrayList<Card> filterOutParallelCards(ArrayList<Card> allCards){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.matches(".+\\da?")){
					cards.add(card);
				}
			}
		}
		
		return cards;
	}
	
	public static ArrayList<String> getAbilities_Sorted(ArrayList<Card> cards){
		ArrayList<String> abilities = new ArrayList<String>(); 
		
		for(Card card : cards){
			for(String ability : card.habs){
				if(!abilities.contains(ability)){
					abilities.add(ability);
				}
			}
		}
		
		Collections.sort(abilities);
		return abilities;
	}
}
