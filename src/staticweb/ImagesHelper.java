package staticweb;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

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

		System.out.println("*** Finished ***");
	}
	
	public ImagesHelper() throws Exception{
		this.conf = LocalConf.getInstance();
	}
	
	public void mergeDownloadAndCotdImages_LittleAkiba(String setActualId) throws Exception{
		
		System.out.println("** Merge Download and Cotd Images");
		
		String cotdFolderPath = this.conf.getGeneralResultsFolderPath() + "images_cotd//";
		File cotdFolder = new File(cotdFolderPath);
		String laFolderPath = this.conf.getGeneralResultsFolderPath() + "images_la//";
		File laFolder = new File(laFolderPath);
		String mergedFolderPath = this.conf.getGeneralResultsFolderPath() + "images//";
		File mergedFolder = new File(mergedFolderPath);
		Utilities.checkFolderExistence(mergedFolder);
		
		ArrayList<String> cotdImages = Utilities.getFileNames(cotdFolder);
		
		for(File laImageFile : laFolder.listFiles()) {
			String laImageName = FilenameUtils.removeExtension(laImageFile.getName());
			String imageName = this.upperRaritySuffix(laImageName);
			System.out.println("* Checking Image: " + imageName);
			String actualImageName = imageName.replaceFirst(".+?_", setActualId + "-"); 
			File targetImageFile = new File(mergedFolderPath + actualImageName + ".png");
			if(!cotdImages.contains(imageName)){
				this.createWebFormatImage(laImageFile);
			}
			else{
				File cotdImageFile = new File(cotdFolderPath + imageName + ".png");
				FileUtils.copyFile(cotdImageFile, targetImageFile);
			}
		}
	}
	
	public String upperRaritySuffix(String cardId) throws Exception{
		return cardId.replace("sp","SP").replace("r","R").replace("s","S");
	}
	
	public void renameCotdImagesToWebFormat(String setFileId) throws Exception{
		
		System.out.println("** Rename Cotd Images to Web Format");
		
		String imgFolderPath = this.conf.getStaticWebFolderPath() + setFileId + "//images//";
		File imgFolder = new File(imgFolderPath);
		
		for(File imageFile : imgFolder.listFiles()){
			String imageName = imageFile.getName();
			if(!imageName.startsWith(setFileId)){
				String actualImageName = imageName.replaceFirst(".+?_", setFileId + "-");
				Path source = imageFile.toPath();
				Files.move(source, source.resolveSibling(actualImageName));
			}
		}
	}
	
	public static void createWebFormatImage(File originFile) throws Exception{
		BufferedImage originBi = ImageIO.read(originFile);
		int width = 350;
		int height = 489;
		Image tmp = originBi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage targetBi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = targetBi.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        File targetFile = new File(originFile.getAbsolutePath().replace(".jpg", ".png"));
		ImageIO.write(targetBi, "png", targetFile);
	}
	
	public void rotateWebFormatImage(File targetFile) throws Exception{
		BufferedImage originBi = ImageIO.read(targetFile);
		int width = originBi.getWidth();
	    int height = originBi.getHeight();
	    
	    if(height > width){
	    
	    	System.out.println("* Rotating Image: " + targetFile.getName());
		    BufferedImage targetBi = new BufferedImage(height, width, originBi.getType());
		 
		    for(int i = 0; i < width; i++){
		        for(int j = 0; j < height; j++){
		        	targetBi.setRGB(j, width-1-i, originBi.getRGB(i,j));
		        }
		    }
			ImageIO.write(targetBi, "png", targetFile);
	    }
	}
}
