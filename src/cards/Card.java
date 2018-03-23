package cards;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Card {
	
	public String name;
	public String jpName;
	public String id;
	public String fileId;
	public String rarity;
	public String color;
	public String side;
	public String type;
	public String level;
	public String cost;
	public String power;
	public String soul;
	public String trigger;
	public String trait1;
	public String trait2;
	public String flavor;
	public ArrayList<String> habs;
	
	
	public Card(String nameLine, String jpNameLine, String idLine, String colorLine, String levelLine, String traitLine, String triggerLine, ArrayList<String> habLines){
		
		Pattern r;
		Matcher m;
		
		this.name = nameLine;
		this.jpName = jpNameLine;
        r = Pattern.compile("Card No.: (.+?)  Rarity: (.+)");
        m = r.matcher(idLine);
        if(m.find()){
        	this.id = m.group(1);
        	this.fileId = this.id.replace("/", "_");
        	this.rarity = m.group(2);
        }
        else{
        	System.out.println("ERROR in idLine:" + idLine);
        }

        r = Pattern.compile("Color: (.+?)   Side: (.+?)  (.+)");
        m = r.matcher(colorLine);
        if(m.find()){
        	this.color = m.group(1);
        	this.side = m.group(2);
        	this.type = m.group(3);
        }
        else{
        	System.out.println("ERROR in colorLine:" + colorLine);
        }
        
        r = Pattern.compile("Level: (\\d)   Cost: (\\d+)   Power: (\\d+?)   Soul: (\\d)");
        m = r.matcher(levelLine);
        if(m.find()){
        	this.level = m.group(1);
        	this.cost = m.group(2);
        	this.power = m.group(3);
        	this.soul = m.group(4);
        }
        else{
        	System.out.println("ERROR in levelLine:" + levelLine);
        }
        
        r = Pattern.compile("Trait 1: (.+?)      Trait 2: (.+)");
        m = r.matcher(traitLine);
        if(m.find()){
        	this.trait1 = m.group(1);
        	this.trait2 = m.group(2);
        }
        else{
        	System.out.println("ERROR in traitLine:" + traitLine);
        }
        
        r = Pattern.compile("Triggers: (.+)");
        m = r.matcher(triggerLine);
        if(m.find()){
        	this.trigger = m.group(1);
        }
        else{
        	System.out.println("ERROR in triggerLine:" + triggerLine);
        }

		this.habs = habLines;
		
	}
	
	public Card(Card toCopy){
		this.name = toCopy.name;
		this.jpName = toCopy.jpName;
		this.id = toCopy.id;
		this.rarity = toCopy.rarity;
		this.color = toCopy.color;
		this.side = toCopy.side;
		this.type = toCopy.type;
		this.level = toCopy.level;
		this.cost = toCopy.cost;
		this.power = toCopy.power;
		this.soul = toCopy.soul;
		this.trigger = toCopy.trigger;
		this.trait1 = toCopy.trait1;
		this.trait2 = toCopy.trait2;
		this.flavor = toCopy.flavor;
		this.habs = new ArrayList<String>();
		for(String hab : toCopy.habs){
			this.habs.add(hab);
		}
	}
	
	/* No s� qu� quer�a hacer con esto
	public boolean match(Card card){
		boolean isTheSame = true;
		return isTheSame;
	}
	*/
}
