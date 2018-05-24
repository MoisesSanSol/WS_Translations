package series;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import configuration.LocalConf;

public class SeriesDAO {

	
	public static HashMap<String,String> getNeoStandardHotcGroups_ById() throws Exception{

		HashMap<String,String> groups = new HashMap<String,String>();
		
		LocalConf conf = LocalConf.getInstance();
		File hotcGroupsFile = new File(conf.neoStandardHotcGroupsPath);
		
		ArrayList<String> groupsFileContent = new ArrayList<String>(Files.readAllLines(hotcGroupsFile.toPath(), StandardCharsets.UTF_8));
		
		for(String line : groupsFileContent){
			
			String[] splitLine = line.split("\t");
			String[] setIds = splitLine[1].split(", ");
			
			for(String setId : setIds){
				groups.put(setId, splitLine[0]);
			}
		}
		return groups;
	}

	public static HashMap<String,String> getNeoStandardJpGroups_ById() throws Exception{

		HashMap<String,String> groups = new HashMap<String,String>();
		
		LocalConf conf = LocalConf.getInstance();
		File jpGroupsFile = new File(conf.neoStandardJpGroupsPath);
		
		ArrayList<String> groupsFileContent = new ArrayList<String>(Files.readAllLines(jpGroupsFile.toPath(), StandardCharsets.UTF_8));
		
		while(!groupsFileContent.isEmpty()){
			
			String group = groupsFileContent.remove(0);
			String[] setIds = groupsFileContent.remove(0).split(", ");
			
			for(String setId : setIds){
				groups.put(setId, group);
			}
		}
		return groups;
	}
	
	public static void saveJpProductInformation(ArrayList<Series> seriesList) throws Exception{

		ArrayList<String> content = new ArrayList<>();
		
		for(Series series : seriesList){
			
			String jpInfo = series.setJpType + "\t" + series.setJpName + "\t" + series.setImage + "\t" + series.setImageUrl + "\t" + series.setReleaseDate;
			
			content.add(series.setFileName);
			content.add(series.relevant);
			content.add(jpInfo);
		}
		
		LocalConf conf = LocalConf.getInstance();
		File jpProductsFile = new File(conf.productsPath);
		
		Files.write(jpProductsFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static void updateJpProductInformation(ArrayList<Series> seriesList) throws Exception{

		ArrayList<Series> currentSeriesList = SeriesDAO.readJpProductInformation();
		
		for(Series series : seriesList){
			
			boolean found = false;
			
			for(Series currentSeries : currentSeriesList){
				
				if((currentSeries.setJpType + currentSeries.setJpName).equals(series.setJpType + series.setJpName)){
					
					currentSeries.setReleaseDate = series.setReleaseDate;
					currentSeries.setImageUrl = series.setImageUrl;
					currentSeries.setImage = series.setImage;
					found = true;
					
				}
				
			}
			
			if(!found){
				currentSeriesList.add(series);
			}
		}
		
		SeriesDAO.saveJpProductInformation(currentSeriesList);
	}
	
	public static ArrayList<Series> readJpProductInformation() throws Exception{

		ArrayList<Series> seriesList = new ArrayList<Series>();

		LocalConf conf = LocalConf.getInstance();
		File jpProductsFile = new File(conf.productsPath);
		
		ArrayList<String> content = new ArrayList<>(Files.readAllLines(jpProductsFile.toPath(), StandardCharsets.UTF_8));
		
		while(!content.isEmpty()){
			
			Series series = new Series();
			
			series.setFileName = content.remove(0);
			series.relevant = content.remove(0);
			
			String[] jpInfo = content.remove(0).split("\t");
			series.setJpType = jpInfo[0];
			series.setJpName = jpInfo[1];
			series.setImage = jpInfo[2];
			series.setImageUrl = jpInfo[3];
			series.setReleaseDate =  jpInfo[4];
			
			seriesList.add(series);
		}
		
		return seriesList;
	}
	
}
