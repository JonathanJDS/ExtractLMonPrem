package com.pricer;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.database.OperationOnDBTarget;
import com.file.OperationOnFile;




public class Start {
	static Logger logger = Logger.getLogger(Start.class);
	static Wini ini;
	static OperationOnFile operationOnFile;
	static OperationOnFile operationOnFile_ko;
	static int count=0;
	
	
	public static void main(String[] args)  {
		
/*		try {
			ini = new Wini(new File("preference.ini"));
		} catch (InvalidFileFormatException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			
			System.exit(1);
		} catch (IOException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		}*/

		Initialisation();

		 logger.info("Prepare Installation...");
		 Prepare();
	
	}




	private static void Prepare() {
		operationOnFile = new OperationOnFile("Extract_begin.flag");
		
		operationOnFile.createFlagFile();
		
		
		
		
		ThreadPrepareMigration threadMigration = new ThreadPrepareMigration();
		//threadMigration.setPriority(Thread.NORM_PRIORITY);
		threadMigration.start();
		
		operationOnFile = new OperationOnFile("Extract_ok.flag");
		operationOnFile_ko	= new OperationOnFile("Extract_ko.flag");
		
		
		
	
	while(operationOnFile.isExist()==false) {
		
					
		try {
			
			System.out.println("waiting for Prepare Migration result... ");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		
		if (count>=240 || operationOnFile_ko.isExist()) {
		
			
			threadMigration.stop();
		logger.fatal("Threatment during more than 40 min Or flag ko detected , stopping migration");
		operationOnFile.deleteFile();
		new OperationOnFile("Extract_ko.flag");
		new OperationOnFile("Extract_begin.flag").deleteFile();
		logger.fatal("Prepare Migration Aborted !!!");
		logger.fatal("Exit Application Without Rollback (not necessary");
		//threadRollback.start();
		System.exit(0);
		return;
		}
		
		
		count+=1;
		
	}
	logger.info("!!!!!!! DATA EXPORT SUCCESSFUL !!!!!!\r\n");


	}

	private static void Initialisation() {
		

		new OperationOnFile("Extract_begin.flag").purgeResultFolder();
		new OperationOnFile("Extract_begin.flag").deleteFile();
		new OperationOnFile("Extract_ok.flag").deleteFile();
		new OperationOnFile("Extract_ko.flag").deleteFile();
		
		
		new OperationOnFile("Installation_begin.flag").deleteFile();
		new OperationOnFile("Installation_ok.flag").deleteFile();
		new OperationOnFile("Installation_ko.flag").deleteFile();
		
		
		new OperationOnFile("Update_begin.flag").deleteFile();
		new OperationOnFile("Update_ok.flag").deleteFile();
		new OperationOnFile("Update_ko.flag").deleteFile();
		
		
		
	}	
		
		
		

	
	

}
