package com.database;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pricer.*;
import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;


public class OperationOnDBSource {

	private Connection connectionSource;
	//private Connection connectionTarget;
	private Wini ini;
	private String hostNameSource;
	private String username;
	private String password;
	private String portnumber;

	static Logger logger = Logger.getLogger(Start.class);

	public OperationOnDBSource() {

		super();

/*		try {
			this.ini = new Wini(new File("preference.ini"));
		} catch (InvalidFileFormatException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		} catch (IOException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		}*/

/*		this.hostNameSource = ini.get("MYSQL_SOURCE", "HOSTNAME", String.class);
		this.portnumber = ini.get("MYSQL_SOURCE", "PORTNAME", String.class);
		this.username = ini.get("MYSQL_SOURCE", "USER", String.class);
		this.password = ini.get("MYSQL_SOURCE", "PASSWORD", String.class);*/

		this.hostNameSource = "localhost";
		this.portnumber = "3306";
		this.username = "root";
		this.password = "";


		System.out.println("operation on DB Source initialisation.....");
		JDBCConnector jdbcconnectorSource = new JDBCConnector(this.hostNameSource, this.username, this.password, this.portnumber);
		this.connectionSource = jdbcconnectorSource.connectDatabase();


	}
	


	public Connection getConnectionSource() {
		return connectionSource;
	}


	public void setConnectionSource(Connection connectionSource) {
		this.connectionSource = connectionSource;
	}
	
	
	



//	public String CheckTable() {
//		Statement st = null;
//		ResultSet rs = null;
//		String releaseName = null;
//		DatabaseMetaData dbm = null;
//		String tableNamesubcelltrxmap = "subcelltrxmap";
//
//		try {
//			st = connectionSource.createStatement();
//
//			dbm = connectionSource.getMetaData();
//			rs = dbm.getTables(null, null, tableNamesubcelltrxmap, null);
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			logger.error("SQLException to database, please verify your Parameters ==> "
//					+ e.getCause());
//			logger.fatal("Migration Aborted");
//			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
//			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
//			return null;
//			
//
//		} catch (NullPointerException n) {
//
//			logger.error("Unable to connect to database, please verify your Parameters (user, password) ==> "
//					+ n.getCause());
//			logger.fatal("Migration Aborted");
//			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
//			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
//			return null;
//		}
//
//		try {
//
//			if (rs.next()) {
//				// System.out.println("Table subcelltrxmap exist");
//
//				rs = st.executeQuery("select * from subcelltrxmap");
//				// rs = dbm.getTables(null, null, tableNametrxid, null);
//
//				if (hasColumn(rs, "trxid")) {
//
//					// System.out.println("Column trxid exist");
//					releaseName = "< R5.4.13";
//
//				}
//
//				else {
//
//					// System.out.println("column trxid doesn't exist");
//					releaseName = "R3";
//				}
//
//			} else {
//
//				// System.out.println("table subcelltrxmap doesn't exist ");
//				releaseName = "> R5.4.13";
//
//			}
//		}
//
//		catch (SQLException e) {
//
//			logger.error("SQLException to database, please verify your Parameters ==> "
//					+ e.getCause());
//			logger.fatal("Migration Aborted");
//			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
//			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
//			return null;
//		} catch (NullPointerException npex) {
//
//			logger.warn("unable to extract elements from database ...");
//			logger.fatal("Migration Aborted");
//			new OperationOnFile("PrepareMigration_begin.flag").deleteFile();
//			new OperationOnFile("PrepareMigration_ko.flag").createFlagFile();
//			return null;
//
//		}
//
//		try {
//			st.close();
//			rs.close();
//
//		} catch (SQLException e) {
//
//			e.printStackTrace();
//		}
//
//		catch (NullPointerException n) {
//
//			logger.warn("Unable to disconnect from database");
//			// n.printStackTrace();
//		}
//
//		return releaseName;
//
//	}

/*
	public void importData(String resultDirectory, String sqlFile) {
		
		
		try {
		ScriptRunner sr = new ScriptRunner(connectionTarget);
		
		Reader reader = new BufferedReader(new FileReader(resultDirectory+"\\"+sqlFile));
		
		sr.runScript(reader);
		
		} catch (Exception e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}
		
	}*/
	
	
	
	

	public List<StoreInfo> getStoreInfos(String request) {


		List<StoreInfo> lstStoreInfos = new ArrayList<StoreInfo>();
		StoreInfo storeInfo;
		Statement st = null;
		ResultSet rs = null;
		

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				
				storeInfo = new StoreInfo();

				storeInfo.setID(rs.getString("ID"));
				storeInfo.setSTORE_NAME(rs.getString("STORE_NAME"));
				storeInfo.setSTORE_CHAIN(rs.getString("STORE_CHAIN"));
				storeInfo.setADDRESS_1(rs.getString("ADDRESS_1"));
				storeInfo.setADDRESS_2(rs.getString("ADDRESS_2"));
				storeInfo.setADDRESS_3(rs.getString("ADDRESS_3"));
				storeInfo.setZIP_CODE(rs.getString("ZIP_CODE"));
				storeInfo.setCITY(rs.getString("CITY"));
				storeInfo.setCOUNTRY(rs.getString("COUNTRY"));
				storeInfo.setSTATIC_ENABLED(rs.getString("STATISTICS_ENABLED").replaceAll("[a-b]\\|'", ""));

				lstStoreInfos.add(storeInfo);

			}

		} catch (SQLException e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}

