package translations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		Dispatcher.rawAbilityMasterList();
		
		System.out.println("*** Finished ***");
	}
}
