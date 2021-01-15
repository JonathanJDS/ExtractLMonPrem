package com.pricer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class Service {
	static Logger logger = Logger.getLogger(Service.class);
	private String serviceName;
	
	public String getServiceName() 					{	return serviceName;				}
	public void setServiceName(String serviceName) 	{	this.serviceName = serviceName;	}

	
	
	public Service(String serviceName) {
	
super();
	this.serviceName = serviceName;
	
	
	}

	
	
	public boolean StartService() {


		boolean result = false;

		try {
			logger.info("Starting service " + this.serviceName);
			Process p = Runtime.getRuntime().exec("sc start " + this.serviceName);

		} catch (IOException ex) {
			logger.warn(
					"Unable to Start service : " + this.serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}
	
	
	
	
	

	public boolean StopService() {

		boolean result = false;

		try {
			logger.info("Stopping service " + this.serviceName);
			Process p = Runtime.getRuntime().exec("sc stop " + this.serviceName);

		} catch (IOException ex) {
			logger.warn(
					"Unable to Stop service : " + this.serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}
	
	

	
	public boolean UninstallService() {

		boolean result = false;

		try {
			logger.info("Uninstall service " + this.serviceName);
			Process p = Runtime.getRuntime().exec("sc delete " + this.serviceName);

		} catch (IOException ex) {
			logger.warn(
					"Unable to Uninstall service : " + this.serviceName + " cause : " + ex.getCause() + " " + ex.getMessage());
		}

		return result;
	}
	
	
	
	
	public String GetServiceStatus() {

		try {

			Process p = Runtime.getRuntime().exec("sc query " + this.serviceName);
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
	
	
	
	
	
	
	
	
	
	
	
	
}
