package staticweb;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import configuration.LocalConf;
import utilities.Utilities;

public class ImagesHelper {
	
	public LocalConf conf;
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		// For testing and individual execution purposes.
		ImagesHelper imagesHelper = new ImagesHelper();
		//imagesHelper.mergeDownloadAndCotdImages();
		File targetImage = new File(LocalConf.getInstance().getGeneralResultsFolderPath() + "images_cotd//w56_001.png");
		imagesHelper.formatImage(targetImage, targetImage);
		System.out.println("*** Finished ***");
	}
	
	public ImagesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void mergeDownloadAndCotdImages() throws Exception{
		
		System.out.println("** Merge Download and Cotd Images");
		
		File yuyuteiFolder = new File(this.conf.getGeneralResultsFolderPath() + "temp");
		File mergedFolder = new File(this.conf.getGeneralResultsFolderPath() + "merged");
		Utilities.checkFolderExistence(mergedFolder);
		
		for(File yuyuteiImage : yuyuteiFolder.listFiles()) {
			String name = yuyuteiImage.getName();
			System.out.println("* Yuyutei Image: " + name);
			File targetImage = new File(this.conf.getGeneralResultsFolderPath() + "merged//" + name);
			String cotdEquivalent = name.replaceAll(".+?_(.+?)-(.+?)\\.png", "$1_$2.png").toLowerCase();
			System.out.println("* Cotd Equivalent: " + cotdEquivalent);
			File cotdImage = new File(this.conf.getGeneralResultsFolderPath() + "images\\" + cotdEquivalent);
			if(cotdImage.exists()) {
				System.out.println("* Cotd Equivalent Exist");
				FileUtils.copyFile(cotdImage, targetImage);
			}
			else {
				System.out.println("* No Cotd Equivalent");
				FileUtils.copyFile(yuyuteiImage, targetImage);
			}
		}
	}
	
	public void mergeDownloadAndCotdImages_LittleAkiba() throws Exception{
		
		System.out.println("** Merge Download and Cotd Images");
		
		File cotdFolder = new File(this.conf.getGeneralResultsFolderPath() + "images_cotd");
		File laFolder = new File(this.conf.getGeneralResultsFolderPath() + "images_la");
		File mergedFolder = new File(this.conf.getGeneralResultsFolderPath() + "images_merged");
		Utilities.checkFolderExistence(mergedFolder);
		
		for(File yuyuteiImage : laFolder.listFiles()) {
			String fileName = yuyuteiImage.getName();
			System.out.println("* LA Image: " + fileName);
			String name = FilenameUtils.removeExtension(fileName);
			
			File targetImage = new File(this.conf.getGeneralResultsFolderPath() + "merged//" + name);
			
			String cotdEquivalent = name.replaceAll(".+?_(.+?)-(.+?)\\.png", "$1_$2.png").toLowerCase();
			System.out.println("* Cotd Equivalent: " + cotdEquivalent);
			File cotdImage = new File(this.conf.getGeneralResultsFolderPath() + "images\\" + cotdEquivalent);
			if(cotdImage.exists()) {
				System.out.println("* Cotd Equivalent Exist");
				FileUtils.copyFile(cotdImage, targetImage);
			}
			else {
				System.out.println("* No Cotd Equivalent");
				FileUtils.copyFile(yuyuteiImage, targetImage);
			}
		}
	}
	
	public void formatImage(File origin, File target) throws Exception{
		//BufferedImage bi = new BufferedImage(350, 489, BufferedImage.);
		BufferedImage bufferedImage = ImageIO.read(origin);
		System.out.println(bufferedImage.getType());
		System.out.println(BufferedImage.TYPE_3BYTE_BGR);
	}
}
