package translations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

public class ExcelHelper {

	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		ExcelHelper.test();
		
		System.out.println("*** Finished ***");
	}
	
	public static void test() throws Exception{
		
        /*La ruta donde se creará el archivo*/
        String filePath = Conf.defaultFolder + "//testing.xls";
        /*Se crea el objeto de tipo File con la ruta del archivo*/
        File archivoXLS = new File(filePath);
        /*Si el archivo existe se elimina*/
        if(archivoXLS.exists()) archivoXLS.delete();
        /*Se crea el archivo*/
        archivoXLS.createNewFile();
        
        /*Se crea el libro de excel usando el objeto de tipo Workbook*/
        Workbook libro = new HSSFWorkbook();
        /*Se inicializa el flujo de datos con el archivo xls*/
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        
        /*Utilizamos la clase Sheet para crear una nueva hoja de trabajo dentro del libro que creamos anteriormente*/
        Sheet hoja = libro.createSheet("Mi hoja de trabajo 1");
        Sheet hoja2 = libro.createSheet("Mi hoja de trabajo 2");
        
        /*Hacemos un ciclo para inicializar los valores de 10 filas de celdas*/
        for(int f=0;f<10;f++){
            /*La clase Row nos permitirá crear las filas*/
            Row fila = hoja.createRow(f);
            
            /*Cada fila tendrá 5 celdas de datos*/
            for(int c=0;c<5;c++){
                /*Creamos la celda a partir de la fila actual*/
                Cell celda = fila.createCell(c);
                
                /*Si la fila es la número 0, estableceremos los encabezados*/
                if(f==0){
                    celda.setCellValue("Encabezado #"+c);
                }else{
                    /*Si no es la primera fila establecemos un valor*/
                    celda.setCellValue("Valor celda "+c+","+f);
                }
            }
        }
        
        // read the image to the stream
        FileInputStream inputStream = new FileInputStream(Conf.defaultFolder + "//bloque.jpg");
        byte[] bytes = IOUtils.toByteArray(inputStream);
        
        CreationHelper helper = libro.getCreationHelper();
        Drawing drawing = hoja2.createDrawingPatriarch();

        ClientAnchor anchor = helper.createClientAnchor();

        int pictureIndex = libro.addPicture( bytes, Workbook.PICTURE_TYPE_PNG );


        anchor.setCol1( 0 );
        anchor.setRow1( 0 ); // same row is okay
        anchor.setRow2( 5 );
        anchor.setCol2( 5 );
        Picture pict = drawing.createPicture( anchor, pictureIndex );
        //pict.resize();
        
        
        /*Escribimos en el libro*/
        libro.write(archivo);
        /*Cerramos el flujo de datos*/
        archivo.close();
        libro.close();
	}
	
	public static void createCollaboratorExcel(ArrayList<Card> cards, String fileName) throws Exception{
		
		Translator translator = new Translator();
		
        String filePath = Conf.resultsFolder + "//" + fileName + ".xls";
        File archivoXLS = new File(filePath);
        if(archivoXLS.exists()) archivoXLS.delete();
        archivoXLS.createNewFile();
        
        Workbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        
        Sheet hoja = libro.createSheet("Cartas");
        //Sheet hoja2 = libro.createSheet("Mi hoja de trabajo 2");
        
        CellStyle styleGreen = libro.createCellStyle();
        styleGreen.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        styleGreen.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        CellStyle styleOrange = libro.createCellStyle();
        styleOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleOrange.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        CellStyle styleRed = libro.createCellStyle();
        styleRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleRed.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        int count = 0; 
        for(Card card : cards){

        	int initialCount = count;
        	
        	Row filaNombre = hoja.createRow(count++);
        	Row filaId = hoja.createRow(count++);
        	Row filaTrait1 = hoja.createRow(count++);
        	Row filaTrait2 = hoja.createRow(count++);
        	Row filaHab = hoja.createRow(count++);
            
        	Cell celda;
        	celda = filaNombre.createCell(3);
        	celda.setCellValue("Nombre: ");
        	celda = filaNombre.createCell(4);
        	celda.setCellValue(card.name);
        	celda = filaId.createCell(3);
        	celda.setCellValue("Id: ");
        	celda = filaId.createCell(4);
        	celda.setCellValue(card.id);
        	celda = filaTrait1.createCell(3);
        	celda.setCellValue("Trait 1: ");
        	celda = filaTrait1.createCell(4);
        	celda.setCellValue(card.trait1);
        	celda = filaTrait2.createCell(3);
        	celda.setCellValue("Trait 2: ");
        	celda = filaTrait2.createCell(4);
        	celda.setCellValue(card.trait2);
        	Cell celdaHabs = filaHab.createCell(3);
        	celdaHabs.setCellValue("Habilidades: ");
        	
        	int habCount = 0;
        	for(String hab : card.habs){
        		Row filaHab1 = hoja.createRow(count + habCount++);
        		Row filaHab2 = hoja.createRow(count + habCount++);
        		Cell celdaOri = filaHab1.createCell(3);
        		celdaOri.setCellValue("Original: ");
        		celda = filaHab1.createCell(4);
        		celda.setCellValue(hab);
            	Cell celdaTrad = filaHab2.createCell(3);
            	celdaTrad.setCellValue("Traducida: ");
            	
            	String transAttempt = translator.translateAbilityPrettyOutput(hab);
            	if(!hab.equals(transAttempt)){
            		celda = filaHab2.createCell(4);
                	celda.setCellValue(transAttempt);
                	celdaOri.setCellStyle(styleGreen);
                	celdaTrad.setCellStyle(styleGreen);
            	}
            	else{
            		celdaOri.setCellStyle(styleOrange);
                	celdaTrad.setCellStyle(styleOrange);
            	}
        	}

        	if(habCount > 11){
        		count = count + habCount + 1;
        	}
        	else{
        		count = count + 11;
        	}
	        // read the image to the stream
	        FileInputStream inputStream = new FileInputStream(Conf.defaultFolder + "//imgs//" + card.id.replace("/", "_") + ".jpg");
	        byte[] bytes = IOUtils.toByteArray(inputStream);
	        
	        CreationHelper helper = libro.getCreationHelper();
	        Drawing drawing = hoja.createDrawingPatriarch();
	
	        ClientAnchor anchor = helper.createClientAnchor();
	
	        int pictureIndex = libro.addPicture( bytes, Workbook.PICTURE_TYPE_PNG );
	
	
	        anchor.setCol1(0);
	        anchor.setCol2(3);
	        
	        anchor.setRow1(initialCount);
	        anchor.setRow2(initialCount + 15);
	        
	        Picture pict = drawing.createPicture( anchor, pictureIndex );
	        //pict.resize();

        }
        
        hoja.autoSizeColumn(3);
        hoja.autoSizeColumn(4);
	        
        libro.write(archivo);

        archivo.close();
        libro.close();
		
	}
	
	public static void createCollaboratorExcel_EB(ArrayList<Card> cards, String fileName) throws Exception{
		
		Translator translator = new Translator();
		
        String filePath = Conf.resultsFolder + "//" + fileName + ".xls";
        File archivoXLS = new File(filePath);
        if(archivoXLS.exists()) archivoXLS.delete();
        archivoXLS.createNewFile();
        
        Workbook libro = new HSSFWorkbook();
        FileOutputStream archivo = new FileOutputStream(archivoXLS);
        
        Sheet hoja = libro.createSheet("Cartas");
        //Sheet hoja2 = libro.createSheet("Mi hoja de trabajo 2");
        
        CellStyle styleGreen = libro.createCellStyle();
        styleGreen.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        styleGreen.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        CellStyle styleOrange = libro.createCellStyle();
        styleOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleOrange.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        CellStyle styleRed = libro.createCellStyle();
        styleRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleRed.setFillPattern(CellStyle.SOLID_FOREGROUND);
        
        int count = 0; 
        for(Card card : cards){

        	int initialCount = count;
        	
        	Row filaNombre = hoja.createRow(count++);
        	Row filaId = hoja.createRow(count++);
        	Row filaTrait1 = hoja.createRow(count++);
        	Row filaTrait2 = hoja.createRow(count++);
        	Row filaHab = hoja.createRow(count++);
            
        	Cell celda;
        	celda = filaNombre.createCell(6);
        	celda.setCellValue("Nombre: ");
        	celda = filaNombre.createCell(7);
        	celda.setCellValue(card.name);
        	celda = filaId.createCell(6);
        	celda.setCellValue("Id: ");
        	celda = filaId.createCell(7);
        	celda.setCellValue(card.id);
        	celda = filaTrait1.createCell(6);
        	celda.setCellValue("Trait 1: ");
        	celda = filaTrait1.createCell(7);
        	celda.setCellValue(card.trait1);
        	celda = filaTrait2.createCell(6);
        	celda.setCellValue("Trait 2: ");
        	celda = filaTrait2.createCell(7);
        	celda.setCellValue(card.trait2);
        	Cell celdaHabs = filaHab.createCell(6);
        	celdaHabs.setCellValue("Habilidades: ");
        	
        	int habCount = 0;
        	for(String hab : card.habs){
        		Row filaHab1 = hoja.createRow(count + habCount++);
        		Row filaHab2 = hoja.createRow(count + habCount++);
        		Cell celdaOri = filaHab1.createCell(6);
        		celdaOri.setCellValue("Original: ");
        		celda = filaHab1.createCell(7);
        		celda.setCellValue(hab);
            	Cell celdaTrad = filaHab2.createCell(6);
            	celdaTrad.setCellValue("Traducida: ");
            	
            	String transAttempt = translator.translateAbilityPrettyOutput(hab);
            	if(!hab.equals(transAttempt)){
            		celda = filaHab2.createCell(7);
                	celda.setCellValue(transAttempt);
                	celdaOri.setCellStyle(styleGreen);
                	celdaTrad.setCellStyle(styleGreen);
            	}
            	else{
            		celdaOri.setCellStyle(styleOrange);
                	celdaTrad.setCellStyle(styleOrange);
            	}
        	}

        	if(habCount > 11){
        		count = count + habCount + 1;
        	}
        	else{
        		count = count + 11;
        	}
        	
	        // Standard Image
	        FileInputStream inputStream = new FileInputStream(Conf.defaultFolder + "//imgs//" + card.id.replace("/", "_") + ".jpg");
	        byte[] bytes = IOUtils.toByteArray(inputStream);
	        inputStream.close();
	        
	        CreationHelper helper = libro.getCreationHelper();
	        Drawing drawing = hoja.createDrawingPatriarch();
	
	        ClientAnchor anchor = helper.createClientAnchor();
	
	        int pictureIndex = libro.addPicture( bytes, Workbook.PICTURE_TYPE_PNG );
	
	
	        anchor.setCol1(0);
	        anchor.setCol2(3);
	        
	        anchor.setRow1(initialCount);
	        anchor.setRow2(initialCount + 15);
	        
	        Picture pict = drawing.createPicture( anchor, pictureIndex );
	        //pict.resize();
	        
	        // Foil Image
	        FileInputStream inputStream2 = new FileInputStream(Conf.defaultFolder + "//imgs//" + card.id.replace("/", "_") + "-S.jpg");
	        byte[] bytes2 = IOUtils.toByteArray(inputStream2);
	        inputStream2.close();
	        
	        CreationHelper helper2 = libro.getCreationHelper();
	        Drawing drawing2 = hoja.createDrawingPatriarch();
	
	        ClientAnchor anchor2 = helper2.createClientAnchor();
	
	        int pictureIndex2 = libro.addPicture(bytes2, Workbook.PICTURE_TYPE_PNG);
	
	
	        anchor2.setCol1(3);
	        anchor2.setCol2(6);
	        
	        anchor2.setRow1(initialCount);
	        anchor2.setRow2(initialCount + 15);
	        
	        Picture pict2 = drawing2.createPicture(anchor2, pictureIndex2);
        }
        
        hoja.autoSizeColumn(6);
        hoja.autoSizeColumn(7);
	        
        libro.write(archivo);

        archivo.close();
        libro.close();
		
	}
}
