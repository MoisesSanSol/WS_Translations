package cards;

public class CardUtilities {

	public static String compareCards(Card card1, Card card2){
		
		String differences = "";

		if(!card1.name.equals(card2.name)){
			differences = differences + "name difference: " + card1.name + " vs " + card2.name + "\r\n";
		}
		if(!card1.jpName.equals(card2.jpName)){
			differences = differences + "jpName difference: " + card1.jpName + " vs " + card2.jpName + "\r\n";
		}
		if(!card1.id.equals(card2.id)){
			differences = differences + "id difference: " + card1.id + " vs " + card2.id + "\r\n";
		}
		if(!card1.fileId.equals(card2.fileId)){
			differences = differences + "fileId difference: " + card1.fileId + " vs " + card2.fileId + "\r\n";
		}
		if(!card1.rarity.equals(card2.rarity)){
			differences = differences + "rarity difference: " + card1.rarity + " vs " + card2.rarity + "\r\n";
		}
		if(!card1.color.equals(card2.color)){
			differences = differences + "color difference: " + card1.color + " vs " + card2.color + "\r\n";
		}
		if(!card1.side.equals(card2.side)){
			differences = differences + "side difference: " + card1.side + " vs " + card2.side + "\r\n";
		}
		if(!card1.type.equals(card2.type)){
			differences = differences + "type difference: " + card1.type + " vs " + card2.type + "\r\n";
		}
		if(!card1.level.equals(card2.level)){
			differences = differences + "level difference: " + card1.level + " vs " + card2.level + "\r\n";
		}
		if(!card1.cost.equals(card2.cost)){
			differences = differences + "cost difference: " + card1.cost + " vs " + card2.cost + "\r\n";
		}
		if(!card1.power.equals(card2.power)){
			differences = differences + "power difference: " + card1.power + " vs " + card2.power + "\r\n";
		}
		if(!card1.soul.equals(card2.soul)){
			differences = differences + "soul difference: " + card1.soul + " vs " + card2.soul + "\r\n";
		}
		if(!card1.trigger.equals(card2.trigger)){
			differences = differences + "trigger difference: " + card1.trigger + " vs " + card2.trigger + "\r\n";
		}
		if(!card1.trait1.equals(card2.trait1)){
			differences = differences + "trait1 difference: " + card1.trait1 + " vs " + card2.trait1 + "\r\n";
		}
		if(!card1.trait2.equals(card2.trait2)){
			differences = differences + "trait2 difference: " + card1.trait2 + " vs " + card2.trait2 + "\r\n";
		}
		/* Current unused
		if(!card1.flavor.equals(card2.flavor)){
			differences = differences + "flavor difference: " + card1.flavor + " vs " + card2.flavor + "\r\n";
		}
		*/
		
		if(card1.habs.size() != card2.habs.size()){
			differences = differences + "number of abilities difference: " + card1.habs.size() + " vs " + card2.habs.size() + "\r\n";
		}
		else{
			for(int i = 0; i < card1.habs.size(); i++){
				if(!card1.habs.get(i).equals(card2.habs.get(i))){
					differences = differences + "ability difference: " + card1.habs.get(i) + " vs " + card2.habs.get(i) + "\r\n";
				}
			}	
		}
		
		return (differences.isEmpty()) ? null : differences;
	}
	
}
