package translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineTranslation{
	public String patternString;
	public Pattern pattern;
	public String replace;
	
	public LineTranslation(String pattern, String replace){
		this.patternString = pattern;
		String escapedPattern = "^\\Q" + pattern.replace("(.+?)", "\\E(.+?)\\Q") + "\\E$";
		this.pattern = Pattern.compile(escapedPattern);
		this.replace = replace;
	}
	
	public String translateAbility(String ability){
		Matcher m = this.pattern.matcher(ability);
		return m.replaceAll(this.replace);
	}
	
	public boolean matchesPattern(String ability){
		Matcher m = this.pattern.matcher(ability);
		return m.matches();
	}
}