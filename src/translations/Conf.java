package translations;

public class Conf {

	public static String baseFolder = ".\\";
	public static String imagesFolder = Conf.baseFolder + "imagenes\\";
	public static String defaultFolder = "C:\\Users\\Moises BSS\\Desktop\\PruebasTemporales\\";
	public static String resultsFolder = "C:\\Users\\Moises BSS\\Desktop\\PruebasTemporales\\";

	public static String correctionFilesFolder = "D:\\WorkShop\\Translations\\Correcciones\\";
	public static String hotcCleanFilesFolder = "D:\\WorkShop\\Translations\\TranslationPlainFiles\\HotcCleanFiles\\";
	
	
	public static String translationPairsFolder = "D:\\WorkShop\\Translations\\TranslationPairs\\";
	public static String databaseFilesFolder = "D:\\WorkShop\\Translations\\Database";
	
	public static String remoteFilesURL = "http://cotd.esy.es/translations/referencias/";
	public static String yuyuteiBaseUrl = "http://yuyu-tei.jp";
	public static String yuyuteiSetBaseUrl = "http://yuyu-tei.jp/game_ws/sell/sell_price.php?ver=";

	
	public static String[] translationPairsFiles = {"abilityListCont","abilityListAct","abilityListAuto","abilityListAutoCC","abilityListOthers"};

	public static String lineBasedCorrectionFile = "LineCorrections";
	public static String stringReplacementBasedCorrectionFile = "ReplaceCorrections";
	public static String regExpBasedCorrectionFile = "RegExpCorrections";
	public static String removeLinesCorrectionFile = "RemoveLineCorrections";
	
	// HotC Raw Files Conf
	public static String hotcBaseUrl = "http://www.heartofthecards.com/";
	public static String hotcTranslationMainUrl = Conf.hotcBaseUrl + "code/cardlist.html?pagetype=ws";
	public static String hotcTranslationSetBaseUrl = Conf.hotcBaseUrl + "code/cardlist.html?pagetype=ws&cardset=";
	public static String hotcTranslationFileBaseUrl = Conf.hotcBaseUrl + "translations/";
	
	public static String hotcRawFilesFolder = "D:\\WorkShop\\Translations\\TranslationPlainFiles\\HotcRawFiles\\";
	public static String hotcRawFilesReferenceFile = Conf.hotcRawFilesFolder + "reference.properties";
	
	// Little Akiba
	public static String littleAkibaSetBaseUrl = "http://littleakiba.com/tcg/weiss-schwarz/browse.php?series_id=";
}
