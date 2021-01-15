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
		
		try {
			ini = new Wini(new File("preference.ini"));
		} catch (InvalidFileFormatException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			
			System.exit(1);
		} catch (IOException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		}

		Initialisation();

		 logger.info("Prepare Installation...");
		 Prepare();
	
	}




	private static void Prepare() {
		operationOnFile = new OperationOnFile("PrepareMigration_begin.flag");
		
		operationOnFile.createFlagFile();
		
		
		
		
		ThreadPrepareMigration threadMigration = new ThreadPrepareMigration();
		//threadMigration.setPriority(Thread.NORM_PRIORITY);
		threadMigration.start();
		
		operationOnFile = new OperationOnFile("PrepareMigration_ok.flag");
		operationOnFile_ko	= new OperationOnFile("PrepareMigration_ko.flag");					
		
		
		
	
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
		new OperationOnFile("PrepareMigration_ko.flag");
		new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
		logger.fatal("Prepare Migration Aborted !!!");
		logger.fatal("Exit Application Without Rollback (not necessary");
		//threadRollback.start();
		System.exit(0);
		return;
		}
		
		
		count+=1;
		
	}
	

	logger.info("!!!!!!! DATA EXPORT SUCCESSFUL !!!!!!\r\n");
	
	
	
	
	System.out.println("******************************************************************");
	System.out.println("*                                                                *");
	System.out.println("* PLEASE CHECK THAT SERVICES ARE CORRECTLY STOPPED ON THE OLD VM *");
	System.out.println("*                                                                *");
	System.out.println("******************************************************************");
	
	System.out.println("Press any key to continue migration ...");

	try {
		
		System.in.read();


	} catch (Exception e) {
		
		e.printStackTrace();
	}
	
	
	
	
	
	
	
	logger.info("!!!!!!! Beginning IMPORT ON TARGET MACHINE !!!!!!!");
	
	logger.info("Trying to stop service : PricerServer");
	//LocalStopService("PricerServer");
	

	String statusTarget = "";
	String hostTarget = ini.get("MYSQL_TARGET", "HOSTNAME");
	//String winPassword = ini.get("MYSQL_TARGET", "WIN_PASS");
	
	System.out.println("Host Target : "+ hostTarget);
	
	statusTarget = GetServiceStatus("PricerServer",hostTarget);
	
	System.out.println("Status service Pricerserver : " + statusTarget);
	
	
	if (statusTarget.equalsIgnoreCase("Running")) {
		logger.info("Trying to stop service : PricerServer");
		RemoteStopService("PricerServer",hostTarget);

		for (int count = 1; count <= 5; count++) {
			statusTarget = GetServiceStatus("PricerServer",hostTarget);
			System.out.println("status = " + statusTarget);
			logger.info("status of pricer server = " + statusTarget);
			if (statusTarget.equalsIgnoreCase("stopped") == true) {
				logger.info("PricerServer service is stopped");
				break;

			}

			else {
				try {

					logger.warn("waiting 15 seconds tentative " + count + "/3");
					logger.info("PricerServer service status = " + statusTarget);
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

			}

		}

	}
	
	else if (statusTarget.equalsIgnoreCase("unknown")){
		System.out.println("*********************************************************************");
		System.out.println("*                                                                   *");
		System.out.println("*              STATUS SERVICE PRICERSERVER UNKNOWN                  *");
		System.out.println("*                                                                   *");
		System.out.println("*********************************************************************");
		System.out.println("*                                                                   *");
		System.out.println("* PLEASE CHECK THAT PRICERSERVER IS CORRECTLY STOPPED ON THE NEW VM *");
		System.out.println("*                                                                   *");
		System.out.println("*********************************************************************");
		
		System.out.println("Press any key to continue migration ...");
		
		
		try {
			
			System.in.read();


		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	
	
	System.out.println("*********************************************************************");
	System.out.println("*                                                                   *");
	System.out.println("* PLEASE CHECK THAT PRICERSERVER IS CORRECTLY STOPPED ON THE NEW VM *");
	System.out.println("*                                                                   *");
	System.out.println("*********************************************************************");
	
	System.out.println("Press any key to continue migration ...");
	
	
	try {
		
		System.in.read();


	} catch (Exception e) {
		
		e.printStackTrace();
	}
	
	
	
	
	System.out.println("!!!!!! Importing SQL on Database !!!!!!!");
	
	OperationOnDBTarget pricerDB = new OperationOnDBTarget();
	String PRICER_RESULT_FOLDER = ini.get("SERVER","PRICER_RESULT_FOLDER");
	
	
	File dir = new File(PRICER_RESULT_FOLDER);
	String [] extensions = new String [] {"sql"};
	try {
		System.out.println("Getting all .sql files in "+dir.getCanonicalPath());
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
		
		for(File file : files) {
			pricerDB.importData(PRICER_RESULT_FOLDER, file.getName());
		}
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	System.out.println("!!!!!! Starting Service PricerServer ... !!!!!!!");
	
	statusTarget = GetServiceStatus("PricerServer",hostTarget);
	
	System.out.println("Status service Pricerserver : " + statusTarget);
	
	
	if (statusTarget.equalsIgnoreCase("Stopped")) {
		logger.info("Trying to start service : PricerServer");
		StartService("PricerServer",hostTarget);

		for (int count = 1; count <= 3; count++) {
			statusTarget = GetServiceStatus("PricerServer",hostTarget);
			System.out.println("status = " + statusTarget);
			logger.info("status of pricer server = " + statusTarget);
			if (statusTarget.equalsIgnoreCase("Running") == true) {
				logger.info("PricerServer service is Running");
				break;

			}

			else {
				try {

					logger.warn("waiting 15 seconds tentative " + count + "/3");
					logger.info("PricerServer service status = " + statusTarget);
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

			}

		}

	} else {
		System.out.println("Service PricerServer is in a different state than STOPPED ... Please check ... Exiting !!!");
		System.out.println("Press any key to continue migration ...");
		
		
		try {
			
			System.in.read();


		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	System.out.println("!!!! Copying ITEM and Link to DataFiles !!!!");
	System.out.println("!!!! Please move manualy ITEM.m1 to MessageFiles once PricerServer running correctly ! !!!!!");
	System.out.println("Press any key to continue migration ...");
	
	
	try {
		
		System.in.read();


	} catch (Exception e) {
		
		e.printStackTrace();
	}
	
	File fromItemI1 = new File(PRICER_RESULT_FOLDER+"\\item.i1");
	File toI1 = new File("F:\\Pricer\\PFIFiles\\DataFiles\\item.i1");
	File fromItemM1 = new File(PRICER_RESULT_FOLDER+"\\item.m1");
	File toM1 = new File("F:\\Pricer\\PFIFiles\\item.m1");
	
	File fromLinkI1 = new File(PRICER_RESULT_FOLDER+"\\link.i1");
	File toLinkI1 = new File("F:\\Pricer\\PFIFiles\\DataFiles\\link.i1");
	File fromLinkM1 = new File(PRICER_RESULT_FOLDER+"\\link.m1");
	File toLinkM1 = new File("F:\\Pricer\\PFIFiles\\link.m1");
	
	
	try {
		FileUtils.copyFile(fromItemI1, toI1);
		FileUtils.copyFile(fromItemM1, toM1);
		FileUtils.copyFile(fromLinkI1, toLinkI1);
		FileUtils.copyFile(fromLinkM1, toLinkM1);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
	System.out.println("*****************************");
	System.out.println("*                           *");
	System.out.println("*    MIGRATION COMPLETED    *");
	System.out.println("*                           *");
	System.out.println("*****************************");
		
	}


	public static boolean StartService(String serviceName, String hostTarget) {

		boolean result = false;
		String[] script = {"cmd.exe", "/c", "sc", "\\\\"+hostTarget,"start", serviceName};
		System.out.println(script.toString());

		try {
			logger.info("Stopping service " + serviceName);
			Process p = Runtime.getRuntime().exec(script);

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException ex) {
			logger.warn(
					"Unable to Stop service : " + serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}



	
	public static boolean LocalStopService(String serviceName) {

		boolean result = false;
		String[] script = {"cmd.exe", "/c", "sc", "stop", serviceName};

		try {
			logger.info("Stopping service " + serviceName);
			Process p = Runtime.getRuntime().exec(script);

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException ex) {
			logger.warn(
					"Unable to Stop service : " + serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}
	
	public static boolean RemoteStopService(String serviceName, String hostSource) {

		boolean result = false;
		String[] script = {"cmd.exe", "/c", "sc", "\\\\"+hostSource,"stop", serviceName};
		System.out.println(script.toString());

		try {
			logger.info("Stopping service " + serviceName);
			Process p = Runtime.getRuntime().exec(script);

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException ex) {
			logger.warn(
					"Unable to Stop service : " + serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}
	
	private static String GetServiceStatus(String serviceName, String hostName) {

		try {

			Process p = Runtime.getRuntime().exec("sc \\\\"+hostName + " query " + serviceName);
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


	private static void Initialisation() {
		

		new OperationOnFile("PrepareMigration_begin.flag").purgeResultFolder();
		new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
		new OperationOnFile("PrepareMigration_ok.flag").deleteFile();
		new OperationOnFile("PrepareMigration_ko.flag").deleteFile();
		
		
		new OperationOnFile("Installation_begin.flag").deleteFile();
		new OperationOnFile("Installation_ok.flag").deleteFile();
		new OperationOnFile("Installation_ko.flag").deleteFile();
		
		
		new OperationOnFile("Update_begin.flag").deleteFile();
		new OperationOnFile("Update_ok.flag").deleteFile();
		new OperationOnFile("Update_ko.flag").deleteFile();
		
		
		
	}	
		
		
		

	
	

}
