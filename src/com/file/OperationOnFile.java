package com.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pricer.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class OperationOnFile {
	static Logger logger = Logger.getLogger(OperationOnFile.class);
	static Wini ini;
	private File file = null;
	private String encoding = "UTF-8";

	public OperationOnFile(String fileName) {

		file = new File(fileName);
	
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
	
	}

	
	
	public boolean isExist() {
		
	return file.exists();
		
	}
	
	public void deleteFile() {
		
		this.file.delete();
		
	}
	
	
	public void createFlagFile() {
		PrintStream PSFileFlag = null;

		try {
			try {
				PSFileFlag = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)),
						true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		PSFileFlag.close();

	}

	public void createLinkm1() {
		PrintStream PSFileExportM1 = null;

		System.out.println("here is the m1 file " + this.file.getPath());

		try {
			try {
				PSFileExportM1 = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		StringBuilder completeLine = new StringBuilder();

		
		String PRICER_PFI_FOLDER = ini.get("SERVER","PRICER_PFI_FOLDER");

		
		

		
		
		
		
		
		
		completeLine.append(
				"TARGETLINK,9999,," + PRICER_PFI_FOLDER + "\\Datafiles\\link.i1," + PRICER_PFI_FOLDER + "\\Resultfiles\\link.r7");

		PSFileExportM1.println(completeLine.toString());
		PSFileExportM1.flush();

		PSFileExportM1.close();

	}
	
	public void createItemm1() {
		PrintStream PSFileExportM1 = null;

		System.out.println("here is the m1 file " + this.file.getPath());

		try {
			try {
				PSFileExportM1 = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		StringBuilder completeLine = new StringBuilder();

		
		String PRICER_PFI_FOLDER = ini.get("SERVER","PRICER_PFI_FOLDER");

		
		

		
		
		
		
		
		
		completeLine.append(
				"UPDATE,9999,," + PRICER_PFI_FOLDER + "\\Datafiles\\item.i1," + PRICER_PFI_FOLDER + "\\Resultfiles\\item.r7");

		PSFileExportM1.println(completeLine.toString());
		PSFileExportM1.flush();

		PSFileExportM1.close();

	}

	
	
	public void purgeResultFolder() {
		
		//String PRICER_RESULT_FOLDER = ini.get("SERVER", "PRICER_RESULT_FOLDER");
		String FolderForExtractedData = System.getProperty("user.dir");
		
		File resultFodler = new File(FolderForExtractedData+"//Result");
		
		try {
			FileUtils.cleanDirectory(resultFodler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


	public void createFileExportSQLTRX(List<Transceiver> lstTransceivers) {
		PrintStream PSFileExportSQLTRX = null;
		int i = 0;

		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table transceiver;");

		try {
			try {
				PSFileExportSQLTRX = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLTRX.println(truncateLine.toString());
				PSFileExportSQLTRX.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (Transceiver trx : lstTransceivers) {

			i = i + 1;

			StringBuilder completeLine = new StringBuilder();

			completeLine.append("INSERT INTO `transceiver`(`ID`," + "`TRXBSNAMEREF`," + "`TRXPORTNUM`,"
					+ "`TRXCABLEINDEX`," + "`TRXHWID`," + "`TRXFAILTIME`) " + "VALUES ( ");

			// completeLine.append("'").append(trx.getID()).append("',");
			completeLine.append("'").append(i).append("',");
			completeLine.append("'").append(trx.getTRXBSNAMEREF()).append("',");
			completeLine.append("'").append(trx.getTRXPORTNUM()).append("',");
			completeLine.append("'").append(trx.getTRXCABLEINDEX()).append("',");
			completeLine.append("'").append(trx.getTRXHWID()).append("');");
			completeLine.append("'1970-01-01 01:00:00');");

			// trx.setTRXIQOFFSET("NULL");

			PSFileExportSQLTRX.println(completeLine.toString());
			PSFileExportSQLTRX.flush();
		}

		PSFileExportSQLTRX.close();

	}

	public void createFileExportSQLTRX_inf_R5_4_13(List<Transceiver> lstTransceivers) {
		PrintStream PSFileExportSQLTRX = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table transceiver;");

		try {
			try {
				PSFileExportSQLTRX = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLTRX.println(truncateLine.toString());
				PSFileExportSQLTRX.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}
		
		int i =1;

		for (Transceiver trx : lstTransceivers) {
			StringBuilder completeLine = new StringBuilder();

			completeLine.append("INSERT INTO `transceiver`(`ID`," + "`TRXBSNAMEREF`," + "`TRXPORTNUM`,"
					+ "`TRXCABLEINDEX`," + "`TRXHWID`," + "`TRXFAILTIME`)"

					+ "VALUES ( ");
			completeLine.append("'").append(i).append("',");
			completeLine.append("'").append(trx.getTRXBSNAMEREF()).append("',");
			completeLine.append("'").append(trx.getTRXPORTNUM()).append("',");
			completeLine.append("'").append(trx.getTRXCABLEINDEX()).append("',");
			completeLine.append("'").append(trx.getTRXHWID()).append("',");
			completeLine.append("'1970-01-01 01:00:00');");

			// trx.setTRXIQOFFSET("NULL");

			PSFileExportSQLTRX.println(completeLine.toString());
			PSFileExportSQLTRX.flush();
			
			i++;
		}

		PSFileExportSQLTRX.close();

	}

	public void createFileExportSQLTRXLocation(List<Transceiver> lstTransceivers) {
		PrintStream PSFileExportSQLTRX = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table transceiver_location;");

		try {
			try {
				PSFileExportSQLTRX = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLTRX.println(truncateLine.toString());
				PSFileExportSQLTRX.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}



		for (Transceiver trx : lstTransceivers) {
			StringBuilder completeLine = new StringBuilder();

			completeLine.append("INSERT INTO `transceiver_location`(`TRXHWID`," + "`LOCATION`) VALUES ( ");
			completeLine.append("'").append(trx.getTRXHWID()).append("',");

		/*	if(trx.getTRXLOCATION().equalsIgnoreCase("")){
				completeLine.append("NULL");
			}else*/
			completeLine.append("'").append(trx.getTRXLOCATION()).append("');");

			// trx.setTRXIQOFFSET("NULL");

			PSFileExportSQLTRX.println(completeLine.toString());
			PSFileExportSQLTRX.flush();

		}

		PSFileExportSQLTRX.close();

	}


	public void createFileExportSQLSubcells(List<Subcell> lstSubcells) {
		PrintStream PSFileExportSQLSubcells = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table subcell;");
		try {
			try {
				PSFileExportSQLSubcells = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLSubcells.println(truncateLine.toString());
				PSFileExportSQLSubcells.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (Subcell subcell : lstSubcells) {
			StringBuilder completeLine = new StringBuilder();
			completeLine.append(
					"INSERT INTO `subcell`(`SUBCELLID`,`SUBISBACKOFFICE`,`SUBCELLMODE`,`SUBBSNAME`,`SUBCELLTRANSMISSIONTIME`,`SUBCELLCREATIONTIME`,`LASTCALIBRATIONTIME`,`SUBCELLALIAS`) VALUES ( ");
			completeLine.append("'").append(subcell.getSUBCELLID()).append("',");
			completeLine.append("'").append(subcell.getSUBISBACKOFFICE()).append("',");
			completeLine.append("'").append(subcell.getSUBCELLMODE()).append("',");
			completeLine.append("'").append(subcell.getSUBBSNAME()).append("',");
			completeLine.append("'").append(subcell.getSUBCELLTRANSMISSIONTIME()).append("',");
			completeLine.append("'").append(subcell.getSUBCELLCREATIONTIME()).append("',");
			completeLine.append("'").append(subcell.getLASTCALIBRATIONTIME()).append("',");
			completeLine.append("'").append(subcell.getSUBCELLALIAS()).append("',)");

			PSFileExportSQLSubcells.println(completeLine.toString());
			PSFileExportSQLSubcells.flush();
		}

		PSFileExportSQLSubcells.close();

	}

	public void createFileExportSQLBaseStations(List<BaseStation> lstBS) {
		PrintStream PSFileExportSQLBaseStations = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table basestation;");
		try {
			try {
				PSFileExportSQLBaseStations = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLBaseStations.println(truncateLine.toString());
				PSFileExportSQLBaseStations.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (BaseStation bs : lstBS) {
			StringBuilder completeLine = new StringBuilder();

			
			completeLine.append("INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`,`BSHWID`,`BSALIAS`,`BASESTATION_ACCEPTANCE`) VALUES ( ");
			completeLine.append("'").append(bs.getBSNAME()).append("',");
			completeLine.append("'").append(bs.getBSADDRESS()).append("',");
			completeLine.append("'").append(bs.getBSHWID()).append("',");
			completeLine.append("'',");
			completeLine.append("'").append("1").append("');");

			/*
			 * completeLine.
			 * append("INSERT INTO `basestation`(`BSNAME`,`BSADDRESS`,`BSHWID`,`BSSTATUS`,`BSFAILTIME`,`BSSWID`,`INFRAHEAVYTIME`,`BSFAIL_COUNT`,`BSALIAS`) VALUES ( "
			 * ); completeLine.append("'").append(bs.getBSNAME()).append("',");
			 * completeLine.append("'").append(bs.getBSADDRESS()).append("',");
			 * completeLine.append("'").append(bs.getBSHWID()).append("',");
			 * completeLine.append("'").append(bs.getBSSTATUS()).append("',");
			 * completeLine.append("'").append(bs.getBSFAILTIME()).append("',");
			 * completeLine.append("'").append(bs.getBSHWID()).append("',");
			 * completeLine.append("'").append(bs.getINFRAHEAVYTIME()).append("',");
			 * completeLine.append("'").append(bs.getBSFAIL_COUNT()).append("',");
			 * completeLine.append("'").append(bs.getBSALIAS()).append("',)");
			 * 
			 */
			PSFileExportSQLBaseStations.println(completeLine.toString());
			PSFileExportSQLBaseStations.flush();
		}

		PSFileExportSQLBaseStations.close();

	}


	public void createFileExportSQLGeoStore(List<GeoStore> lstGeoStore) {
		PrintStream PSFileExportSQLBaseStations = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table geo_store;");
		try {
			try {
				PSFileExportSQLBaseStations = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLBaseStations.println(truncateLine.toString());
				PSFileExportSQLBaseStations.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (GeoStore gs : lstGeoStore) {
			StringBuilder completeLine = new StringBuilder();


			completeLine.append("INSERT INTO `geo_store`(`ID`,`MAP`,`CREATED`,`ROUTING`) VALUES ( ");
			completeLine.append("'").append(gs.getID()).append("',");
			completeLine.append("'").append(gs.getMAP()).append("',");
			completeLine.append("'").append(gs.getCREATED()).append("',");
			completeLine.append("'").append(gs.getROUTING()).append("');");

			PSFileExportSQLBaseStations.println(completeLine.toString());
			PSFileExportSQLBaseStations.flush();
		}

		PSFileExportSQLBaseStations.close();

	}



	public void createFileExportSQLSubcellTRXMAP(List<SubcellTRXMap> lstSubcellTRXMAP) {
		PrintStream PSFileExportSQLSubcellTRXMAP = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table subcelltrxmap;");
		try {
			try {
				PSFileExportSQLSubcellTRXMAP = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLSubcellTRXMAP.println(truncateLine.toString());
				PSFileExportSQLSubcellTRXMAP.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (SubcellTRXMap TRXMAP : lstSubcellTRXMAP) {
			StringBuilder completeLine = new StringBuilder();
			completeLine.append("INSERT INTO `subcelltrxmap`(`TRXID`,`SCMSUBCELLID`) VALUES ( ");
			completeLine.append("'").append(TRXMAP.getTRXID()).append("',");
			completeLine.append("'").append(TRXMAP.getSCMSUBCELLID()).append("',)");

			PSFileExportSQLSubcellTRXMAP.println(completeLine.toString());
			PSFileExportSQLSubcellTRXMAP.flush();
		}

		PSFileExportSQLSubcellTRXMAP.close();

	}

	public void createFileExportSQLLinkDepartment(List<LinkDepartment> lstLinkDepartment) {
		PrintStream PSFileExportSQLinkDepartment = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table link_department;");
		try {
			try {
				PSFileExportSQLinkDepartment = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLinkDepartment.println(truncateLine.toString());
				PSFileExportSQLinkDepartment.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (LinkDepartment linkDepartment : lstLinkDepartment) {
			StringBuilder completeLine = new StringBuilder();
			completeLine.append("INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( ");
			completeLine.append("'").append(linkDepartment.getID()).append("',");
			completeLine.append("'").append(linkDepartment.getALIAS()).append("',");
			completeLine.append("'").append(linkDepartment.getTRX_GROUP()).append("',");
			completeLine.append("'").append(linkDepartment.getIS_BACKOFFICE()).append("');");

			PSFileExportSQLinkDepartment.println(completeLine.toString());
			PSFileExportSQLinkDepartment.flush();
		}

		PSFileExportSQLinkDepartment.close();

	}
	
	
	public void createFileExportSQLItem(List<Item> lstItem) {
		PrintStream PSFileExportSQLItem = null;
		try {
			try {
				PSFileExportSQLItem = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				//PSFileExportSQLItem.println(truncateLine.toString());
				PSFileExportSQLItem.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		for (Item item : lstItem) {
			StringBuilder completeLine = new StringBuilder();
			completeLine.append("9999 ").append(item.getItemID()).append(" 7 0 |").append(item.getItemName()).append("| 121 0 |").append(item.getItemIpf())
			.append("| 4 0 |").append(item.getIntItemCode()).append("| 5 0 |").append(item.getItemDptIdRef()).append("| 6 0 |").append(item.getItemGroup())
			.append("| 8 0 |").append(item.getItemName2()).append("| 9 0 |").append(item.getItemName3()).append("| 10 0 |").append(item.getItemName4())
			.append("| 11 0 |").append(item.getItemName5())
			.append("| 81 0 |").append(item.getItemDesc1()).append("| 82 0 |").append(item.getItemDesc2()).append("| 83 0 |").append(item.getItemDesc3())
			.append("| 84 0 |").append(item.getItemDesc4()).append("| 85 0 |").append(item.getItemDesc5()).append("| 86 0 |").append(item.getItemDesc6())
			.append("| 87 0 |").append(item.getItemDesc7()).append("| 88 0 |").append(item.getItemDesc8()).append("| 89 0 |").append(item.getItemDesc9())
			.append("| 90 0 |").append(item.getItemDesc10()).append("| 91 0 |").append(item.getItemDesc11()).append("| 23 0 |").append(item.getPrice())
			.append("| 24 0 |").append(item.getDeee()).append("| 26 0 |").append(item.getLibelleDeee()).append("| 27 0 |").append(item.getLibelleEco())
			.append("| 37 0 |").append(item.getDiscountFlag()).append("| 40 0 |").append(item.getCodeBarre()).append("| 41 0 |").append(item.getIlot())
			.append("| 42 0 |").append(item.getEmplacement()).append("| 45 0 |").append(item.getUnitPrice()).append("| 47 0 |").append(item.getCoeff())
			.append("| 52 0 |").append(item.getdSign()).append("| 51 0 |").append(item.getCross()).append("| 50 0 |").append(item.getSquare()).append("| 54 0 |")
			.append(item.getCircle()).append("| 53 0 |").append(item.getCircle()).append("| 71 0 |").append(item.getUnitCode()).append("| 74 0 |")
			.append(item.getTaxExcludingPrice()).append("| 318 0 |").append(item.getStockTheo()).append("| 319 0 |").append(item.getQtyInOrder())
			.append("| 321 0 |").append(item.getDateDeliv()).append("| 323 0 |").append(item.getStockDispo()).append("|,");
			
			
			
			
			

			PSFileExportSQLItem.println(completeLine.toString());
			PSFileExportSQLItem.flush();
		}

		PSFileExportSQLItem.close();

	}

	public void createFileExportSQLLinkDepartment(HashMap<String, LinkDepartment> mapLinkDepartment) {
		PrintStream PSFileExportSQLinkDepartment = null;
		StringBuilder truncateLine = new StringBuilder();
		truncateLine.append("TRUNCATE table link_department;");
		try {
			try {
				PSFileExportSQLinkDepartment = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(this.file.getPath(), true)), true, encoding);
				PSFileExportSQLinkDepartment.println(truncateLine.toString());
				PSFileExportSQLinkDepartment.flush();
			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		LinkDepartment linkdepartment;
		for (Map.Entry<String, LinkDepartment> entry : mapLinkDepartment.entrySet()) {

			StringBuilder completeLine = new StringBuilder();
			linkdepartment = entry.getValue();

			completeLine.append("INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( ");
			completeLine.append("'").append(linkdepartment.getID()).append("',");
			completeLine.append("'").append(linkdepartment.getALIAS()).append("',");
			completeLine.append("'").append(linkdepartment.getTRX_GROUP()).append("',");
			completeLine.append("'").append(linkdepartment.getIS_BACKOFFICE()).append("');");

			PSFileExportSQLinkDepartment.println(deletDuplcateValue(completeLine.toString()));
			PSFileExportSQLinkDepartment.flush();
		}

		PSFileExportSQLinkDepartment.close();

	}

	public static String deletDuplcateValue(String trxGroupR3) {

		String begin = "INSERT INTO `link_department`(`ID`,`ALIAS`,`TRX_GROUP`,`IS_BACKOFFICE`) VALUES ( ";
		String end = ");";
		String s2 = trxGroupR3.replace("\'", "").replace(begin, "").replace(end, "");

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

		return begin + s3 + end;
	}

}
