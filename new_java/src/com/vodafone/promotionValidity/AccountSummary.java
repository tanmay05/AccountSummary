package com.vodafone.promotionValidity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import com.portal.pcm.fields.FldAccountNo;
import com.portal.pcm.fields.FldAccountObj;
import com.portal.pcm.fields.FldArgs;
import com.portal.pcm.fields.FldCycleEndT;
import com.portal.pcm.fields.FldDataArray;
import com.portal.pcm.fields.FldDiscountObj;
import com.portal.pcm.fields.FldEmailAddr;
import com.portal.pcm.fields.FldFlags;
import com.portal.pcm.fields.FldName;
import com.portal.pcm.fields.FldNameinfo;
import com.portal.pcm.fields.FldParent;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldResults;
import com.portal.pcm.fields.FldStatus;
import com.portal.pcm.fields.FldTemplate;
import com.portal.vodafone.VfFldNotifyEmail;
import com.portal.vodafone.VfFldPromoCode;

public class AccountSummary {
	
	public static Vector<PortalContext> available_connections = new Vector<PortalContext>();
	public static Vector<PortalContext> busy_connections = new Vector<PortalContext>();
	static Logger log = Logger.getLogger(AccountSummary.class);

	static String serviceURL = "";
	static String serviceAction = "";
	static String gatewayUserToken = "";
	static String gatewayUsername = "";
	static String gatewayPassword = "";


	/**
	 * Entry point for the process search account
	 * @throws InterruptedException 
	 * @throws InputFileNotFound 
	 * @throws InputDirectoryCreationFailed
	 * @throws EBufException
	 *
	 */
	public static void main(String [] args) throws InterruptedException, InputFileNotFound, EBufException, InputDirectoryCreationFailed {
		ExecutorService executor = null;
		PortalContext ctxp = null;

		try{
			//Read properties Files
			PropertyConfigurator.configure("Infranet.properties");
			Properties prop = new Properties();
			InputStream prod_inputStream = new FileInputStream(new File("Infranet.properties"));
			prop.load(prod_inputStream);
			String no_of_thread = prop.getProperty("Threads");
			String day_range = prop.getProperty("Day_Range");
			int FETCH_SIZE = Integer.parseInt(prop.getProperty("FETCH_SIZE"));
			String input_Directory = "generated_html";

			serviceURL = prop.getProperty("ServiceURL");
			serviceAction = prop.getProperty("ServiceAction");
			gatewayUserToken = prop.getProperty("XMLGatewayUsernameToken");
			gatewayUsername = prop.getProperty("XMLGatewayUsername");
			gatewayPassword = prop.getProperty("XMLGatewayPassword");

			if(new File(input_Directory).exists()){
				log.info("input HTML Directory exists .. All the HTML files generated will be saved to "+input_Directory);
			}
			else {
				if (new File(input_Directory).mkdirs()){
					log.info("Input Directory doesn't exist..Creation of new Directory successful.");
				}
				else{
					throw new InputDirectoryCreationFailed("Input Directory creation failed");
				}
			}



			if (no_of_thread == null || day_range == null  ){
				throw new InfranetMalFormed("Missing value in Infranet properties file no_of_thread , day_range , serviceURL ,serviceAction ");
			}

			//Making connections to BRM CM as per the configuration present in Infranet.properties file
			PortalConnect.openPortalContext(available_connections, log);
			
			//Call AccountSummary method to display the account details
			AccountSummary();

			//Closing all the BRM connections as processing is complete
			for (int i = 0 ; i<available_connections.size() ; i ++ ){
				ctxp = available_connections.elementAt(i);
				ctxp.close(true);
			}

			for (int i = 0 ; i<busy_connections.size() ; i ++ ){
				ctxp = busy_connections.elementAt(i);
				ctxp.close(true);
			}

		}
		catch (IOException ex) {
			log.error("IOException Exception",ex);
			ex.printStackTrace();
		} catch (EBufException ex) {
			log.error("EBufException Exception",ex);
			ex.printStackTrace();
		} catch (LoginFailureException ex) {
			log.error("LoginFailureException Exception",ex);
			ex.printStackTrace();
		} catch (InfranetFileNotFound ex) {
			log.error("InfranetFileNotFound Exception",ex);
			ex.printStackTrace();
		} catch (InfranetMalFormed ex) {
			log.error("InfranetMalFormed Exception",ex);
			ex.printStackTrace();
		}
		finally{
			//Closing all the BRM connections
			for (int i = 0 ; i<available_connections.size() ; i ++ ){
				ctxp = available_connections.elementAt(i);
				ctxp.close(true);
			}

			for (int i = 0 ; i<busy_connections.size() ; i ++ ){
				ctxp = busy_connections.elementAt(i);
				ctxp.close(true);
			}
		}
	}

	
	
	private static void AccountSummary() throws EBufException
	{	

		FList AcctSummaryInFlist = null;
		FList AcctSummaryOutFlist = null;
		String template = "select X from /VF_EAI_OP_COL_ACC_SUMMARY where F1=V1";
		PortalContext ctxt = null;

		try {

			ctxt = AccountSummary.available_connections.elementAt(0);
			AccountSummary.available_connections.remove(ctxt);
			AccountSummary.busy_connections.add(ctxt);

			AcctSummaryInFlist = new FList ();
			AcctSummaryInFlist.set(FldPoid.getInst(),new Poid(1L, -1L, "/search"));
			AcctSummaryInFlist.set(FldAccountNo.getInst(),"300000391");
			

			log.info("Check Data Already Exists in /VF_EAI_OP_COL_ACC_SUMMARY Input flist\n" + AcctSummaryInFlist.asString());
			AcctSummaryOutFlist = ctxt.opcode(120198, AcctSummaryInFlist);
			System.out.println("Account number " +AcctSummaryOutFlist);
			log.info("Check Data Already Exists in /VF_EAI_OP_COL_ACC_SUMMARY Output flist\n" + AcctSummaryOutFlist.asString());

			//Making the connection available again for reuse
			AccountSummary.busy_connections.remove(ctxt);
			AccountSummary.available_connections.add(ctxt);
		}
		catch(EBufException ex){
			log.error("Failure in Checking Data Exists in /VF_EAI_OP_COL_ACC_SUMMARY",ex);
			ex.printStackTrace();
		}
		catch(Exception ex){
			log.error("Some Unknown Exception",ex);
			ex.printStackTrace();
		}
		return;
	}

}


