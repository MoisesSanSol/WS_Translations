package download;

import java.io.File;
import java.io.FileOutputStream;

import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import configuration.LocalConf;
import staticweb.ImagesHelper;
import utilities.Utilities;

public class DownloadHelper {

	LocalConf conf;
	int politeness = 1000;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		DownloadHelper downloadHelper = new DownloadHelper();
		
		//downloadHelper.downloadImages_Yuyutei_FullSet("fsubw2.0");
		//downloadHelper.downloadImages_LittleAkiba_Test("123");
		//downloadHelper.downloadImages_Yuyutei_SetGaps("ppext");
		
		System.out.println("*** Finished ***");
	}
	
	public DownloadHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void downloadImages_LittleAkiba_FullSet(String laSetId, String folderPath) throws Exception{
		
		System.out.println("** Download All Set Images from Little Akiba");
		System.out.println("** Set Id: " + laSetId);
		
		String setUrl = this.conf.littleAkibaSetBaseUrl + laSetId;
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		//System.out.println("** Doc: " + doc.html());
		
		Elements cards = doc.select("div.card_list").first().select("a");
		
		for(Element card : cards){
			
			//System.out.println("** Card Url: " + card.attr("href"));
			
			Document cardDoc = Jsoup.connect(card.attr("href")).maxBodySize(0).validateTLSCertificates(false).get();
			
			Element imageLarge = cardDoc.select("a.fullview").first();
			String imageLargeUrl = imageLarge.attr("href");
			
			String cardFullId = cardDoc.select("div.details").first().select("small").first().text();

			String cardId = cardFullId.split(" ")[0].replace("/", "_");

			File laImageFile = new File(folderPath + cardId + ".jpg");
			
			Thread.sleep(this.politeness);
			DownloadHelper.downloadFile(imageLargeUrl, laImageFile);
			ImagesHelper.createWebFormatImage(laImageFile);
			laImageFile.delete();
		}		
	}
	
	public void downloadImages_LittleAkiba_SetGaps(String laSetId, String folderPath) throws Exception{
		
		System.out.println("** Download Images from Little Akiba: Fill Set Gaps");
		System.out.println("** Set Id: " + laSetId);
		
		String setUrl = this.conf.littleAkibaSetBaseUrl + laSetId;
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		//System.out.println("** Doc: " + doc.html());
		
		Elements bloques = doc.select("h6");
		
		boolean ebFoils = false;
		
		for(Element bloque : bloques){
			
			System.out.println("** Bloque: " + bloque.text());
			if(bloque.text().contains("Foil/Parallel")){
				ebFoils = true;
			}
			else{
				ebFoils = false;
			}
			
			Element ul = bloque.nextElementSibling();
			
			while(ul != null && ul.tagName().equals("ul")){
				
				Elements cards = ul.select("a");
		
				for(Element card : cards){
					
					//System.out.println("** Card Url: " + card.attr("href"));
					
					Document cardDoc = Jsoup.connect(card.attr("href")).maxBodySize(0).validateTLSCertificates(false).get();
					
					Element imageLarge = cardDoc.select("a.fullview").first();
					String imageLargeUrl = imageLarge.attr("href");
					
					String cardFullId = cardDoc.select("div.details").first().select("small").first().text();
		
					String cardId = cardFullId.split(" ")[0].replace("/", "_");
					if(ebFoils){cardId = cardId + "-S";}
					
					File laImageFile = new File(folderPath + cardId + ".jpg");
					
					File currentImageFile = new File(folderPath + cardId + ".png");
					if(currentImageFile.exists()){
						System.out.println("Image already exists: " + cardId);
					}
					else{
						File imageFile = new File(folderPath + cardId + ".jpg");
						Thread.sleep(this.politeness);
						DownloadHelper.downloadFile(imageLargeUrl, laImageFile);	
						ImagesHelper.createWebFormatImage(imageFile);
						imageFile.delete();
					}
				}
				
				ul = ul.nextElementSibling();
			}
		}
	}
	
	public void downloadImages_LittleAkiba_Test(String laSetId) throws Exception{
		
		System.out.println("** Download All Set Images from Little Akiba");
		System.out.println("** Set Id: " + laSetId);
		
		String setUrl = this.conf.littleAkibaSetBaseUrl + laSetId;
		
		File imageDir = new File(this.conf.getGeneralResultsFolderPath() + laSetId);
		if(!imageDir.exists()){imageDir.mkdirs();}
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		//System.out.println("** Doc: " + doc.html());
		
		Elements bloques = doc.select("h6");
				
		boolean ebFoils = false;
		
		for(Element bloque : bloques){
			
			System.out.println("** Bloque: " + bloque.text());
			if(bloque.text().contains("Foil/Parallel")){
				ebFoils = true;
			}
			else{
				ebFoils = false;
			}
			
			Element ul = bloque.nextElementSibling();
			
			while(ul != null && ul.tagName().equals("ul")){
				
				Elements cards = ul.select("a");
				
				for(Element card : cards){
					
					System.out.println("** Card Href: " + card.attr("href"));
					
					Document cardDoc = Jsoup.connect(card.attr("href")).maxBodySize(0).validateTLSCertificates(false).get();
					
					Element imageLarge = cardDoc.select("a.fullview").first();
					String imageLargeUrl = imageLarge.attr("href");
					
					String cardFullId = cardDoc.select("div.details").first().select("small").first().text();

					String[] cardIdParts = cardFullId.split(" ");
					
					String cardId = cardIdParts[0].replace("-", "_");
					
					if(ebFoils){cardId = cardId + "-S";}
					if(cardIdParts[1].equals("SP")){cardId = cardId + "SP";}
					
					File imageFile = new File(this.conf.getGeneralResultsFolderPath() + laSetId + "//" + cardId + ".jpg");
					
					Thread.sleep(this.politeness);
					DownloadHelper.downloadFile(imageLargeUrl, imageFile);
				}
				
				ul = ul.nextElementSibling();
			}
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
	
	
	public void downloadImages_Yuyutei_SetGaps(String set, String folderPath) throws Exception{
		
		System.out.println("** Download Yuyutei Images: Fill Set Gaps");
		System.out.println("* Set: " + set);
		
		String setUrl = this.conf.yuyuteiSetBaseUrl + set;
		
		ImagesHelper imagesHelper = new ImagesHelper();
		
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
			
			String imgThumbUrl = this.conf.yuyuteiBaseUrl + imgSrc;
			String imgUrl = imgThumbUrl.replace("90_126", "front");
			
			File currentImageFile = new File(folderPath + cardId + ".png");
			if(currentImageFile.exists()){
				System.out.println("Image already exists: " + cardId);
			}
			else{
				File imageFile = new File(folderPath + cardId + ".jpg");
				Thread.sleep(this.politeness);
				DownloadHelper.downloadFile(imgUrl, imageFile);	
				imagesHelper.createWebFormatImage(imageFile);
				imageFile.delete();
			}
		}
	}
	
	public void downloadImages_Yuyutei_FullSet(String set) throws Exception{
		
		System.out.println("** Download Yuyutei Images: Full Set");
		System.out.println("* Set: " + set);
		
		String setUrl = this.conf.yuyuteiSetBaseUrl + set;
		
		String imageDirPath = this.conf.getGeneralResultsFolderPath() + set + "\\";
		File imageDir = new File(imageDirPath);
		Utilities.checkFolderExistence(imageDir);
		
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
			
			String imgThumbUrl = this.conf.yuyuteiBaseUrl + imgSrc;
			String imgUrl = imgThumbUrl.replace("90_126", "front");
			
			File currentImageFile = new File(imageDirPath + cardId + ".png");
			if(currentImageFile.exists()){
				System.out.println("Image already exists.");
			}
			else{
				File imageFile = new File(imageDirPath + cardId + ".jpg");
				Thread.sleep(this.politeness);
				DownloadHelper.downloadFile(imgUrl, imageFile);
			}
		}
	}
}
