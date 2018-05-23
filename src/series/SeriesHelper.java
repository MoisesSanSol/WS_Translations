package series;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import configuration.LocalConf;

public class SeriesHelper {

	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		SeriesHelper.downloadJpProductsReference_All();
		
		System.out.println("*** Finished ***");
	}
	
	public static void downloadJpProductsReference_All() throws Exception{
		
		System.out.println("** Download WsTcg Jp Products Reference");
		
		String productsPagesBaseUrl = "https://ws-tcg.com/products/page/";
		
		LocalConf conf = LocalConf.getInstance();
		
		ArrayList<Series> products = new ArrayList<Series>();
		
		int pageCount = 0;
		boolean keepGoing = true;
		
		while(keepGoing){
			pageCount++;
		
			String productsPageUrl = productsPagesBaseUrl + pageCount;
			
			System.out.println("** Parsing Page: " + productsPageUrl);
			
			try{
				Thread.sleep(conf.politeness);
				Document doc = Jsoup.connect(productsPageUrl).maxBodySize(0).get();

				Elements imageContainers = doc.select("ul.product-list > li");
		
				for(Element imageContainer : imageContainers){

					Series series = new Series();
					
					series.setFileName = "A definir";
					series.relevant = "true";
					
					Element set = imageContainer.select("h4").first();
					
					series.splitJpSetInfo(set.text());
					
					Element releaseDate = imageContainer.select("p.release").first();
					String rawDateLine = releaseDate.text().replaceAll("\\(.+", "");
					DateFormat formatterJp = new SimpleDateFormat("yyyy/MM/dd");
					DateFormat formatterEs = new SimpleDateFormat("dd/MM/yyyy");
					Date jpDate = formatterJp.parse(rawDateLine);
					
					series.setReleaseDate = formatterEs.format(jpDate);
					
					Element image = imageContainer.select("img").first();
					String imgUrl = image.attr("src");
					
					series.setImageUrl = imgUrl;
					series.setImage = FilenameUtils.getName(imgUrl);
					
					products.add(series);
				}
			}
			catch(IOException exc){
				System.out.println("** Page: " + productsPageUrl + " out of range.");
				keepGoing = false;
			}
			
			SeriesDAO.updateJpProductInformation(products);
			products.clear();
		}
	}
}
