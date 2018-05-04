package utilities;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import configuration.FileConf;
import configuration.LocalConf;

public class FileUtilities {

	
	public static void saveGenericFile(ArrayList<String> content, String fileReferenceName) throws Exception{
		
		String fileFieldName = fileReferenceName + "File";
		String fileFieldValue = null;
		
		for(Field field : FileConf.class.getDeclaredFields()){
			if(field.getName().equals(fileFieldName)){
				fileFieldValue = (String)field.get(FileConf.class);
			}
		}
		
		String fileFolderFieldName = fileReferenceName + "Folder";
		String fileFolderFieldValue = null;
		
		for(Field field : FileConf.class.getDeclaredFields()){
			if(field.getName().equals(fileFolderFieldName)){
				fileFolderFieldValue = (String)field.get(FileConf.class);
			}
		}
		
		File fileFolder = null;
		
		for(Field field : LocalConf.class.getDeclaredFields()){
			if(field.getName().equals(fileFolderFieldValue)){
				fileFolder = (File)field.get(LocalConf.getInstance());
			}
		}
		
		String filePath = fileFolder.getPath() + "\\" + fileFieldValue;
		File file = new File(filePath);
		
		Files.write(file.toPath(), content, StandardCharsets.UTF_8);
	}
	
}
