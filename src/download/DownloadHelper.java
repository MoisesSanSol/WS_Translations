package download;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
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
		
		downloadHelper.downloadImages_WsTcg_Products();
		
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
	
	public void downloadImages_LittleAkiba_LegacyPromos(String laSetId, String folderPath) throws Exception{
		
		System.out.println("** Download All Set Images from Little Akiba");
		System.out.println("** Set Id: " + laSetId);
		
		String setUrl = this.conf.littleAkibaSetBaseUrl + laSetId;
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		//System.out.println("** Doc: " + doc.html());
		
		Elements bloques = doc.select("h6");
		
		for(Element bloque : bloques){
			
			System.out.println("** Bloque: " + bloque.text());
			if(bloque.text().contains("Specials/Promo")){
				Element ul = bloque.nextElementSibling();
			
				while(ul != null && ul.tagName().equals("ul")){
					
					Elements cards = ul.select("a");
					
					for(Element card : cards){
						
						System.out.println("** Card Href: " + card.attr("href"));
						
						Document cardDoc = Jsoup.connect(card.attr("href")).maxBodySize(0).validateTLSCertificates(false).get();
						
						Element imageLarge = cardDoc.select("a.fullview").first();
						String imageLargeUrl = imageLarge.attr("href");
						
						String cardFullId = cardDoc.select("div.details").first().select("small").first().text();
	
						String cardId = cardFullId.split(" ")[0].replace("/", "_");
						
						String[] cardIdParts = cardFullId.split(" ");
						if(cardIdParts[1].equals("SP")){cardId = cardId + "SP";}
						
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
	
	public void downloadImages_WsTcg_Products() throws Exception{
		
		System.out.println("** Download ws-tcg Product Images");
		
		String setUrl = "https://ws-tcg.com/products/page/";
		
		String imageDirPath = this.conf.getStaticWebFolderPath() + "ProductImages\\";
		File imageDir = new File(imageDirPath);
		Utilities.checkFolderExistence(imageDir);
		
		String imageRefPath = imageDirPath + "imageReferences.txt";
		File imageRef = new File(imageRefPath);
		String newImageRefPath = imageDirPath + "imageReferencesNew.txt";
		File newImageRef = new File(newImageRefPath);
		
		LinkedHashMap<String,String[]> references = new LinkedHashMap<String,String[]>();
				
		ArrayList<String> imageRefContent = new ArrayList<String>(Files.readAllLines(imageRef.toPath(), StandardCharsets.UTF_8));
		
		for(String reference : imageRefContent){
			if(!reference.isEmpty()){
				String[] data = reference.split("\t");
				references.put(data[0], data);
			}
		}

		ArrayList<String> imgReferences = new ArrayList<String>();
		
		for(int i = 2; i <= 2; i++){
		
			System.out.println("** Parsing Page: " + setUrl + i);
			
			Document doc = Jsoup.connect(setUrl + i).maxBodySize(0).get();
			
			Elements imageContainers = doc.select("ul.product-list > li");
	
			for(Element imageContainer : imageContainers){
				
				String referencia = "";
				
				Element set = imageContainer.select("h4").first();
				referencia = referencia + set.text() + "\t";
				
				Element releaseDate = imageContainer.select("p.release").first();
				referencia = referencia + releaseDate.text() + "\t";

				Element image = imageContainer.select("img").first();
				String imgUrl = image.attr("src");
				String imgName = FilenameUtils.getName(imgUrl);
				referencia = referencia + imgName;
				
				String date = releaseDate.text().replaceAll("\\(.+", "");
				DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
				
				Date when = new Date();
				try{
					when = formatter.parse(date);
				}
				catch(ParseException wrongFormat){
					
				}
				Date today = new Date();
				if(set.text().contains("トライアルデッキ") || set.text().contains("ブースターパック") || set.text().contains("エクストラブースター") || set.text().contains("エクストラパック")) {
					if(!when.after(today)){
						//System.out.println("* Something: " + releaseDate.text());
						File imageFile = new File(imageDirPath + imgName);
						if(!imageFile.exists()) {
							Thread.sleep(this.politeness);
							DownloadHelper.downloadFile(imgUrl, imageFile);
						}
						referencia = referencia + "\tUseful";
					}
					else {
						referencia = referencia + "\tTooNew";
					}
				}
				else {
					referencia = referencia + "\tOtherShit";
				}
				imgReferences.add(referencia);
			}
			Thread.sleep(this.politeness);
		}

		Files.write(newImageRef.toPath(), imgReferences, StandardCharsets.UTF_8);
	}
}
