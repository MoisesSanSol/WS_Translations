package download;

import java.io.File;
import java.io.FileOutputStream;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import configuration.LocalConf;
import translations.Conf;

public class DownloadHelper {

	LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		DownloadHelper downloadHelper = new DownloadHelper();
		
		downloadHelper.downloadImages_Yuyutei_FillSetGaps("bd2.0");
		
		System.out.println("*** Finished ***");
	}
	
	public DownloadHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public static void downloadAllSetImages_LittleAkiba(String laSetId) throws Exception{
		
		System.out.println("** Download All SetImages Little Akiba");
		System.out.println("** Set Id: " + laSetId);
		
		String setUrl = Conf.littleAkibaSetBaseUrl + laSetId;
		
		File imageDir = new File(Conf.resultsFolder + laSetId);
		if(!imageDir.exists()){imageDir.mkdirs();}
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		
		Elements cards = doc.select("div.card_list").first().select("a");
		

		for(Element card : cards){
			
			System.out.println("** Set Id: " + card.attr("href"));
			
			Document cardDoc = Jsoup.connect(card.attr("href")).maxBodySize(0).validateTLSCertificates(false).get();
			
			Element imageLarge = cardDoc.select("a.fullview").first();
			String imageLargeUrl = imageLarge.attr("href");
			
			String cardFullId = cardDoc.select("div.details").first().select("small").first().text();

			String cardId = cardFullId.split(" ")[0].split("/")[1].toLowerCase().replace("-", "_");

			File imageFile = new File(Conf.resultsFolder + laSetId + "//" + cardId + ".jpg");
			
			Thread.sleep(5000);
			DownloadHelper.downloadFile(imageLargeUrl, imageFile);
		}		
	}
	
	
	public static void downloadAllSetImages_Yuyutei(String set) throws Exception{
		
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
		
		System.out.println("** Download File " + file.getName() + " from " + url);
		
		// Open a URL Stream
		Response resultImageResponse = Jsoup.connect(url).ignoreContentType(true).execute();
		byte[] imageContent = resultImageResponse.bodyAsBytes();
		
		// Output here
		FileOutputStream out = new FileOutputStream(file);
		out.write(imageContent);
		out.close();
		
		System.out.println("* Download File - Done");
	}
	
	
	public void downloadImages_Yuyutei_FillSetGaps(String set) throws Exception{
		
		System.out.println("** Download Yuyutei Images: Fill Set Gaps");
		System.out.println("* Set: " + set);
		
		String setUrl = Conf.yuyuteiSetBaseUrl + set;
		
		String imageDirPath = this.conf.getGeneralResultsFolderPath() + set + "\\images\\";
		File imageDir = new File(imageDirPath);
		if(!imageDir.exists()){throw new Exception("Folder for filling gaps does not exist.");}
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		
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
			
			System.out.println(rarity);
			System.out.println(cardId);
			System.out.println(imgSrc);
			
			String imgThumbUrl = Conf.yuyuteiBaseUrl + imgSrc;
			String imgUrl = imgThumbUrl.replace("90_126", "front");
			
			File currentImageFile = new File(imageDirPath + cardId + ".png");
			if(currentImageFile.exists()){
				System.out.println("Image already exists.");
			}
			else{
				File imageFile = new File(imageDirPath + cardId + ".jpg");
				Thread.sleep(5000);
				DownloadHelper.downloadFile(imgUrl, imageFile);				
			}
		}
	}
}
