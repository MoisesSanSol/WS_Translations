package cards;

public class CardUtilities {

	public static String compareCards(Card card1, Card card2){
		
		String differences = "";

		try{
		
			if(!card1.name.equals(card2.name)){
				differences = differences + "name difference:\r\n" + card1.name + "\r\n" + card2.name + "\r\n";
			}
			if(!card1.jpName.equals(card2.jpName)){
				differences = differences + "jpName difference:\r\n" + card1.jpName + "\r\n" + card2.jpName + "\r\n";
			}
			if(!card1.id.equals(card2.id)){
				differences = differences + "id difference:\r\n" + card1.id + "\r\n" + card2.id + "\r\n";
			}
			if(!card1.fileId.equals(card2.fileId)){
				differences = differences + "fileId difference:\r\n" + card1.fileId + "\r\n" + card2.fileId + "\r\n";
			}
			if(!card1.rarity.equals(card2.rarity)){
				differences = differences + "rarity difference:\r\n" + card1.rarity + "\r\n" + card2.rarity + "\r\n";
			}
			if(!card1.color.equals(card2.color)){
				differences = differences + "color difference:\r\n" + card1.color + "\r\n" + card2.color + "\r\n";
			}
			if(!card1.side.equals(card2.side)){
				differences = differences + "side difference:\r\n" + card1.side + "\r\n" + card2.side + "\r\n";
			}
			if(!card1.type.equals(card2.type)){
				differences = differences + "type difference:\r\n" + card1.type + "\r\n" + card2.type + "\r\n";
			}
			if(!card1.level.equals(card2.level)){
				differences = differences + "level difference:\r\n" + card1.level + "\r\n" + card2.level + "\r\n";
			}
			if(!card1.cost.equals(card2.cost)){
				differences = differences + "cost difference:\r\n" + card1.cost + "\r\n" + card2.cost + "\r\n";
			}
			if(!card1.power.equals(card2.power)){
				differences = differences + "power difference:\r\n" + card1.power + "\r\n" + card2.power + "\r\n";
			}
			if(!card1.soul.equals(card2.soul)){
				differences = differences + "soul difference:\r\n" + card1.soul + "\r\n" + card2.soul + "\r\n";
			}
			if(!card1.trigger.equals(card2.trigger)){
				differences = differences + "trigger difference:\r\n" + card1.trigger + "\r\n" + card2.trigger + "\r\n";
			}
			if(!card1.trait1.equals(card2.trait1)){
				differences = differences + "trait1 difference:\r\n" + card1.trait1 + "\r\n" + card2.trait1 + "\r\n";
			}
			if(!card1.trait2.equals(card2.trait2)){
				differences = differences + "trait2 difference:\r\n" + card1.trait2 + "\r\n" + card2.trait2 + "\r\n";
			}
			/* Current unused
			if(!card1.flavor.equals(card2.flavor)){
				differences = differences + "flavor difference:\r\n" + card1.flavor + "\r\n" + card2.flavor + "\r\n";
			}
			*/
			
			if(card1.habs.size() != card2.habs.size()){
				differences = differences + "number of abilities difference:\r\n" + card1.habs.size() + "\r\n" + card2.habs.size() + "\r\n";
			}
			else{
				for(int i = 0; i < card1.habs.size(); i++){
					if(!card1.habs.get(i).equals(card2.habs.get(i))){
						differences = differences + "ability difference:\r\n" + card1.habs.get(i) + "\r\n" + card2.habs.get(i) + "\r\n";
					}
				}	
			}
		}
		catch(Exception any){
			differences = "Card is broken.";
		}
		
		return (differences.isEmpty()) ? null : differences;
	}
	
}