		return lstStoreInfos;

	
	} 
	

	public List<StickerSize> getStickerSizeConfiguration_inf_R5_4_13(String request) {

		List<StickerSize> lstStickerSize = new ArrayList<StickerSize>();
		StickerSize stickerSize;
		Statement st = null;
		ResultSet rs = null;
		

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				
				stickerSize = new StickerSize();

				stickerSize.setID(rs.getString("ID"));
				stickerSize.setNAME(rs.getString("NAME"));
				stickerSize.setHIGHT(rs.getString("HIGHT"));
				stickerSize.setWIDTH(rs.getString("WIDTH"));

				lstStickerSize.add(stickerSize);

			}

		} catch (SQLException e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}

		return lstStickerSize;

	}

	public List<PrintModelConfiguration> getPrintModelConfiguration_inf_R5_4_13(String request) {

		List<PrintModelConfiguration> lstPrintModelConfiguration = new ArrayList<PrintModelConfiguration>();
		PrintModelConfiguration printModelConfiguration;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				
				printModelConfiguration = new PrintModelConfiguration();

				printModelConfiguration.setID(rs.getString("ID"));
				printModelConfiguration.setPRINT_HOST_ID(rs.getString("PRINTHOST_ID"));
				printModelConfiguration.setDEFAULT_MODEL(rs.getString("DEFAULT_MODEL"));
				printModelConfiguration.setMODEL(rs.getString("MODEL"));
				printModelConfiguration.setPRINTER(rs.getString("PRINTER"));
				printModelConfiguration.setSTICKERSIZE_ID(rs.getString("STICKERSIZE_ID"));

				lstPrintModelConfiguration.add(printModelConfiguration);
			}

		} catch (SQLException e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}

		return lstPrintModelConfiguration;

	}

	public List<PrintHostConfiguration> getPrintHostConfiguration_inf_R5_4_13(String request) {

		List<PrintHostConfiguration> lstPrintHostConfiguration = new ArrayList<PrintHostConfiguration>();
		PrintHostConfiguration printHostConfiguration;
		Statement st = null;
		ResultSet rs = null;
	

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				
				printHostConfiguration = new PrintHostConfiguration();

				printHostConfiguration.setID(rs.getString("ID"));
				printHostConfiguration.setHOSTNAME(rs.getString("HOSTNAME"));
			
				printHostConfiguration.setSUBCELL(rs.getString("SUBCELL"));

				printHostConfiguration.setALIAS(rs.getString("ALIAS"));

				lstPrintHostConfiguration.add(printHostConfiguration);
			}

		} catch (SQLException e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());
			logger.error("error during select request SQLException" + e.getMessage());
			logger.fatal("Migration aborted");
			System.exit(1);
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn("unable to close Recordset !!!");

				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}

		return lstPrintHostConfiguration;
	}


	public List<GeoStore> getGeoStoreList(String request) {

		List<GeoStore> lstGeoStore = new ArrayList<GeoStore>();
		GeoStore geoStore;

		Statement st = null;
		ResultSet rs = null;

		try{
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);


			while (rs.next()) {

				geoStore = new GeoStore();
				geoStore.setID(rs.getString("ID"));
				geoStore.setMAP(rs.getString("MAP"));
				geoStore.setCREATED(rs.getString("CREATED"));
				geoStore.setROUTING(rs.getString("ROUTING"));
				lstGeoStore.add(geoStore);

			}


			} catch (SQLException e) {

				System.out.println("error during select request SQLException" + e.getMessage());

				e.printStackTrace();
			}

		finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("error during select request SQLException" + e.getMessage());
					}
				}

				if (st != null) {

					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("error during select request SQLException" + e.getMessage());

					}
				}

			}
			return lstGeoStore;

		}



	public List<BaseStation> getBaseStationList(String request) {

		List<BaseStation> lstBaseStation = new ArrayList<BaseStation>();
		BaseStation baseStation;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;
		//int i = 0;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

			
				baseStation = new BaseStation();

				
				
				baseStation.setBSNAME(rs.getString("BSNAME"));
				baseStation.setBSADDRESS(rs.getString("BSADDRESS"));
				baseStation.setBSHWID(rs.getString("BSHWID"));
				//baseStation.setBSALIAS(rs.getString("BSALIAS"));
				lstBaseStation.add(baseStation);
			}

		} catch (SQLException e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}
		return lstBaseStation;

	}


	public List<Item> getItemList(String request) {

		List<Item> lstItem = new ArrayList<Item>();
		Item item;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;


		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {


				item = new Item();

				item.setArrow(rs.getString("ARROW"));
				item.setBox(rs.getString("BOX"));
				item.setCircle(rs.getString("CIRCLE"));
				item.setCodeBarre(rs.getString("CODE_BARRE"));
				item.setCodeGeo(rs.getString("CODE_GEO"));
				item.setCoeff(rs.getString("COEFF"));
				item.setdSign(rs.getString("D_SIGN"));
				item.setCross(rs.getString("CROSS1"));
				item.setSquare(rs.getString("SQUARE"));
				item.setIntItemCode(rs.getString("INT_ITEM_CODE"));
				item.setItemID(rs.getString("ITEMID"));
				item.setItemIpf(rs.getString("ITEMIPF"));
				item.setItemDptIdRef(rs.getString("ITEMDPTIDREF"));
				item.setItemGroup(rs.getString("ITEM_GROUP"));
				item.setPrice(rs.getString("PRICE"));
				item.setUnitCode(rs.getString("UNIT_CODE"));
				item.setUnitPrice(rs.getString("UNIT_PRICE"));
				item.setUnitPriceText(rs.getString("UNIT_PRICE_TEXT"));
				item.setItemName(rs.getString("ITEM_NAME"));
				item.setItemName2(rs.getString("ITEM_NAME_2"));
				item.setItemName3(rs.getString("ITEM_NAME_3"));
				item.setItemName4(rs.getString("ITEM_NAME_4"));
				item.setItemName5(rs.getString("ITEM_NAME_5"));
				item.setItemDesc1(rs.getString("ITEM_DESC_1"));
				item.setItemDesc2(rs.getString("ITEM_DESC_2"));
				item.setItemDesc3(rs.getString("ITEM_DESC_3"));
				item.setItemDesc4(rs.getString("ITEM_DESC_4"));
				item.setItemDesc5(rs.getString("ITEM_DESC_5"));
				item.setItemDesc6(rs.getString("ITEM_DESC_6"));
				item.setItemDesc7(rs.getString("ITEM_DESC_7"));
				item.setItemDesc8(rs.getString("ITEM_DESC_8"));
				item.setItemDesc9(rs.getString("ITEM_DESC_9"));
				item.setItemDesc10(rs.getString("ITEM_DESC_10"));
				item.setItemDesc11(rs.getString("ITEM_DESC_11"));
				item.setSupplierName(rs.getString("SUPPLIER_NAME"));
				item.setItemSize(rs.getString("ITEM_SIZE"));
				item.setDeee(rs.getString("DEEE"));
				item.setTaxExcludingPrice(rs.getString("TAX_EXCLUDING_PRICE"));
				item.setLibelleDeee(rs.getString("LIBELLE_DEEE"));
				item.setLibelleEco(rs.getString("LIBELLE_ECO"));
				item.setRetailerPackageSize(rs.getString("RETAILER_PACKAGE_SIZE"));
				item.setDiscountFlag(rs.getString("DISCOUNT_PRICE_FLAG"));
				item.setIlot(rs.getString("ILOT"));
				item.setEmplacement(rs.getString("EMPLACEMENT"));
				item.setFormalLabel(rs.getString("FORMAT_LABEL"));
				item.setStockDispo(rs.getString("STOK_DISPO"));
				item.setStockTheo(rs.getString("STOK_THEO"));
				item.setQtyInOrder(rs.getString("QTY_IN_ORDER"));
				item.setOrderingQty(rs.getString("ORDERING_QTY"));
				item.setDateDeliv(rs.getString("DATE_DELIV"));
				item.setItemStatus(rs.getString("ITEM_STATUS"));


				lstItem.add(item);
			}

		} catch (SQLException e) {

			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during select request SQLException" + e.getMessage());

				}
			}

		}
		return lstItem;

	}

	public List<Subcell> getSubcellListFrom_R3(String request) {

		List<Subcell> lstSubcell = new ArrayList<Subcell>();
		Subcell subcell;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				subcell = new Subcell();

				subcell.setSUBCELLID(rs.getString("SUBCELLID"));
				subcell.setSUBISBACKOFFICE(rs.getString("SUBISBACKOFFICE"));
				subcell.setSUBCELLMODE(rs.getString("SUBCELLMODE"));
				subcell.setSUBBSNAME(rs.getString("SUBBSNAME"));
				subcell.setSUBCELLTRANSMISSIONTIME(rs.getString("SUBCELLTRANSMISSIONTIME"));
				subcell.setSUBCELLCREATIONTIME(rs.getString("SUBCELLCREATIONTIME"));
				subcell.setLASTCALIBRATIONTIME(rs.getString("LASTCALIBRATIONTIME"));

				lstSubcell.add(subcell);
			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}
		return lstSubcell;

	}

	public List<Subcell> getSubcellListFrom_inf_R5_4_13(String request) {

		List<Subcell> lstSubcell = new ArrayList<Subcell>();
		Subcell subcell;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				subcell = new Subcell();

				subcell.setSUBCELLID(rs.getString("SUBCELLID"));
				subcell.setSUBISBACKOFFICE(rs.getString("SUBISBACKOFFICE"));
				subcell.setSUBCELLMODE(rs.getString("SUBCELLMODE"));
				subcell.setSUBBSNAME(rs.getString("SUBBSNAME"));
				//subcell.setSUBCELLTRANSMISSIONTIME(rs.getString("SUBCELLTRANSMISSIONTIME"));
				//subcell.setSUBCELLCREATIONTIME(rs.getString("SUBCELLCREATIONTIME"));
				//subcell.setLASTCALIBRATIONTIME(rs.getString("LASTCALIBRATIONTIME"));
				subcell.setSUBCELLALIAS(rs.getString("SUBCELLALIAS"));
				lstSubcell.add(subcell);
			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}
		return lstSubcell;

	}

	public List<SubcellTRXMap> getSubcellListTRXMAPFrom_inf_R5_4_13(String request) {

		List<SubcellTRXMap> lstSubcellTRXMap = new ArrayList<SubcellTRXMap>();
		SubcellTRXMap subcellTRXMap;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				subcellTRXMap = new SubcellTRXMap();

				subcellTRXMap.setTRXID((rs.getString("ID")));
				subcellTRXMap.setSCMSUBCELLID((rs.getString("SCMSUBCELLID")));
				subcellTRXMap.setTRXPORTNUM(rs.getString("TRXPORTNUM"));
				lstSubcellTRXMap.add(subcellTRXMap);
			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}
		return lstSubcellTRXMap;

	}

	public List<SubcellTRXMap> getSubcellListTRXMAPFrom_R3(String request) {

		List<SubcellTRXMap> lstSubcellTRXMap = new ArrayList<SubcellTRXMap>();
		SubcellTRXMap subcellTRXMap;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				subcellTRXMap = new SubcellTRXMap();

				subcellTRXMap.setSCMTRXIDREF(rs.getString("SCMTRXIDREF"));
				subcellTRXMap.setSCMSUBCELLID((rs.getString("SCMSUBCELLID")));
				subcellTRXMap.setTRXPORTNUM(rs.getString("TRXPORTNUM"));
				lstSubcellTRXMap.add(subcellTRXMap);
			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}
		return lstSubcellTRXMap;

	}

	public List<LinkDepartment> getLinkDepartmentListFrom_sup_R5_4_13(String request) {

		List<LinkDepartment> lstLinkDepartment = new ArrayList<LinkDepartment>();
		LinkDepartment linkDepartment;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				linkDepartment = new LinkDepartment();

				linkDepartment.setID(rs.getString("ID"));
				linkDepartment.setALIAS(rs.getString("ALIAS"));
				linkDepartment.setTRX_GROUP(rs.getString("TRX_GROUP"));
				linkDepartment.setIS_BACKOFFICE(rs.getString("iS_BACKOFFICE"));
				lstLinkDepartment.add(linkDepartment);
			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}
		return lstLinkDepartment;

	}

	public List<Transceiver> getTRXListFrom_inf_R5_4_13(String request) {

		// define the new request "update" for setting

//select TRXID,TRXBSNAMEREF,TRXPORTNUM,TRXHWID from TRANSCEIVER;	
		List<Transceiver> lstTRXList = new ArrayList<Transceiver>();
		Transceiver trx;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				trx = new Transceiver();

				trx.setID(rs.getString("ID"));
				trx.setTRXBSNAMEREF(rs.getString("TRXBSNAMEREF"));
				trx.setTRXPORTNUM(rs.getString("TRXPORTNUM"));
				trx.setTRXCABLEINDEX("1");
				trx.setTRXHWID(rs.getString("TRXHWID"));
				trx.setTRXLOCATION(rs.getString("TRXLOCATION"));

				lstTRXList.add(trx);

			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		return lstTRXList;

	}

	public List<Transceiver> getTRXListFrom_sup_R5_4_13(String request) {

		// define the new request "update" for setting

//select TRXID,TRXBSNAMEREF,TRXPORTNUM,TRXHWID from TRANSCEIVER;	
		List<Transceiver> lstTRXList = new ArrayList<Transceiver>();
		Transceiver trx;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				trx = new Transceiver();

				trx.setID(rs.getString("ID"));
				trx.setTRXBSNAMEREF(rs.getString("TRXBSNAMEREF"));
				trx.setTRXPORTNUM(rs.getString("TRXPORTNUM"));
				trx.setTRXCABLEINDEX("1");
				trx.setTRXHWID(rs.getString("TRXHWID"));
				trx.setTRXSTATUS("1");
				trx.setTRXFAILTIME("1970-01-01 01:00:00");
				trx.setTRXSWID("NULL");
				trx.setTRXHWSTATUS("NULL");
				trx.setTRXDELAY("0");
				trx.setTRXLEDCHAIN("NULL");
				trx.setTRXTRSHMULT("0");
				trx.setTRXPARTYPE("0");
				trx.setTRXTHRESHOLD("0");
				trx.setTRXIQOFFSET("NULL");
				lstTRXList.add(trx);

			}

		} catch (SQLException e) {
			
			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		return lstTRXList;

	}

	public List<Transceiver> getTRXListFrom_R3(String request) {

		// define the new request "update" for setting

//select TRXID,TRXBSNAMEREF,TRXPORTNUM,TRXHWID from TRANSCEIVER;
		List<Transceiver> lstTRXList = new ArrayList<Transceiver>();
		Transceiver trx;
		// int id=0;

		Statement st = null;
		ResultSet rs = null;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {
				// id+=1;
				trx = new Transceiver();

				trx.setID(rs.getString("TRXID"));
				trx.setTRXBSNAMEREF(rs.getString("TRXBSNAMEREF"));
				trx.setTRXPORTNUM(rs.getString("TRXPORTNUM"));
				trx.setTRXCABLEINDEX("1");
				trx.setTRXHWID(rs.getString("TRXHWID"));
				trx.setTRXSTATUS("1");
				trx.setTRXFAILTIME("1970-01-01 01:00:00");
				trx.setTRXSWID("NULL");
				trx.setTRXHWSTATUS("NULL");
				trx.setTRXDELAY("0");
				trx.setTRXLEDCHAIN("NULL");
				trx.setTRXTRSHMULT("0");
				trx.setTRXPARTYPE("0");
				trx.setTRXTHRESHOLD("0");
				trx.setTRXIQOFFSET("NULL");
				lstTRXList.add(trx);

			}

		} catch (SQLException e) {

			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		return lstTRXList;

	}

	public void createFileExportLink_sup_R5_4_13(String request, String filename) {

		File file = new File(filename);
		PrintStream PSFileExportLink = null;
		String encoding = "ISO-8859-1";

		try {
			try {
				PSFileExportLink = new PrintStream(new BufferedOutputStream(new FileOutputStream(file.getPath(), true)),
						true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		Statement st = null;
		ResultSet rs = null;
		StringBuilder completeLine;
		String lastTRXRSPList;
		String[] lstTRXRSPList;
		String largestKeyValue;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				lastTRXRSPList = rs.getString("PRICERLABEL.LASTTRXRESPONSELIST");

				lstTRXRSPList = lastTRXRSPList.split("]");

				if (lastTRXRSPList.trim().length() > 5) {

					HashMap<Integer, String> map = new HashMap<Integer, String>();

					for (String value : lstTRXRSPList) {
						try {
							// System.out.println("value= " + value);
							map.put(Integer.valueOf(value.split("=")[1]),
									value.split("=")[0].replace("[", "").replace(",", "").replace("|", "-"));
							// System.out.println("adding data in hashmap : " +
							// Integer.valueOf(value.split("=")[1]) + "," + value.split("=")[0].replace("[",
							// "").replace(",", "").replace("|", "-"));
						} catch (IndexOutOfBoundsException iex) {

							// System.out.println("field is empty...");
						}

					}

					Map.Entry<Integer, String> firstEntry = map.entrySet().iterator().next();
					int largestKey = firstEntry.getKey();
					largestKeyValue = firstEntry.getValue();

					for (Map.Entry<Integer, String> map2 : map.entrySet()) {
						int key = map2.getKey();
						if (key > largestKey) {
							largestKey = key;
							largestKeyValue = map2.getValue();
						}
					}
					// System.out.println("Largest Key : " + largestKey);
					// System.out.println("Largest Key Value : " + largestKeyValue);

				}

				else {
					largestKeyValue = "";

				}

				completeLine = new StringBuilder();
				completeLine.append("9999 ").append(rs.getString("item.itemid")).append(" 1 0 |N| 93 0 |")
						.append(rs.getString("PRICERLABEL.PLBARCODE")).append("| -116 0 |").append(largestKeyValue)
						.append("|,");
				PSFileExportLink.println(completeLine.toString());
				PSFileExportLink.flush();

			}

		} catch (SQLException e) {

			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		PSFileExportLink.close();

	}

	public void createFileExportBSIP(String request, String filename) {

		File file = new File(filename);
		PrintStream PSFileExportLink = null;
		String encoding = "ISO-8859-1";

		try {
			try {
				PSFileExportLink = new PrintStream(new BufferedOutputStream(new FileOutputStream(file.getPath(), true)),
						true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		Statement st = null;
		ResultSet rs = null;
		StringBuilder completeLine;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				completeLine = new StringBuilder();
				completeLine.append(rs.getString("line"));
				PSFileExportLink.println(completeLine.toString());
				PSFileExportLink.flush();

			}

		} catch (SQLException e) {

			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		PSFileExportLink.close();

	}





	public void createFileExportLinkFromMysqlDump(String tableName) {

		String FolderForExtractedData = ini.get("SERVER", "PRICER_RESULT_FOLDER");
		String cmdLine  = "mysqldump.exe -u " + username + " -P " + portnumber + " pricer" + " " + tableName ;
		String resultFile = FolderForExtractedData + "\\" + tableName + ".sql";
		logger.info("mysqldump for table : " + tableName);
		ExecuteDump(cmdLine, resultFile);
		logger.info("mysqldump for table " + tableName + " ok ");

	}




	public void createFileExportLink(String request, String filename) {

		File file = new File(filename);
		PrintStream PSFileExportLink = null;
		String encoding = "ISO-8859-1";

		try {
			try {
				PSFileExportLink = new PrintStream(new BufferedOutputStream(new FileOutputStream(file.getPath(), true)),
						true, encoding);

			} catch (UnsupportedEncodingException ex) {
				System.out.println("UnsupportedEncodingException " + file.getName());

			}
		} catch (FileNotFoundException ex) {
			System.out.println("FileNotFoundException " + file.getName());

		}

		Statement st = null;
		ResultSet rs = null;
		StringBuilder completeLine;

		try {
			st = connectionSource.createStatement();
			rs = st.executeQuery(request);

			while (rs.next()) {

				completeLine = new StringBuilder();
				completeLine.append("9999 ")
				.append(rs.getString("item.itemid"))
				.append(" 1 0 |N| 93 0 |")
				.append(rs.getString("PRICERLABEL.PLBARCODE")).append("|")
				.append(" 9100 0 |").append(rs.getString("PRICERLABEL.PLHOMESUBCELLID"))
				.append("|,");
				PSFileExportLink.println(completeLine.toString());
				PSFileExportLink.flush();

			}

		} catch (SQLException e) {

			System.out.println("error during update request SQLException" + e.getMessage());

			e.printStackTrace();
		}

		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());
				}
			}

			if (st != null) {

				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("error during update request SQLException" + e.getMessage());

				}
			}

		}

		PSFileExportLink.close();

	}
	
	
	
	
	
	public boolean ExecuteRequestUpdate(String request){
		
		logger.info("Execute request : " + request);
				
				
				
			boolean result = false ;	
				try {
					
					PreparedStatement ps=null;
					ps = connectionSource.prepareStatement(request);	
					ps.executeUpdate();	 
					result = true; 
					logger.info("request executed successfully ...");
					
				} catch (SQLException e) {
					
					logger.error("error during update request SQLException" +  e.getMessage ());         
					result = false;
				}


				finally {
					if (connectionSource!=null) {
						try {	connectionSource.close();	} catch (SQLException e) {
			                              //e.printStackTrace();
			                              logger.error("unable to close connection :" +  e.getMessage ());
			                          }
					}
				
				
					
				}

			 return result;       
			  
			         
			     } 


		public void UpdateSystemParameterMessageFilePath(){
			   
			String request = "replace INTO `systemparameter` (`KEY_NAME`, `VALUE`) VALUES ('MESSAGE_FILE_PATH','D:\\Applicat\\Data\\Pricer\\PFIFiles\\MessageFiles\\NEWPFI')";
			logger.info("Executing request : " + request);
			 
			try {
				
				PreparedStatement ps=null;
				ps = connectionSource.prepareStatement(request);	
				ps.executeUpdate();	 
				logger.info("execution successfull...");
				 
			} catch (SQLException e) {
				
				//System.out.println("error during update request SQLException" +  e.getMessage ());
				logger.error("error during update request SQLException :" +  e.getMessage ());           
				
				e.printStackTrace();
			}


			finally {
				if (connectionSource!=null) {
					try {	connectionSource.close();	} catch (SQLException e) {
		                             // e.printStackTrace();
		                              logger.error("unable to close connection :" +  e.getMessage ());
		                          }
				}
			
			
				
			}

		    
		        
		  
		         
		     }

	
		
		
		
		
		
		private static void ExecuteDump(String commandLine, String resultFile) {

			logger.info("mysql dump Execution : " + commandLine);

			try {

				
				Runtime rt = Runtime.getRuntime();
				Process process = rt.exec(commandLine);

				InputStream is=process.getInputStream();     
		        FileOutputStream fos=new FileOutputStream(resultFile);        
		        int ch;
		        while((ch=is.read())!=-1) {            
		               fos.write(ch);
		        }    
		        fos.close();
		        is.close();   

			} catch (Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
			}

		}
			

			


		
}