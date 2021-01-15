package com.pricer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.database.OperationOnDBSource;
import com.file.OperationOnFile;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

/**
 *
 * @author mohamed.derraz
 */
public class ThreadInstallation extends Thread{
	protected volatile boolean running = true;
	static Logger logger = Logger.getLogger(Start.class);
	static Wini ini;
	
	//ThreadRollbackInstallation threadRollback = new ThreadRollbackInstallation();
	
	
	public ThreadInstallation(){
    
   
	}
	
	public void run() 
	{

		 while(running) { 
	//	threadRollback.setPriority(Thread.MIN_PRIORITY);
		

			try {
				ini = new Wini(new File("preference.ini"));
			} catch (InvalidFileFormatException e1) {
				
				e1.printStackTrace();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}

			

			logger.info("Starting Application InstallationCRF Version 1.0.2");
			String disk = ini.get("SERVER", "DISK_NAME", String.class);
			double AVAILABLE_DISK_SPACE_MIN = ini.get("Server", "AVAILABLE_DISK_SPACE_MIN", double.class);
			String pricerFolder = ini.get("SERVER", "PRICER_FOLDER", String.class);
			String PACKAGE_SOURCE_FOLDER_NAME = ini.get("SERVER", "PACKAGE_SOURCE_FOLDER_NAME", String.class);
			String PRICER_DATABASE_FOLDER = ini.get("SERVER", "PRICER_DATABASE_FOLDER",String.class);
			String PRICER_PFI_FOLDER = ini.get("SERVER", "PRICER_PFI_FOLDER",String.class);
			
			//String FolderPricer = ini.get("SERVER", "PRICER_FOLDER");
			//String FolderPricerDatabase = ini.get("SERVER", "PRICER_PFI_FOLDER");
			//String FolderPricerPFIFolder= ini.get("SERVER", "PRICER_DATABASE_FOLDER");
			
			String currentDir = System.getProperty("user.dir");

			logger.info("DISK = " + disk);
			logger.info("PRICER_FOLDER = " + pricerFolder);

			// getting hardware machin info

			DisplayMachinInfo();

			/* Checking Available Disk Size */
			logger.info("Checking Available Disk Space...");
			File f = new File(disk);
			double availableSpaceDisk = f.getFreeSpace() / 1000000000.00;
			logger.info("Available Disk Space on " + disk + " = " + availableSpaceDisk + "  Gb");

			if (availableSpaceDisk < AVAILABLE_DISK_SPACE_MIN) {

				logger.fatal("insufficient disk space need at least 10 Gb fo Installation,  exit Programm");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;

			}

			
			
			
			
			UninstallAllServices();
			
			
			List<String> lstFolderNames = new ArrayList<String>();

			
			
			lstFolderNames.add(pricerFolder);
			lstFolderNames.add(PRICER_DATABASE_FOLDER);
			lstFolderNames.add(PRICER_PFI_FOLDER);
			File folderSource;
			File folderdestination;
			boolean renameFolderOk = false;
			
			
			
			for (String folderName : lstFolderNames) {

				logger.info("Trying to rename folder name : " + folderName);
				folderSource = new File(folderName);
				folderdestination = new File(folderName + "_ORG");

				if (folderSource.exists() == false) {

					logger.fatal("source Folder doesn't exist, Installation Aborted !!!");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					running=false;

				}

				

				try {
					logger.info("waiting 30 seconds ...");
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				renameFolderOk = folderSource.renameTo(folderdestination);

				
				try {
					logger.info("waiting 2 seconds ...");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				
				
				if (folderdestination.exists() == false) {

					logger.fatal("unable to rename source to destination Folder " + folderdestination.getPath() +" , Installation Aborted !!!");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					
					logger.warn("complete Roolback, Before Exit");
					
					running = false;

				}
				
				if (renameFolderOk == false) {

					logger.fatal("unable to rename source folder : " + folderName + " into " + folderName + "_ORG");
					logger.fatal("Installation Aborted !!!");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					logger.warn("complete Roolback, Before Exit");
					
					running=false;
				}

				logger.info("folder name : " + folderName + " was renamed successfully ...");

			}
				
				
			try {
				logger.info("waiting 5 seconds ...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			
			logger.info("Checking if Pricer Folder Exist...");
			
			File fPricer = new File(pricerFolder);

			if (fPricer.exists()) {
				logger.fatal(pricerFolder
						+ " already exist, Stopping installation please delete or check this folder before installation !!!");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();

				running=false;

			}
			
			
			logger.info("Checking if PRICER DATABASE FOLDER Folder Exist...");
			
			File fPricerDatabaseFolder = new File(PRICER_DATABASE_FOLDER);

			if (fPricerDatabaseFolder.exists()) {
				logger.fatal(fPricerDatabaseFolder
						+ " already exist, Stopping installation please delete or check this folder before installation !!!");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();

				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;

			}
			
			
			
			logger.info("Checking if PRICER PFI FOLDER Folder Exist : " + PRICER_PFI_FOLDER);
			
			File fPricerPFIFolder = new File(PRICER_PFI_FOLDER);

			if (fPricerPFIFolder.exists()) {
				logger.fatal(fPricerPFIFolder
						+ " already exist, Stopping installation please delete or check this folder before installation !!!");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();

				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;

			}
			

			boolean bCopyFile = false;
			boolean bUnzipFile = false;

			logger.info("Copying  " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Applicat.zip To " + disk);
			bCopyFile = copyFile(currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Applicat.zip", disk + "Applicat.zip");
			if (!bCopyFile) {
				logger.fatal(
						"Unable to copy " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Applicat.zip To " + disk);
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;

			}
			logger.info(
					"copy " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Applicat.zip To " + disk + " successfully");

			logger.info("Copying  " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Databases.zip To " + disk);
			bCopyFile = copyFile(currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Databases.zip", disk + "/Databases.zip");
			if (!bCopyFile) {
				logger.fatal("Unable to copy " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Databases.zip To " + disk
						+ "Databases.zip");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}
			logger.info("copy " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/Databases.zip To " + disk
					+ " successfully");

			logger.info("Unzip  Applicat.zip To " + disk);
			bUnzipFile = Unzip(disk + "Applicat.zip", disk);
			if (!bUnzipFile) {
				logger.fatal("Unable to Unzip " + disk + "/Applicat.zip To " + disk);
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();

				running=false;
			}
			logger.info("Unzip Applicat.zip successfully");

			logger.info("Unzip  Database.zip To " + disk);
			bUnzipFile = Unzip(disk + "Databases.zip", disk);
			if (!bUnzipFile) {
				logger.fatal("Unable to Unzip " + disk + "/Databases.zip To " + disk);
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();

				running=false;
			}
			logger.info("Unzip Databases.zip successfully");

			
			
			
			logger.info("Checking mysql service...");
			
			if (GetServiceStatus("pricermysql").equalsIgnoreCase("unknown") == false) {

				logger.fatal("Mysql Service is already installed !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}

			
			logger.info("Mysql Service not installed , ok...");
			
			if (GetServiceStatus("pricerserver").equalsIgnoreCase("unknown") == false) {

				logger.fatal("Pricer Service is already installed !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();

				running=false;
			}

			
			logger.info("Checking tomcat service...");
			
			if (GetServiceStatus("pricertomcat").equalsIgnoreCase("unknown") == false) {

				logger.fatal("Tomcat Service is already installed !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();

				running=false;
			}

			logger.info("tomcat service not installed, ok ...");
			
			if (GetServiceStatus("pricerflashschedulerservice").equalsIgnoreCase("unknown") == false) {

				logger.fatal("Flashscheduler Service is already installed !!!! ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}

			logger.info("Executing set_env.bat");
			String scriptEnvFile = currentDir + "/set_env.bat";
			ExecuteBatchServiceInstallation(scriptEnvFile);
			logger.info("Installation ok environment variables ");

			
			String fileNameMysqlServiceBatchInstallation = PRICER_DATABASE_FOLDER + "\\Mysql\\bin\\initMySQLService.bat";
			//String fileNameMysqlServiceBatchInstallation = disk + "Databases\\Mysql\\Pricer\\bin\\initMySQLService.bat";

			logger.info("installation of PricerMysql Service");
			ExecuteBatchServiceInstallation(fileNameMysqlServiceBatchInstallation);

			// waiting 5 seconds
			logger.info("waiting 30 seconds...");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}

			
			logger.info("checking if mysql service is installed");
			
			if (GetServiceStatus("pricermysql").equalsIgnoreCase("unknown") == true) {

				logger.fatal("Mysql Service is not installed correctly !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}

			else {
				logger.info("PricerMysql service is installed ");
			}

			String status = "unknown";

			logger.info("checking if PricerMysql service is started correctly..");

			for (int count = 1; count <= 5; count++) {
				status = GetServiceStatus("pricermysql");
				logger.info("PricerMysql service status = " + status);
				if (status.equalsIgnoreCase("Running") == true) {
					logger.info("pricermysql service is started");
					break;

				}

				else {
					try {

						logger.warn("waiting 30 seconds tentative " + count + "/5");
						logger.info("PricerMysql service status = " + status);
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

			}

			System.out.println("on continue");
			System.out.println("on check à nouveau si le service pricermysql est stoppé, on essaie de le démarrer ");

			if (status.equalsIgnoreCase("Stopped") == true) {
				logger.fatal("Trying to start service : pricermysql");
				StartService("pricermysql");

				for (int count = 1; count <= 5; count++) {
					status = GetServiceStatus("pricermysql");
					logger.info("PricerMysql service status = " + status);
					if (status.equalsIgnoreCase("running") == true) {
						logger.info("pricermysql service is started");
						break;

					}

					else {
						try {

							logger.warn("waiting 30 seconds tentative " + count + "/5");
							logger.info("PricerMysql service status = " + status);
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}

					}

				}

			}

			if (status.equalsIgnoreCase("Stopped") == true) {
				logger.fatal("Unable to start pricermysql Installation Aborted !!! ");
				
				logger.warn("complete Roolback, Before Exit");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				//threadRollback.start();
				running = false;

			}

			System.out.println("on continue");

			

			String fileNamePricerServiceBatchInstallation = disk
					+ "Applicat\\Prog\\Pricer\\R3Server\\Service\\PricerServerInstall.bat";

			logger.info("installation of PricerServer Service");
			ExecuteBatchServiceInstallation(fileNamePricerServiceBatchInstallation);

			
			logger.info("waiting 30 seconds...");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}

	

			logger.info("checking if PricerServer service is installed");

			if (GetServiceStatus("PricerServer").equalsIgnoreCase("unknown")) {

				logger.fatal("PricerService Service is not installed correctly !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}

			else {
				logger.info("PricerService service is installed ");
			}

			status = "unknown";

			
			
			System.out.println("on continue");

			

			String fileNamePricerFlashSchedulerServiceBatchInstallation = disk
					+ "Applicat\\Prog\\Pricer\\PricerFlashSchedulerService\\service\\bat\\installService.bat";

			logger.info("installation of PricerFlashScheduler Service");
			ExecuteBatchServiceInstallation(fileNamePricerFlashSchedulerServiceBatchInstallation);

			// waiting 5 seconds
			logger.info("waiting 30 seconds...");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
				
				e1.printStackTrace();
			}

			

			logger.info("checking if PricerFlashScheduler service is installed");

			String pricerFlashSchedulerServiceStatus = GetServiceStatus("PricerFlashSchedulerService");
			if (pricerFlashSchedulerServiceStatus.equalsIgnoreCase("unknown")) {

				logger.fatal("PricerFlashScheduler Service is not installed correctly !!!!  ,  installation aborted ");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;
			}

			else {
				logger.info("PricerFlashScheduler service is installed ");
			}

			status = "unknown";

			logger.info("checking if PricerFlashScheduler service is started correctly..");

			for (int count = 1; count <= 5; count++) {
				status = GetServiceStatus("PricerFlashSchedulerService");
				System.out.println("status = " + status);
				logger.debug("status = " + status);
				if (status.equalsIgnoreCase("running") == true) {
					logger.info("PricerFlashScheduler service is started");
					break;

				}

				else {
					try {

						logger.warn("waiting 30 seconds tentative " + count + "/5");
						logger.info("PricerFlashScheduler service status = " + status);
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

			}

			System.out.println(
					"on check à nouveau si le service PricerFlashSchedulerService est stoppé, on essaie de le démarrer ");

			if (status.equalsIgnoreCase("Stopped") == true) {
				logger.fatal("Trying to start service : PricerFlashSchedulerService");
				StartService("PricerFlashSchedulerService");

				for (int count = 1; count <= 5; count++) {
					status = GetServiceStatus("PricerFlashSchedulerService");
					System.out.println("status = " + status);
					logger.debug("status = " + status);
					if (status.equalsIgnoreCase("running") == true) {
						logger.info("PricerFlashScheduler service is started");
						break;

					}

					else {
						try {

							logger.warn("waiting 30 seconds tentative " + count + "/5");
							logger.info("PricerFlashScheduler service status = " + status);
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}

					}

				}

			}

			if (status.equalsIgnoreCase("Stopped") == true) {
				logger.fatal("Unable to start PricerFlashSchedulerService Installation Aborted !!! ");
				
				logger.warn("complete Roolback, Before Exit");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				//threadRollback.start();
				running=false;

			}

			System.out.println("on continue");

			

			boolean bCopyFonts = copyFiles(new File(currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME + "/CRF_Fonts"),
					new File("C:\\Windows\\Fonts\\"));
			if (bCopyFonts == false) {
				logger.fatal("Unable to copy Fonts files from " + currentDir + "/" + PACKAGE_SOURCE_FOLDER_NAME
						+ "/CRF_Fonts " + " To  C:\\Windows\\Fonts\\");
				logger.fatal("Installation aborted");
				new OperationOnFile("Installation_begin.flag").deleteFile();
				new OperationOnFile("Installation_ko.flag").createFlagFile();
				logger.warn("complete Roolback, Before Exit");
				//threadRollback.start();
				running=false;

			}

			
			String typeStoreInstallation = GetTypeStoreInstallation();

			switch (typeStoreInstallation) {

			
			
			
			
			case "MARKET":
				logger.info("Installation for Store ***MARKET***");

				try {
					OperationOnDBSource db = new OperationOnDBSource();
					db.UpdateSystemParameterMessageFilePath();
				}

				catch (Exception ex) {

					logger.fatal("Error During updating Database Table 'systemparameter',  Installation aborted !!!");
					logger.warn("complete Roolback, Before Exit");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					//threadRollback.start();
					running=false;
				}

				logger.info("Installation of Adapter pfi");
				String fileNamePricerAdapterPFI = disk + "Applicat\\Prog\\Pricer\\AdapterPFI\\Install_Adapter_PFI.bat";

				logger.info("installation of Adapter PFI Service");
				ExecuteBatchServiceInstallation(fileNamePricerAdapterPFI);

				// waiting 5 seconds
				logger.info("waiting 5 seconds...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					
					e1.printStackTrace();
				}

				

				logger.info("checking if AdapterPFI service is installed");
				
				String PricerAdapterPFIserviceStatus = GetServiceStatus("PricerAdapterPFI");

				if (PricerAdapterPFIserviceStatus.equalsIgnoreCase("unknown")) {

					logger.fatal("PricerAdapterPFI Service is not installed correctly !!!!  ,  installation aborted ");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					logger.warn("complete Roolback, Before Exit");
					//threadRollback.start();
					running=false;
				}

				else {
					logger.info("PricerAdapterPFI service is installed ");
				}

				status = "unknown";

				logger.info("checking if PricerAdapterPFI service is started correctly..");

				for (int count = 1; count <= 5; count++) {
					status = GetServiceStatus("PricerAdapterPFI");
					System.out.println("status = " + status);
					logger.debug("status = " + status);
					if (status.equalsIgnoreCase("running") == true) {
						logger.info("PricerAdapterPFI service is started");
						break;

					}

					else {
						try {

							logger.warn("waiting 30 seconds tentative " + count + "/5");
							logger.info("PricerAdapterPFI service status = " + status);
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}

					}

				}

				System.out.println("on continue");

				if (status.equalsIgnoreCase("running") == false) {
					logger.fatal("Unable to start PricerAdapterPFI service, Installation aborted !!!");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					logger.warn("complete Roolback, Before Exit");
					//threadRollback.start();
					running=false;

				}

				
				// demarrage du service pricerServer pour la génération du uuid
					
				
				
				  System.out.println("demarrage du service pricer ");
				  
				  
				  StartService("PricerServer");
				  logger.info("activating licence key, waiting 60 seconds before restart ");
				  try {
					  Thread.sleep(60000);
					  }
				  catch (InterruptedException e1) {
					e1.printStackTrace();
				  }
				
				  
				  logger.info("waiting 10 seconds..");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
				if (status.equalsIgnoreCase("Started") == true) {
					logger.info("Trying to stop service : PricerServer");
					StopService("PricerServer");

					for (int count = 1; count <= 5; count++) {
						status = GetServiceStatus("PricerServer");
						System.out.println("status = " + status);
						logger.info("status of pricer server = " + status);
						if (status.equalsIgnoreCase("stopped") == true) {
							logger.info("PricerServer service is stopped");
							break;

						}

						else {
							try {

								logger.warn("waiting 30 seconds tentative " + count + "/5");
								logger.info("PricerServer service status = " + status);
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}

						}

					}

				}

				
				System.out.println("on continue");
				
				
				
				//vidage du repertoire temp de r3server
				
		
				
				
				String tempFolder = pricerFolder + "\\R3Server\\temp"		;
				logger.info("Purge temp folder : " + tempFolder );
				deleteFolder(tempFolder);
		
			
				
				logger.info("update database system parameter for Market");
				String request1 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('CHAIN_UUID','282bc199-e4f4-11e9-a55e-0a967bc786ea','2019-10-08 00:00:01');";
				String request2 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('storeName','Carrefour Market','2019-10-08 00:00:01');";
				String request3 = "REPLACE  INTO `license`(`ID`,`LICENSE_KEY`,`INSTALLATION_DATE`) VALUES ('1','J1boZGJbN+pJrqDfgAEuFHui8+iFwbAIIqM1dT2uJJ+svAFOs6RmI9EOxxsknQna2QCkWahMdKKQaOAg8ldkrxyhvrBE9ByBbvxcW6laFzYIHxkkK84nUTbd1VLj+rNgDHG/cQ+C8xo338p9akK8az0ZcRN5IZd7v7rrUVY20BfhUjoO9LBpaadKCa0wooI/iW3TlK4Ja3xK0rqmrlVlBF51f3dHNSEB/URYjtLLVY5We/6pbTH00prrxT+u9PPYXm5aqnBU4CUz1V22TrzONa4gYjeGYUbskwsekvNsv5rY8p5RC149M1QJwhn5PkjdYLkH3MhqumOJOUHCWJ3XygwyhVF8wN21sjr49sW2SGM/Cxd6ri0roLCPdKQCuy/7jKD4MTEOrIGrg0YgDvqkk1XAy6mOglvI/dBtYjunZU9WNatmHOM4GGY/FQPWaSn/oOL4iOVldUPtYELj2eKzueaMWz9w96hgaocybRR3n7G5G56SQYjNQcD8QlXBZ/aMPxpGeBVku0xsWGCOfgA7PWdZljniW1R4DQ8a1h4mu7iTzQBAh89mQVJceMD4CLeE2u2kCGQ2QoTp4f+gpE470jUHZz0j+wrtJhnBSho+uIVWNNUG2sTaM5o2A23q1LBmdWU4gg1wj0HzqnefyOAQ2KzIGOYsqOq/s+kI169RtcVvzs4iRdfP8rt6ImhbwlqNw4icMOX6/qLpUkENMh+DeIz8kd54e6qAItV4i4YVz9EwncGN32oL8W0vNdlUuOkiR8UAkvyBMvIpBzP6FDMGupUFNpJVgTYcavfJ1wqw3bm56rrJ43MNQW7W/WEJBqYETaaAkWPYjbnsDEItlIqsN3oWZmMbRu1V0U7gEXVDW4I8V30cVPKn4aOUa04O5+9Vkbu7znFyMA3LGQ5P07NrObtylk18qyFyK0c9SxSdGYw3MgpdsvPNPcMxPFY5rG3uJ/eHtiMIz9xa4JIxH0vlrehoefrOLbkTY36ZScByj+th1WQItNCsjBvgSNy6z4S+Eyn5KfhEToB+jD0hvjnJKqDNno7UAKrcx4GyGR/28NJKRuNh0Eyfuj6Po7p9Rh0vu565K6jbi6kySVCJkPWv2kVSR200wi1k4SoSHHiS7ahKOCj9n4jw6Z5hx8ZpkMwxeTVgs2zw/8nlUxApxqvzpgu4gWLlkSC4rbpkzNJsCdwSIF6KBWXwJNyALTw+ygHyDBlY5yfFrgMQZpQqpZRHvJsui1w1SofK+T0NWe9a1v0=','2019-10-08 00:00:01');";
				String request4 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('DEFAULT_RESULT_FILE_PATH','D:\\\\Applicat\\\\data\\\\Pricer\\\\PFIFiles\\\\ResultFile.r7','2019-10-08 00:00:01');";
				String request5 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('MESSAGE_FILE_PATH','D:\\\\Applicat\\\\data\\\\Pricer\\\\PFIFiles\\\\MessageFiles\\\\NEWPFI','2019-10-08 00:00:01');";
				
				
				List<String> lstRequests = new ArrayList<String>();
				lstRequests.add(request1);
				lstRequests.add(request2);
				lstRequests.add(request3);
				lstRequests.add(request4);
				lstRequests.add(request5);
				

				OperationOnDBSource db;

				for (String requete : lstRequests) {
					db = new OperationOnDBSource();

					if (db.ExecuteRequestUpdate(requete) == false) {

						logger.fatal("Unable to Execute request for Market store, Installation aborted !!! : " + requete);
						new OperationOnFile("Installation_begin.flag").deleteFile();
						new OperationOnFile("Installation_ko.flag").createFlagFile();
						
						logger.warn("complete Roolback, Before Exit");
						//threadRollback.start();
						running=false;
					}

					System.out.println("on continue");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

				System.out.println("Arret du service pricer ");

				if (status.equalsIgnoreCase("Started") == true) {
					logger.info("Trying to stop service : PricerServer");
					StopService("PricerServer");

					for (int count = 1; count <= 5; count++) {
						status = GetServiceStatus("PricerServer");
						System.out.println("status = " + status);
						logger.debug("status = " + status);
						if (status.equalsIgnoreCase("stopped") == true) {
							logger.info("PricerServer service is stopped");
							break;

						}

						else {
							try {

								logger.warn("waiting 30 seconds tentative " + count + "/5");
								logger.info("PricerServer service status = " + status);
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}

						}

					}

				}

				System.out.println("on continue");
				System.out.println("démarrage du service pricerserver ");

				status="Stopped";
				
				if (status.equalsIgnoreCase("Stopped") == true) {
					logger.info("Trying to start service : PricerServer");
					StartService("PricerServer");

					for (int count = 1; count <= 5; count++) {
						status = GetServiceStatus("PricerServer");
						System.out.println("status = " + status);
						logger.debug("status = " + status);
						if (status.equalsIgnoreCase("running") == true) {
							logger.info("PricerServer service is started");
							break;

						}

						else {
							try {

								logger.warn("waiting 30 seconds tentative " + count + "/5");
								logger.info("PricerServer service status = " + status);
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}

						}

					}

				}

				if (status.equalsIgnoreCase("Stopped") == true) {
					logger.fatal("Unable to start PricerServer Installation Aborted !!! ");
					
					logger.warn("complete Roolback, Before Exit");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					//threadRollback.start();
					running=false;

				}

				System.out.println("on continue");

				break;

			
			
			
			case "HYPER":
				
				logger.info("Migration for Store ***HYPER***");
				logger.info("update database system parameter for HYPER");
			

				
				// start pricer server and wait 60sec for uuid generation .
				
							
				  System.out.println("demarrage du service pricer ");
				logger.info("Starting pricerServer service");  
				  
				  StartService("PricerServer");
				  logger.info("activating licence key, waiting 60 seconds before restart ");
				  try {
					  Thread.sleep(60000);
					  }
				  catch (InterruptedException e1) {
					e1.printStackTrace();
				  }
				 
				
				
				
				if (status.equalsIgnoreCase("Started") == true) {
					logger.info("Trying to stop service : PricerServer");
					StopService("PricerServer");

					for (int count = 1; count <= 5; count++) {
						status = GetServiceStatus("PricerServer");
						System.out.println("status = " + status);
						logger.debug("status = " + status);
						if (status.equalsIgnoreCase("stopped") == true) {
							logger.info("PricerServer service is stopped");
							break;

						}

						else {
							try {

								logger.warn("waiting 30 seconds tentative " + count + "/5");
								logger.info("PricerServer service status = " + status);
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}

						}

					}

				}

				System.out.println("on continue");
				
				
				
				//vidage du repertoire temp de r3server
				
		
				
				
				String tempFolderHyper = pricerFolder + "\\R3Server\\temp"		;
				logger.info("Purge temp folder : " + tempFolderHyper );
				deleteFolder(tempFolderHyper);
		
			

				
				
				
				
				
				
				String request6 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('CHAIN_UUID','282bc199-e4f4-11e9-a55e-0a967bc786ea','2019-10-08 00:00:01');";
				String request7 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('storeName','Carrefour Hyper','2019-10-08 00:00:01');";
				String request8 = "REPLACE  INTO `license`(`ID`,`LICENSE_KEY`,`INSTALLATION_DATE`) VALUES ('1','J1boZGJbN+pJrqDfgAEuFHui8+iFwbAIIqM1dT2uJJ+svAFOs6RmI9EOxxsknQna2QCkWahMdKKQaOAg8ldkrxyhvrBE9ByBbvxcW6laFzYIHxkkK84nUTbd1VLj+rNgDHG/cQ+C8xo338p9akK8az0ZcRN5IZd7v7rrUVY20BfhUjoO9LBpaadKCa0wooI/iW3TlK4Ja3xK0rqmrlVlBF51f3dHNSEB/URYjtLLVY5We/6pbTH00prrxT+u9PPYXm5aqnBU4CUz1V22TrzONa4gYjeGYUbskwsekvNsv5rY8p5RC149M1QJwhn5PkjdYLkH3MhqumOJOUHCWJ3XygwyhVF8wN21sjr49sW2SGM/Cxd6ri0roLCPdKQCuy/7jKD4MTEOrIGrg0YgDvqkk1XAy6mOglvI/dBtYjunZU9WNatmHOM4GGY/FQPWaSn/oOL4iOVldUPtYELj2eKzueaMWz9w96hgaocybRR3n7G5G56SQYjNQcD8QlXBZ/aMPxpGeBVku0xsWGCOfgA7PWdZljniW1R4DQ8a1h4mu7iTzQBAh89mQVJceMD4CLeE2u2kCGQ2QoTp4f+gpE470jUHZz0j+wrtJhnBSho+uIVWNNUG2sTaM5o2A23q1LBmdWU4gg1wj0HzqnefyOAQ2KzIGOYsqOq/s+kI169RtcVvzs4iRdfP8rt6ImhbwlqNw4icMOX6/qLpUkENMh+DeIz8kd54e6qAItV4i4YVz9EwncGN32oL8W0vNdlUuOkiR8UAkvyBMvIpBzP6FDMGupUFNpJVgTYcavfJ1wqw3bm56rrJ43MNQW7W/WEJBqYETaaAkWPYjbnsDEItlIqsN3oWZmMbRu1V0U7gEXVDW4I8V30cVPKn4aOUa04O5+9Vkbu7znFyMA3LGQ5P07NrObtylk18qyFyK0c9SxSdGYw3MgpdsvPNPcMxPFY5rG3uJ/eHtiMIz9xa4JIxH0vlrehoefrOLbkTY36ZScByj+th1WQItNCsjBvgSNy6z4S+Eyn5KfhEToB+jD0hvjnJKqDNno7UAKrcx4GyGR/28NJKRuNh0Eyfuj6Po7p9Rh0vu565K6jbi6kySVCJkPWv2kVSR200wi1k4SoSHHiS7ahKOCj9n4jw6Z5hx8ZpkMwxeTVgs2zw/8nlUxApxqvzpgu4gWLlkSC4rbpkzNJsCdwSIF6KBWXwJNyALTw+ygHyDBlY5yfFrgMQZpQqpZRHvJsui1w1SofK+T0NWe9a1v0=','2019-10-08 00:00:01');";
				String request9 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('DEFAULT_RESULT_FILE_PATH','D:\\\\Applicat\\\\data\\\\Pricer\\\\PFIFiles\\\\ResultFile.r7','2019-10-08 00:00:01');";
				String request10 = "REPLACE  INTO `systemparameter`(`KEY_NAME`,`VALUE`,`LASTUPDATEDTIME`) VALUES ('MESSAGE_FILE_PATH','D:\\\\Applicat\\\\data\\\\Pricer\\\\PFIFiles\\\\MessageFiles','2019-10-08 00:00:01');";
				
				
				
				
				List<String> lstRequests2 = new ArrayList<String>();
				lstRequests2.add(request6);
				lstRequests2.add(request7);
				lstRequests2.add(request8);
				lstRequests2.add(request9);
				lstRequests2.add(request10);

					for (String requete : lstRequests2) {
					db = new OperationOnDBSource();

					if (db.ExecuteRequestUpdate(requete) == false) {

						logger.fatal("Unable to Execute request for Hyper store, Installation aborted !!! : " + requete);
						new OperationOnFile("Installation_begin.flag").deleteFile();
						new OperationOnFile("Installation_ko.flag").createFlagFile();
						logger.warn("complete Roolback, Before Exit");
						//threadRollback.start();
						running=false;
					}

					System.out.println("on continue");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}
				
				
				
				System.out.println("on continue");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

			

				System.out.println("on continue");
				System.out.println("démarrage du service pricerserver ");
				
				status="Stopped";
				
				if (status.equalsIgnoreCase("Stopped") == true) {
					logger.fatal("Trying to start service : PricerServer");
					StartService("PricerServer");

					for (int count = 1; count <= 5; count++) {
						status = GetServiceStatus("PricerServer");
						System.out.println("status = " + status);
						logger.debug("status = " + status);
						if (status.equalsIgnoreCase("running") == true) {
							logger.info("PricerServer service is started");
							break;

						}

						else {
							try {

								logger.warn("waiting 30 seconds tentative " + count + "/5");
								logger.info("PricerServer service status = " + status);
								Thread.sleep(30000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}

						}

					}

				}

				if (status.equalsIgnoreCase("Stopped") == true) {
					logger.fatal("Unable to start PricerServer Installation Aborted !!! ");
					
					logger.warn("complete Roolback, Before Exit");
					new OperationOnFile("Installation_begin.flag").deleteFile();
					new OperationOnFile("Installation_ko.flag").createFlagFile();
					//threadRollback.start();
					running=false;

				}

				System.out.println("on continue");

				break;

			default:

				logger.info("Unknown Store Type ");
				break;
			}

			
	
			
			logger.info("Installation successfull");
			new OperationOnFile("Installation_begin.flag").deleteFile();
			new OperationOnFile("Installation_ok.flag").createFlagFile();
		
			running=false;
			
	}
	
}
			
	
	
	public static boolean copyFiles(File sourceLocation, File targetLocation) {

		
		
		
		if (sourceLocation.isDirectory()) {

			File[] files = sourceLocation.listFiles();

			InputStream in = null;
			OutputStream out = null;

			for (File file : files) {

				try {
					in = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
					logger.warn(e.getCause() + " : " + e.getMessage());
					return false;
				}

				try {
					out = new FileOutputStream(targetLocation + "/" + file.getName());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					logger.warn(e.getCause() + " : " + e.getMessage());
					e.printStackTrace();
					return false;
				}

				// Copy the bits from input stream to output stream
				byte[] buf = new byte[1024];
				int len;
				try {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.warn(e.getCause() + " : " + e.getMessage());
					e.printStackTrace();
					return false;
				}
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.warn(e.getCause() + " : " + e.getMessage());
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					
					logger.warn(e.getCause() + " : " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return true;

	}

	public static boolean StartService(String serviceName) {

		boolean result = false;

		try {
			logger.info("Starting service " + serviceName);
			Process p = Runtime.getRuntime().exec("sc start " + serviceName);

		} catch (IOException ex) {
			logger.warn(
					"Unable to Start service : " + serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}

	public static boolean StopService(String serviceName) {

		boolean result = false;

		try {
			logger.info("Stopping service " + serviceName);
			Process p = Runtime.getRuntime().exec("sc stop " + serviceName);

		} catch (IOException ex) {
			logger.warn(
					"Unable to Stop service : " + serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}

	private static void ExecuteBatchServiceInstallation(String filename) {

		logger.info("batch installation begin : " + filename);

		try {

			logger.debug("executing " + filename);
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(filename);

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {

				logger.debug(line);
			}

			int exitVal = process.waitFor();

			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

	}

	private static String GetServiceStatus(String serviceName) {

		try {

			Process p = Runtime.getRuntime().exec("sc query " + serviceName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();

			while (line != null) {

				if (line.trim().startsWith("STATE")) {

					if (line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim()
							.equals("1")) { // System.out.println("Stopped");

						return "Stopped";
					}

					else if (line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim()
							.equals("2")) {
						// System.out.println("Starting....");

						return "Starting";
						// return 2;
					}

					else if (line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim()
							.equals("3")) {
						// System.out.println("Stopping....");

						return "Stopping";
						// return 3;
					}

					else if (line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim()
							.equals("4")) {
						// System.out.println("Running");

						return "Running";
						// return 4;

					}

					else

						return "unknown";
					// return -1;
				}
				line = reader.readLine();
			}

			return "unknown";
			// return -1;
		} catch (IOException ioe) {

			return "unknown";
			// return -1;
		} catch (Exception e) {

			return "unknown";
			// return -1;
		}
	}

	
	private static boolean copyFile(String source, String destination) {

		InputStream inStream = null;
		OutputStream outStream = null;

		try {

			File afile = new File(source);
			File bfile = new File(destination);
			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = inStream.read(buf)) > 0) {
				outStream.write(buf, 0, bytesRead);

			}

		} catch (IOException e) {
e.printStackTrace();
			return false;

		}
		
		catch (Exception ex) {
			
			ex.printStackTrace();
		}

		finally {
			try {
				inStream.close();
			} catch (IOException e) {
				logger.warn("Unable to close Stream of file");
				e.printStackTrace();
			}
			try {
				outStream.close();
			} catch (IOException e) {
				logger.warn("Unable to close Stream of file");
				e.printStackTrace();
			}
		}

		return true;

	}

	private static boolean Unzip(String filename, String zipPath) {

		File srcFile = new File(filename);

		// create a directory with the same name to which the contents will be extracted
		// String zipPath = filename.substring(0, filename.length()-4);
		// File temp = new File(zipPath);
		// temp.mkdir();

		ZipFile zipFile = null;

		try {

			zipFile = new ZipFile(srcFile);

			// get an enumeration of the ZIP file entries
			Enumeration<?> e = zipFile.entries();

			while (e.hasMoreElements()) {

				ZipEntry entry = (ZipEntry) e.nextElement();

				File destinationPath = new File(zipPath, entry.getName());

				// create parent directories
				destinationPath.getParentFile().mkdirs();

				// if the entry is a file extract it
				if (entry.isDirectory()) {
					continue;
				} else {

					System.out.println("Extracting file: " + destinationPath);

					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					FileOutputStream fos = new FileOutputStream(destinationPath);

					BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					bos.close();
					bis.close();

				}

			}

		} catch (IOException ioe) {
			System.out.println("Error opening zip file" + ioe);
			return false;
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException ioe) {

				System.out.println("Error while closing zip file" + ioe);
				return false;
			}
		}
		return true;
	}

	/**
	 * Unzip it
	 * 
	 * @param zipFile input zip file
	 * @param output  zip file output folder
	 */
	public static boolean unZipIt(String zipFile, String outputFolder) {

		byte[] buffer = new byte[1024];

		try {

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		} catch (IOException ex) {
			ex.printStackTrace();
			return false;

		}

		return true;

	}

	private static String GetTypeStoreInstallation() {

		String typeStoreInstallation = "UNKNOWN";
		String computerName = null;
		String COMPUTERNAME_MARKET = ini.get("SERVER", "COMPUTERNAME_MARKET", String.class);
		String COMPUTERNAME_HYPER = ini.get("SERVER", "COMPUTERNAME_HYPER", String.class);

		try {
			computerName = Inet4Address.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		
			e.printStackTrace();
		}

		// System.out.println(computerName.substring(0,COMPUTERNAME_MARKET.trim().length()));

		if (computerName.substring(0, COMPUTERNAME_MARKET.trim().length())
				.equalsIgnoreCase(COMPUTERNAME_MARKET.trim())) {

			typeStoreInstallation = "MARKET";

		}

		else if (computerName.substring(0, COMPUTERNAME_HYPER.trim().length()).equalsIgnoreCase(COMPUTERNAME_HYPER)) {

			typeStoreInstallation = "HYPER";

		}

		return typeStoreInstallation;

	}

	private static void DisplayMachinInfo() {
		
		String computerName;
		DecimalFormat df = new DecimalFormat("#.##");
		SystemInfo si = new SystemInfo();
		OperatingSystem os = si.getOperatingSystem();
		HardwareAbstractionLayer hardware = si.getHardware();

		logger.info("Operation Systeme : " + os);
		logger.info("os family : " + os.getFamily());
		logger.info("Manufacturer : " + os.getManufacturer());
		logger.info("process count : " + os.getProcessCount());

		logger.info("memoryInGb : " + df.format((float) ((hardware.getMemory().getTotal()) / 1073741824)));
		logger.info("AvailablememorymemoryInGb : "
				+ df.format((float) ((hardware.getMemory().getAvailable()) / 1073741824)));

		try {
			computerName = Inet4Address.getLocalHost().getHostName();
			logger.info("Computer Name = " + computerName);
			logger.info("user name = " + System.getProperty("user.name"));
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		}

	}
	
	
	
	
	private static void deleteFolder(String directoryName)  {
		 
		Path directory = Paths.get(directoryName);
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
 
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
 
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc)
						throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	
	

	private void UninstallAllServices() {
		List<String> lstServiceNames = new ArrayList<String>();
		lstServiceNames.add("PricerServer");
		lstServiceNames.add("PricerAdapterPFI");
		lstServiceNames.add("PricerFlashSchedulerService");
		lstServiceNames.add("PricerMysql");

		logger.info("Stopping All Services  ...");

		for (String serviceName : lstServiceNames) {

			Service service = new Service(serviceName);

			if (service.GetServiceStatus().equalsIgnoreCase("Running")) {

				logger.info("Trying to Stop service :" + serviceName);

				service.StopService();
				int i = 1;
				while (service.GetServiceStatus().equalsIgnoreCase("Stopped") == false && i <= 5) {

					logger.warn("waiting 30 seconds tentative " + i++ + "/5");
					
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}

				if (service.GetServiceStatus().equalsIgnoreCase("stopped") == false) {

					logger.warn("unable to stop service : " + serviceName);
					logger.warn("continue...");
				}

				else {
					logger.info("service : " + serviceName + " is Stopped...");

				}

			}

			if (service.GetServiceStatus().equalsIgnoreCase("Stopped")) {

				logger.info("service: " + serviceName + " is already stopped, Trying to Uninstall it...");

				service.UninstallService();
				int i = 1;
				while (service.GetServiceStatus().equalsIgnoreCase("Unknown") == false && i <= 5) {

					logger.warn("waiting 30 seconds tentative " + i++ + "/5");
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}

				if (service.GetServiceStatus().equalsIgnoreCase("Unknown") == false) {

					logger.fatal("unable to Uninstall service : " + serviceName);
					logger.warn("continue anyway");
				}

				else {
					logger.info("service : " + serviceName + " is Uninstalled Successfully ...");

				}

			}

		}
		
	}
	

}



    	



    			
    			
    			
    			
    			
    			
    	
		








 

