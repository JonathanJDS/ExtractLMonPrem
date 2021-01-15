package com.pricer;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.database.OperationOnDBSource;
import com.file.OperationOnFile;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

/**
 *
 * @author mohamed.derraz
 */
public class ThreadPrepareMigration implements Runnable{
	
	//protected volatile boolean running = true;
	private Thread worker;
	private final AtomicBoolean running = new AtomicBoolean(false);
	static Logger logger = Logger.getLogger(Start.class);
	static Wini ini;
	private int interval;
	
	   public ThreadPrepareMigration(int sleepInterval) {
	        interval = sleepInterval;
	    }
	//ThreadRollbackInstallation threadRollback = new ThreadRollbackInstallation();
	
	
	public ThreadPrepareMigration() {

	}
	
    public void start() {
        worker = new Thread(this);
        worker.start();
    }
 
    public void stop() {
        running.set(false);
    }
	
	

	public void run() {
		
		running.set(true);
		
		 while(running.get()) {	
		
	//	threadRollback.setPriority(Thread.MIN_PRIORITY);
			 try { 
	                Thread.sleep(interval); 
	            } catch (InterruptedException e){ 
	                Thread.currentThread().interrupt();
	                System.out.println(
	                  "Thread was interrupted, Failed to complete operation");
	            }
		
		logger.info("Starting Application MigrationCastorama V1.0");

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

		String FolderForExtractedData = ini.get("SERVER", "PRICER_RESULT_FOLDER");

		String hostSource = ini.get("MYSQL_SOURCE", "HOSTNAME");
		System.out.println("Folder = " + FolderForExtractedData);

		File Ffolder = new File(FolderForExtractedData);

		if (Ffolder.exists() == false) {

			logger.fatal("unable to get Folder " + FolderForExtractedData + ", Please check configuration !!!");
			logger.fatal("Migration aborted !!!");
			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
			System.exit(1);

		}

		OperationOnDBSource pricerDB = new OperationOnDBSource();
		int ireleaseName = 1;

		try {
			switch (ireleaseName) {
			case 1: // < R5.4.13
				
				logger.info("getting item list...");
				List<Item> lstItem = pricerDB.getItemList("select * from item");

				logger.info("getting subcell List...");
				List<Subcell> lstSubcell = pricerDB.getSubcellListFrom_inf_R5_4_13("select * from subcell");
				

				logger.info("getting subcell TRXMAP List...");
				List<SubcellTRXMap> lstSubcellTRXMap = pricerDB.getSubcellListTRXMAPFrom_inf_R5_4_13(
						"SELECT * FROM subcelltrxmap,`transceiver` WHERE `transceiver`.`TRXID`=`subcelltrxmap`.`SCMTRXIDREF`");
				
				
				logger.info("getting Base Station List...");
				List<BaseStation> lstBaseStation_inf_R5_4_13 = pricerDB.getBaseStationList("select * from basestation");
				
				
				
				logger.info("getting Link Department List...");
				HashMap<String, LinkDepartment> mapLinkDepartment = new HashMap<String, LinkDepartment>();

				LinkDepartment linkDepartment = null;

				for (Subcell subcell : lstSubcell) {

					linkDepartment = new LinkDepartment();
					linkDepartment.setID(subcell.getSUBCELLID());
					linkDepartment.setALIAS(subcell.getSUBCELLALIAS());
					linkDepartment.setIS_BACKOFFICE(subcell.getSUBISBACKOFFICE());

					mapLinkDepartment.put(subcell.getSUBCELLID(), linkDepartment);

				}
				// faire une iteration sur les deux map pour completer celle ci.

				String trxGroup = "";
				String subcellID;

				logger.info("Construction of LinkDepartement ...");
				for (SubcellTRXMap subcellTRXMAP : lstSubcellTRXMap) {

					subcellID = subcellTRXMAP.getSCMSUBCELLID();
					System.out.println("subcellID =" + subcellID);
					System.out.println("check if " + subcellID + " exist in maplinkdepartment");
					if (mapLinkDepartment.containsKey(subcellID)) {

						System.out.println("exist ok )");
						linkDepartment = mapLinkDepartment.get(subcellID);
						trxGroup = linkDepartment.getTRX_GROUP();
						System.out.println("trxGroup =" + trxGroup);

						System.out.println("check if trxGroup==null");
						if (trxGroup == null) {
							System.out.println("is null");
							trxGroup = subcellID.substring(0, 1) + "|" + subcellTRXMAP.getTRXPORTNUM() + ":1";

						} else {
							System.out.println("is not null");
							trxGroup += "," + subcellID.substring(0, 1) + "|" + subcellTRXMAP.getTRXPORTNUM() + ":1";

						}

						linkDepartment.setTRX_GROUP(trxGroup);
						mapLinkDepartment.put(subcellID, linkDepartment);

					} else {
						System.out.println("doesn't exist");
					}

				}
				
				
				
				logger.info("Reading Declaration.jsp for ALIAS...");
				
				try {
				
				File TestFile = new File("\\\\"+hostSource+"\\Pricer\\Tomcat\\webapps\\mobile1\\Declaration.jsp");
				System.out.println(TestFile.getPath());
				logger.info(TestFile.getPath());
					
			        //NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, winUser, winPassword);
			        //SmbFile smbFile = new SmbFile("smb:\\\\"+hostSource+"\\Pricer\\Tomcat\\webapps\\mobile1\\Declaration.jsp", auth);
			        
			        //System.out.println("SMB File is "+smbFile.getPath());
					
				if (TestFile.exists() == false){
					logger.fatal("##########File Declaration.jsp doesn't exist!!! Stop Export");
					System.exit(0); 
				}
				
				//System.out.println("File Declaration.jsp seems to be existing ... continue");
				
				FileReader R3MobileFile = new FileReader("\\\\"+hostSource+"\\Pricer\\Tomcat\\webapps\\mobile1\\Declaration.jsp");
				BufferedReader BuffR3MobileFile = new BufferedReader(R3MobileFile);
				
				String ReadLine = BuffR3MobileFile.readLine();
				//InputStream ReadLine2 = smbFile.getInputStream();
				
				System.out.println("smbfile to inputstream");
				
				//String ReadLine = IOUtils.toString(ReadLine2, StandardCharsets.UTF_8.name());
				
				String LineClientName = "";
				String LineSubcellName = "";
				String LinePrinterName = "";
				String LineHostName = "";
				
				
				while (ReadLine != null){
					if ((ReadLine.trim().length()) > 28){
						if(ReadLine.trim().substring(0,23).equalsIgnoreCase("String SubcellDisplay[]")){				
							LineClientName = ReadLine.trim().substring(23).replace("}","").replace("{","").replace("\"","").replace("=","").replace(";","");
							logger.info("Client: " + LineClientName);					
						}	
						if(ReadLine.trim().substring(0,21).equalsIgnoreCase("String SubcellValue[]")){
							LineSubcellName = ReadLine.trim().substring(21).replace("}","").replace("{","").replace("\"","").replace("=","").replace(";","");
							logger.info("Subcell: " + LineSubcellName );
						}
						
						if(ReadLine.trim().substring(0,28).equalsIgnoreCase("String LocalizationDisplay[]")){				
							LinePrinterName = ReadLine.trim().substring(28).replace("}","").replace("{","").replace("\"","").replace("=","").replace(";","");
							logger.info("Printer: " + LinePrinterName);					
						}
						if(ReadLine.trim().substring(0,26).equalsIgnoreCase("String LocalizationValue[]")){				
							LineHostName = ReadLine.trim().substring(26).replace("}","").replace("{","").replace("\"","").replace("=","").replace(";","");
							logger.info("HostName: " + LineHostName);					
						}	
						
					}
					ReadLine = BuffR3MobileFile.readLine();
				}

				String StClient[] = LineClientName.trim().split(",");
				String StSubcell[] = LineSubcellName.trim().split(",");
				String StPrinter[] = LinePrinterName.trim().split(",");
				String StHostName[] = LineHostName.trim().split(",");
				
				StringBuilder completeLineLD = new StringBuilder();
				StringBuilder completeLinePrint = new StringBuilder();
				
				for (int i=0;  i<(StSubcell.length); i++) { 

					completeLineLD.append("UPDATE `link_department` set `ALIAS`='" + StClient[i].replaceAll("à", "a").replaceAll("é","e") + "' WHERE `ID`='" + StSubcell[i] + "';\r\n");
	
				}
				
				for(int i=0; i<(StHostName.length); i++){
					
					completeLinePrint.append("UPDATE `printhostconfiguration` set `ALIAS` = '" +StPrinter[i] + "' WHERE `HOSTNAME`='" + StHostName[i] + "';\r\n");
				}
				
				completeLinePrint.append("UPDATE `printhostconfiguration` set `ALIAS` = 'default', `HOSTNAME` = 'default' WHERE `HOSTNAME`='127.0.0.1';\r\n");
				completeLinePrint.append("UPDATE `printmodelconfiguration` set `DEFAULT_MODEL` = '1' WHERE `PRINTHOST_ID`='1' AND `MODEL`='1';\r\n");
				completeLinePrint.append("DELETE printhostconfiguration,printmodelconfiguration FROM printhostconfiguration LEFT JOIN printmodelconfiguration "
						+ "ON printhostconfiguration.id=printmodelconfiguration.PRINTHOST_ID WHERE hostname LIKE 'PCXP%';");
				
				
				BufferedWriter writerLD = new BufferedWriter(new FileWriter(FolderForExtractedData + "\\UPDATE_LD.sql", true));
				BufferedWriter writerHost = new BufferedWriter(new FileWriter(FolderForExtractedData + "\\UPDATE_PH.sql", true));
				
				writerLD.append(completeLineLD);
				writerLD.close();
				
				writerHost.append(completeLinePrint);
				writerHost.close();
				
				//
				BuffR3MobileFile.close();
				
				//ReadLine.

				} catch (Exception e) {
					logger.error("##########Problem with Declaration.jsp  : " + e.getMessage());
					System.exit(0);
				}
				
				
				
				
				try {
					
					logger.info("Start Read HostPrinterMapping.xml");
					logger.info("Reading File HostPrinterMapping.xml");			
					

					
					File R3FileName = new File("\\\\"+hostSource+"\\Pricer\\R3Server\\config\\HostPrinterMapping.xml");
					System.out.println(R3FileName.getPath());
					logger.info(R3FileName.getPath());
					
			        //NtlmPasswordAuthentication auth2 = new NtlmPasswordAuthentication(".\\", winUser, winPassword);
			        //SmbFile smbFile = new SmbFile("smb://"+hostSource+"/Pricer/R3Server/config/HostPrinterMapping.xml", auth2);
					
					
					if (R3FileName.exists() == false){
						logger.fatal("##########File HostPrinterMapping.xml doesn't exist!!! Stop Export");
						System.exit(0);
					}
					
					//InputStream is = smbFile.getInputStream();
				
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(R3FileName);
					
					
					doc.getDocumentElement().normalize();		
					NodeList nList = doc.getElementsByTagName("Profile");
					
					StringBuilder completeLinePrintHost = new StringBuilder();
					StringBuilder completeLinePrintModel = new StringBuilder();
					
					for (int NbrClient = 1; NbrClient < nList.getLength()+1; NbrClient++) {
						Node nNode = nList.item(NbrClient-1);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {					
							Element eElement = (Element) nNode;
							logger.info("Hostname : " + eElement.getElementsByTagName("HostName").item(0).getTextContent());
							logger.info("Subcell : " + eElement.getElementsByTagName("DefaultSubcell").item(0).getTextContent());
							logger.info("Nb Model : " + eElement.getElementsByTagName("Model").getLength());
							
							completeLinePrintHost.append("INSERT INTO `printhostconfiguration`(`ID`,`HOSTNAME`,`LINK_DEPARTMENT`,`ALIAS`) "
									+ "VALUES ( '" + NbrClient + "','" + eElement.getElementsByTagName("HostName").item(0).getTextContent() + "','" 
									+ eElement.getElementsByTagName("DefaultSubcell").item(0).getTextContent() + "','LP" + NbrClient + "');\r\n");			


							//PreparedStatement BsSQLCommand = R5_Connection.prepareStatement("INSERT INTO `printhostconfiguration`(`ID`,`HOSTNAME`,`SUBCELL`,`ALIAS`) VALUES ( '" + NbrClient + "','" + eElement.getElementsByTagName("HostName").item(0).getTextContent() + "','" + eElement.getElementsByTagName("DefaultSubcell").item(0).getTextContent() + "','P" + NbrClient + "');");			
							//int Result = BsSQLCommand.executeUpdate("INSERT INTO `printhostconfiguration`(`ID`,`HOSTNAME`,`SUBCELL`,`ALIAS`) VALUES ( '" + NbrClient + "','" + eElement.getElementsByTagName("HostName").item(0).getTextContent() + "','" + eElement.getElementsByTagName("DefaultSubcell").item(0).getTextContent() + "','P" + NbrClient + "');");	

							for (int i = 0; i < eElement.getElementsByTagName("Model").getLength(); i++) {
								logger.info("Model : " + eElement.getElementsByTagName("Model").item(i).getTextContent());
								String ModelName = eElement.getElementsByTagName("Model").item(i).getTextContent();
								ModelName = ModelName.replace("E4_HCS","2").replace("E4_HCN","1").replace("E4_HCW","3");
								logger.info(ModelName.replace("E4_HCS","2").replace("E4_HCN","1").replace("E4_HCW","3"));
								logger.info("Printer : " + eElement.getElementsByTagName("PrinterId").item(i).getTextContent());
								String PrinterName = eElement.getElementsByTagName("PrinterId").item(i).getTextContent();
								//PrinterName = "PRICER_HC" + PrinterName.replace("M","W").substring(PrinterName.length()-2, PrinterName.length()-1) + "_" + PrinterName.substring(PrinterName.length()-1, PrinterName.length());
								PrinterName = "PRICER_" + PrinterName;
								logger.info(PrinterName);
								
								completeLinePrintModel.append("INSERT INTO `printmodelconfiguration`(`PRINTHOST_ID`,`DEFAULT_MODEL`,`MODEL`,`PRINTER`,`STICKERSIZE_ID`) VALUES ( '" + NbrClient + "', '0','" + ModelName + "','" + PrinterName + "','1');\r\n");
								
								//BsSQLCommand = R5_Connection.prepareStatement("INSERT INTO `printmodelconfiguration`(`PRINTHOST_ID`,`MODEL`,`PRINTER`) VALUES ( '" + NbrClient + "','" + ModelName + "','" + PrinterName + "');");			
								logger.info("INSERT INTO `printmodelconfiguration`(`PRINTHOST_ID`,`MODEL`,`PRINTER`) VALUES ( '" + NbrClient + "','" + ModelName + "','" + PrinterName + "');\r\n");	
								//Result = BsSQLCommand.executeUpdate("INSERT INTO `printmodelconfiguration`(`PRINTHOST_ID`,`MODEL`,`PRINTER`) VALUES ( '" + NbrClient + "','" + ModelName + "','" + PrinterName + "');");	

							}
						}
					}
					
					BufferedWriter writerPrintHost = new BufferedWriter(new FileWriter(FolderForExtractedData + "\\PrintHostConfiguration.sql", true));
					writerPrintHost.append(completeLinePrintHost);
					
					writerPrintHost.close();
					
					BufferedWriter writerPrintModel = new BufferedWriter(new FileWriter(FolderForExtractedData + "\\PrintModelConfiguration.sql", true));
					writerPrintModel.append(completeLinePrintModel);
					
					writerPrintModel.close();
					
					logger.info("Extraction HostPrinterMapping finished");

					
					
				} catch (Exception e) {
					logger.error("##########Problem with HostPrinterMapping.xml  : " + e.getMessage());
					System.exit(0);
				}
				
				
				
				
				logger.info("Create ITEM.i1 file for full item...");
				new OperationOnFile(FolderForExtractedData + "\\item.i1").createFileExportSQLItem(lstItem);
				

				logger.info("Create Export File for LinkDepartment...");
				new OperationOnFile(FolderForExtractedData + "\\LinkDepartment.sql")
						.createFileExportSQLLinkDepartment(mapLinkDepartment);

				logger.info("Getting Transceiver List...");
				List<Transceiver> lstTransceiverInfR413 = pricerDB
						.getTRXListFrom_inf_R5_4_13("select * from transceiver");

				logger.info("Create Export File for Transceiver List...");
				new OperationOnFile(FolderForExtractedData + "\\Transceiver.sql")
						.createFileExportSQLTRX_inf_R5_4_13(lstTransceiverInfR413);

				logger.info("Create Export File for Base Stations List...");
				new OperationOnFile(FolderForExtractedData + "\\Basestation.sql")
						.createFileExportSQLBaseStations(lstBaseStation_inf_R5_4_13);

				logger.info("Create Export File for Links...");
				pricerDB.createFileExportLink(
					"SELECT item.itemid,PRICERLABEL.PLBARCODE,PRICERLABEL.PLHOMESUBCELLID from item,PRICERLABEL,LINK WHERE item.itemid=link.linitemidref AND pricerlabel.plid=link.linplidref",
					FolderForExtractedData + "\\link.i1");
				
				
				logger.info("Create Export File for item...");
				//pricerDB.createFileExportLinkFromMysqlDump("item");

				
				// create m1 file
				logger.info("Create " + FolderForExtractedData + "Link.m1 File for pricer Integration...");
				new OperationOnFile(FolderForExtractedData + "\\link.m1").createLinkm1();
				
				logger.info("Create " + FolderForExtractedData + "item.m1 File for Pricer Integration...");
				new OperationOnFile(FolderForExtractedData + "\\item.m1").createItemm1();
				
				break;
				
				
				
			default: // > R5.4.13
				
				logger.fatal("Pricer version not compatible , exiting thread ");
				System.exit(1);
				
				
				break;
				
			
			}

		}

		catch (NullPointerException npex) {
			logger.fatal("general ERROR check File and FOlders Configuration :" + npex.getCause());
			logger.fatal("Migration Aborted !!!");
			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
			running.set(false);
		}

		
		logger.info("Prepare Migration Terminated...");
		
		logger.info("!!!!!!! STOPPING SERVICES ON THE OLD VM !!!!!!!");

		String statusSource = "";
		
		statusSource = GetServiceStatus("PricerServer",hostSource);
		System.out.println("Status service Pricerserver : " + statusSource);
		
		if (statusSource.equalsIgnoreCase("Running")) {
			logger.info("Trying to stop service : PricerServer on "+hostSource);
			RemoteStopService("PricerServer",hostSource);

			for (int count = 1; count <= 3; count++) {
				statusSource = GetServiceStatus("PricerServer",hostSource);
				System.out.println("status = " + statusSource);
				logger.info("status of pricer server = " + statusSource);
				if (statusSource.equalsIgnoreCase("stopped") == true) {
					logger.info("PricerServer service is stopped");
					break;

				}

				else {
					try {

						logger.warn("waiting 15 seconds tentative " + count + "/3");
						logger.info("PricerServer service status = " + statusSource);
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

			}

		}
		
		statusSource = GetServiceStatus("PricerMysql",hostSource);
		System.out.println("Status service PricerMysql : " + statusSource);
		
		if (statusSource.equalsIgnoreCase("Running")) {
			logger.info("Trying to stop service : PricerMysql on "+hostSource);
			RemoteStopService("PricerMysql",hostSource);

			for (int count = 1; count <= 3; count++) {
				statusSource = GetServiceStatus("PricerMysql",hostSource);
				System.out.println("status = " + statusSource);
				logger.info("status of PricerMysql = " + statusSource);
				if (statusSource.equalsIgnoreCase("stopped") == true) {
					logger.info("PricerMysql service is stopped");
					break;

				}

				else {
					try {

						logger.warn("waiting 15 seconds tentative " + count + "/3");
						logger.info("PricerMysql service status = " + statusSource);
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

			}

		}
		
		statusSource = GetServiceStatus("PricerTomcat",hostSource);
		System.out.println("Status service PricerTomcat : " + statusSource);
		
		if (statusSource.equalsIgnoreCase("Running")) {
			logger.info("Trying to stop service : PricerTomcat on "+hostSource);
			RemoteStopService("PricerTomcat",hostSource);

			for (int count = 1; count <= 3; count++) {
				statusSource = GetServiceStatus("PricerTomcat",hostSource);
				System.out.println("status = " + statusSource);
				logger.info("status of PricerTomcat = " + statusSource);
				if (statusSource.equalsIgnoreCase("stopped") == true) {
					logger.info("PricerTomcat service is stopped");
					break;

				}

				else {
					try {

						logger.warn("waiting 15 seconds tentative " + count + "/3");
						logger.info("PricerTomcat service status = " + statusSource);
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}

			}

		}
		
		
		
		
		
		
		
		logger.info("Flag Deleted...");
		new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
		new OperationOnFile("PrepareMigration_ok.flag").createFlagFile();
		running.set(false);
	

		
	}
	
}

	public static String deletDuplcateValue(String trxGroupR3) {

		String s2 = trxGroupR3.replace("\'", "");

		String[] split = s2.split(",", -1);

		ArrayList<String> al2 = new ArrayList<String>();

		for (int i = 0; i < split.length; i++) {

			String o = split[i];
			if (!al2.contains(o))
				al2.add(o);
		}

		al2.set(0, "'" + al2.get(0) + "'");
		al2.set(1, "'" + al2.get(1) + "'");
		al2.set(2, "'" + al2.get(2));
		al2.set(al2.size() - 2, al2.get((al2.size()) - 2) + "'");
		al2.set(al2.size() - 1, "'" + al2.get((al2.size()) - 1) + "'");

		String s3 = "";
		for (String val3 : al2) {
			s3 += val3 + ",";

		}

		s3 = s3.substring(0, s3.length() - 1);

		return s3;

	}
	
	
	private static String GetServiceStatus(String serviceName, String hostName) {

		try {

			Process p = Runtime.getRuntime().exec("sc \\\\"+hostName +" query " + serviceName);
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



}
