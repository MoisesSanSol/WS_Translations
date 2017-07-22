package translations;

import java.io.File;
import java.io.FileOutputStream;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

public class DownloadHelper {

	public static void getAllSetRawHotcFileNames(){
		
	}
	
	
	public static void downloadAllSetImages_yuyutei(String set) throws Exception{
		
		String setUrl = Conf.yuyuteiSetBaseUrl + set;
		
		File imageDir = new File(Conf.resultsFolder + set);
		if(!imageDir.exists()){imageDir.mkdirs();}
		File imageThumbsDir = new File(Conf.resultsFolder + "//thumbs//" + set);
		if(!imageThumbsDir.exists()){imageThumbsDir.mkdirs();}
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		/*File input = new File("C:\\Users\\Moises BSS\\Desktop\\PruebasTemporales\\test.html");
		Document doc = Jsoup.parse(input, "UTF-8");*/
		//System.out.println(doc.html());
		
		Elements cards = doc.select("[class^=card_unit]");

		for(Element card : cards){
			
			String rarity = card.className().replace("card_unit rarity_", "");
			
			Element name = card.select(".id").first();
			String cardId = name.text().replace("/", "_");

			if(rarity.startsWith("S-")){
				cardId = cardId + "-S";
			}
			
			Element img = card.select("img").first();
			String imgSrc = img.attr("src");
			
			/*System.out.println(rarity);
			System.out.println(cardId);
			System.out.println(imgSrc);*/
			
			String imgThumbUrl = Conf.yuyuteiBaseUrl + imgSrc;
			String imgUrl = imgThumbUrl.replace("90_126", "front");
			
			File imageThumbFile = new File(Conf.resultsFolder + "//thumbs//" + set + "//s_" + cardId + ".jpg");
			File imageFile = new File(Conf.resultsFolder + set + "//" + cardId + ".jpg");
			
			Thread.sleep(5000);
			DownloadHelper.downloadFile(imgThumbUrl, imageThumbFile);
			Thread.sleep(5000);
			DownloadHelper.downloadFile(imgUrl, imageFile);
		}		
	}
	
	public static void downloadFile(String url, File file) throws Exception{
		
		System.out.println("*** Download File " + file.getName() + " from " + url + " ***");
		
		// Open a URL Stream
		Response resultImageResponse = Jsoup.connect(url).ignoreContentType(true).execute();
		byte[] imageContent = resultImageResponse.bodyAsBytes();
		
		// Output here
		FileOutputStream out = new FileOutputStream(file);
		out.write(imageContent);
		out.close();
		
		System.out.println("*** Download File - Done ***");
	}
	
}
