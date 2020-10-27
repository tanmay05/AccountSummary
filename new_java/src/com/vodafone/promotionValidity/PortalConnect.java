//============================================================================
// 
// This software is the property of Vodafone Network Pty Limited and/or
// Vodafone New Zealand Limited and is protected by copyright under the laws
// of Australia, New Zealand and other countries. Unathorised reproduction or
// distribution of this software, or any part of it, is prohibited.
//
// MODULE      : vf_promotion_validity_notification
//
// DESCRIPTION : This class opens a thread of PCM connections with BRM.
//              No. of threads opened as per configuration in Infranet.properties file
//
//============================================================================
// Document History
//
// DATE   /  CHANGE                                           /  Author
//========/===================================================/===============
//31.01.18/  MOB-4418 Initial Version                         / Vijayalakshmi
//--------/---------------------------------------------------/----------------
//
//
package com.vodafone.promotionValidity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import com.portal.pcm.EBufException;
import com.portal.pcm.PortalContext;
import com.vodafone.promotionValidity.InfranetFileNotFound;
import com.vodafone.promotionValidity.LoginFailureException;

class PortalConnect {

	/**
	 * This function is the Main function for creating the Vector of Connections
	 * @param available_connections
	 * @param log
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws LoginFailureException 
	 * @throws InfranetFileNotFound 
	 */
	static void openPortalContext(Vector<PortalContext> available_connections, Logger log) throws InterruptedException, IOException, LoginFailureException, InfranetFileNotFound{
		//Read the properties file to get the details for connection
		Properties prop = new Properties();
		InputStream prod_inputStream = null;
		String userName = null;
		String password = null;
		String noOfThreads = null;
		String hostname = null;
		String port = null;
		PortalContext ctxp = null;

		try {
			prod_inputStream = new FileInputStream(new File("./Infranet.properties"));
			prop.load(prod_inputStream);
			userName = prop.getProperty("Username");
			password = prop.getProperty("Password");
			hostname = prop.getProperty("Hostname");
			port = prop.getProperty("Port");
			noOfThreads  = prop.getProperty("Threads");

			for (int i = 0;i<Integer.parseInt(noOfThreads);i++){
				ctxp = getContext(userName, password, hostname, port, log);
				available_connections.add(ctxp);
			}


		} catch (FileNotFoundException e) {
			log.error(e.getCause());
			log.error(e.getMessage());
			throw new FileNotFoundException("File  not Found Exception in PortalConnect class openPortalContext method");
		} catch (IOException e) {
			log.error(e.getCause());
			log.error(e.getMessage());
			throw new IOException("IOException in PortalConnect class IOException method");
		} catch (LoginFailureException e) {
			log.error(e.getCause());
			log.error(e.getMessage());
			throw new LoginFailureException("LoginFailureException in PortalConnect class IOException method");
		} catch (InfranetFileNotFound e) {
			log.error(e.getCause());
			log.error(e.getMessage());
			throw new InfranetFileNotFound("InfranetFileNotFound in PortalConnect class IOException method");
		}
		finally{
			if(prod_inputStream != null)
				prod_inputStream.close();
		}
	}

	/**
	 * This function makes a connection to the CM based on the configuration present in the Infranet.properties files
	 * @param UserId
	 * @param password
	 * @param hostname
	 * @param port
	 * @return
	 * @throws LoginFailureExcpetion
	 * @throws InfranetFileNotFound
	 */
	static PortalContext getContext(String UserId,String  password,String hostname,String port ,Logger log) throws LoginFailureException, InfranetFileNotFound{
		PortalContext ctx = null;
		Properties portalprop = null;

		try{
			ctx = new PortalContext();
			portalprop = new Properties();
			String connect_string = "pcp://" +UserId + ":" + password + "@" + hostname + ":" + port + "/service/admin_client 1";
			log.info("Connecting to BRM using below details \n" + connect_string );
			System.out.println("Connecting to BRM using below details \n" + connect_string );
			portalprop.setProperty("infranet.connection", connect_string);
			portalprop.setProperty("infranet.login.type", "1");
			ctx.connect(portalprop);
		}
		catch (EBufException ex)
		{
			ctx = null;
			if (ex.getMessage().equalsIgnoreCase("ERR_BAD_LOGIN_RESULT"))
			{
				log.error("Username Password is wrong!!! Login Failed",ex);
				throw new LoginFailureException("Username Password is wrong!!! Login Failed");
			}
			else if (ex.getMessage().equalsIgnoreCase("ERR_NAP_CONNECT_FAILED"))
			{
				log.error("BRM is down or Connectivity Issue .",ex);
				throw new LoginFailureException("BRM is down or Connectivity Issue");
			}
			else
			{
				log.error("Some unknown error in creating BRM context",ex);
				//ex.printStackTrace();
				log.error(ex.getCause());
				log.error(ex.getMessage());
				throw new LoginFailureException("Some unknown error in creating BRM context");
			}
		}
		return ctx;
	}
}
