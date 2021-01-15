package com.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;


import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import com.pricer.Start;







public class OperationOnDBTarget {

	private Connection connectionTarget;
	private Wini ini;
	private String hostNameTarget;
	private String usernameTarget;
	private String passwordTarget;
	private String portnumberTarget;

	static Logger logger = Logger.getLogger(Start.class);

	public OperationOnDBTarget() {

		super();

		try {
			this.ini = new Wini(new File("preference.ini"));
		} catch (InvalidFileFormatException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		} catch (IOException e1) {
			
			logger.fatal("Unable to Read ini File : preference.ini ==> " + e1.getMessage());
			logger.fatal("Exit Application ...");
			System.exit(1);
		}

		this.hostNameTarget = ini.get("MYSQL_TARGET", "HOSTNAME", String.class);
		this.portnumberTarget = ini.get("MYSQL_TARGET", "PORTNAME", String.class);
		this.usernameTarget = ini.get("MYSQL_TARGET", "USER", String.class);
		this.passwordTarget = ini.get("MYSQL_TARGET", "PASSWORD", String.class);
		
		
		System.out.println("operation on DB Target initialisation.....");
		JDBCConnector jdbcconnectorTarget = new JDBCConnector(this.hostNameTarget, this.usernameTarget, this.passwordTarget, this.portnumberTarget);
		this.connectionTarget = jdbcconnectorTarget.connectDatabase();
		//this.currentDir = System.getProperty("user.dir");

	}
	


	
	public Connection getConnectionTarget() {
		return connectionTarget;
	}
	
	public void setConnectionTarget(Connection connectionTarget) {
		this.connectionTarget = connectionTarget;
	}


	
	public void importData(String resultDirectory, String sqlFile) {
		
		
		try {
		ScriptRunner sr = new ScriptRunner(connectionTarget);
		
		Reader reader = new BufferedReader(new FileReader(resultDirectory+"\\"+sqlFile));
		
		sr.runScript(reader);
		
		} catch (Exception e) {
			
			System.out.println("error during select request SQLException" + e.getMessage());

			e.printStackTrace();
		}
		
	}

		
}