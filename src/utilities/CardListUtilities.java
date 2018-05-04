package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import configuration.LocalConf;
import parser.HotcCleanFileParser;
import cards.Card;
import cards.CardUtilities;

public class CardListUtilities {

	public static int getMaxPrNumber(ArrayList<Card> cards){
		
		int max = 0;
		
		for(Card card: cards){
			String numberStr = card.id.replaceAll(".+?-P(\\d+)", "$1");
			int cardNumber = Integer.parseInt(numberStr);
			if(cardNumber > max){
				max = cardNumber; 
			}
		}
		
		return max;
	}
	
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
	
	public static ArrayList<Card> filterInParallelCards(ArrayList<Card> allCards){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(!card.id.matches(".+\\da?")){
					if(!card.id.matches(".+[b-z]$")){
						cards.add(card);
					}
				}
			}
		}
		
		return cards;
	}
	
	public static ArrayList<Card> filterInMultipleImageCards(ArrayList<Card> allCards){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.matches(".+[b-z]$")){
					cards.add(card);
				}
			}
		}
		
		return cards;
	}
	
	public static ArrayList<Card> filterCards_FindSetPrs_All(ArrayList<Card> allCards, String setId){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.startsWith(setId)){
					cards.add(card);
				}
			}
		}
		
		return cards;
	}
	
	public static ArrayList<Card> filterCards_FindSetPrs_Pr(ArrayList<Card> allCards, String setId){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.startsWith(setId) && card.id.contains("-P")){
					cards.add(card);
				}
			}
		}
		
		return cards;
	}
	
	public static ArrayList<Card> filterCards_FindSetPrs_Extended(ArrayList<Card> allCards, String setId){
		ArrayList<Card> cards = new ArrayList<Card>();
		
		for(Card card : allCards){
			if(card.id != null){
				if(card.id.startsWith(setId) && !card.id.contains("-P")){
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
	
	public static HashMap<String,String> getIdRarityPairs(ArrayList<Card> cards){
		
		HashMap<String,String> pairs = new HashMap<String,String>();
		for(Card card: cards){
			pairs.put(card.id, card.rarity);
		}
		
		return pairs;
	}
	
	public static HashMap<String,ArrayList<String>> getHomonymIdPairs(ArrayList<Card> cards){
		
		HashMap<String,ArrayList<String>> homonimas = new HashMap<String,ArrayList<String>>();
		HashMap<String,String> processed = new HashMap<String,String>();
		
		for(Card card: cards){
			if(processed.containsKey(card.name)){
				if(homonimas.containsKey(card.name)){
					ArrayList<String> ids = homonimas.get(card.name);
					if(!ids.contains(card.id)){
						ids.add(card.id);
						homonimas.put(card.name, ids);
					}
				}
				else{
					ArrayList<String> ids = new ArrayList<String>();
					ids.add(card.id);
					homonimas.put(card.name, ids);
				}
			}
			else{
				processed.put(card.name, card.id);
			}
		}
		
		return homonimas;
	}
	
	public static ArrayList<Card> getCards_All() throws Exception{
		
		ArrayList<Card> allCards = new ArrayList<Card>(); 
		
		for(File cleanFile : LocalConf.getInstance().hotcCleanFilesFolder.listFiles()){
			
			allCards.addAll(HotcCleanFileParser.parseCards(cleanFile));
		}
		
		return allCards;
	}
	
	public static ArrayList<String> getAbilities_AllSorted() throws Exception{
		
		ArrayList<Card> allCards = CardListUtilities.getCards_All();
		
		return CardListUtilities.getAbilities_Sorted(allCards);
	}
	
	public static Card filterCards_FindCard_ById(ArrayList<Card> cards, String id){
		
		Card card = null;
		for(Card attempt : cards){
			if(attempt.id != null){
				if(attempt.id.equals(id)){
					return attempt;
				}
			}
		}
		return card;
	}
	
	public static HashMap<String, String> compareCards(ArrayList<Card> oldCards, ArrayList<Card> newCards){
		
		HashMap<String, String> differences = new HashMap<String, String>();
		
		for(Card newCard : newCards){
			Card oldCard = filterCards_FindCard_ById(oldCards, newCard.id);
			if(oldCard == null){
				differences.put(newCard.id, "New Card\r\n");
			}
			else{
				String cardDifferences = CardUtilities.compareCards(newCard, oldCard);
				if (cardDifferences != null){
					differences.put(newCard.id, cardDifferences);
				}
				oldCards.remove(oldCard);
			}
		}
		for(Card oldCard : oldCards){
			differences.put(oldCard.id, "Removed Card\r\n");
		}

		return differences;
	}
	
	
}
