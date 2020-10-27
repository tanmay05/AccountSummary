//============================================================================
// 
// This software is the property of Vodafone Network Pty Limited and/or
// Vodafone New Zealand Limited and is protected by copyright under the laws
// of Australia, New Zealand and other countries. Unathorised reproduction or
// distribution of this software, or any part of it, is prohibited.
//
// MODULE      : vf_promotion_validity_notification
//
// DESCRIPTION : This class implements Runnable - all the operations required to be executed by each thread
//
//============================================================================
// Document History
//
// DATE   /  CHANGE                                           /  Author
//========/===================================================/===============
//31.01.18/  MOB-4418 Initial Version                         / Vijayalakshmi
//--------/---------------------------------------------------/---------------
//
//
package com.vodafone.promotionValidity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.apache.log4j.Logger;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import com.portal.pcm.fields.FldAccountNo;
import com.portal.pcm.fields.FldEmailAddr;
import com.portal.vodafone.VfFldNotifyEmail;
import com.portal.pcm.fields.FldPoid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class CreateCustomerInteraction implements Runnable {
	FList threadFList = null;
	String input_folder = null;
	Properties prop = new Properties();

	public CreateCustomerInteraction(FList threadFList,String input_folder,Properties prop){
		this.threadFList = threadFList;
		this.prop = prop;
		this.input_folder = input_folder;
	}

	static Logger log = AccountSummary.log;
	static String serviceURL = AccountSummary.serviceURL;
	static String serviceAction = AccountSummary.serviceAction;
	static String gatewayUserToken = AccountSummary.gatewayUserToken;
	static String gatewayUsername = AccountSummary.gatewayUsername;
	static String gatewayPassword = AccountSummary.gatewayPassword;

	/**
	 * This is the MTA worker function 
	 */
	public void run() {

		long threadId = Thread.currentThread().getId();
		// Call Main Function
		try {
			sendNotification(threadId);
		} 
		catch (EBufException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This is the main function to process the notification 
	 * @param threadId
	 * @throws EBufException 
	 */
	private void sendNotification(long threadId) throws EBufException
	{
		String html_file = "";
		PortalContext ctxp = null;
		Poid vf_promotion_expiry_notify_poid = null;

		//Validate the html template and update accordingly to trigger the mail Customer.
		try {
			vf_promotion_expiry_notify_poid = threadFList.get(FldPoid.getInst());
			log.info("Thread ID : " + threadId + " : Starting to process for Account Number :" + threadFList.get(FldAccountNo.getInst()));

			log.info("Thread ID : " + threadId + " : Generation of promotion expiry notification html start");
			html_file = create_html_file_for_notification(threadId, threadFList);
			log.info("Thread ID : " + threadId + " : Generation of promotion expiry notification html end");

			String To_EmailAddress = threadFList.get(FldEmailAddr.getInst());
			String response = sendNotificationEmail(threadId,html_file,To_EmailAddress,prop,log);
			Object lock1 = new Object();
			synchronized (lock1) {
				ctxp = AccountSummary.available_connections.elementAt(0);
				AccountSummary.available_connections.removeElement(ctxp);
				AccountSummary.busy_connections.addElement(ctxp);
			}	

			//call function to update the /vf_promotion_expiry_notify object.
			update_promotion_expiry_notify(threadId,ctxp, vf_promotion_expiry_notify_poid,response);

		}
		catch(EBufException ex){
			log.error("Failure in Extracting information",ex);
			update_promotion_expiry_notify(threadId, ctxp, vf_promotion_expiry_notify_poid,"2");
			ex.printStackTrace();
		}
		catch(Exception ex){
			log.error("Some Unknown Exception",ex);
			update_promotion_expiry_notify(threadId,ctxp,vf_promotion_expiry_notify_poid,"2");
			ex.printStackTrace();
		}

	}

	/**
	 * This function reads the html content generated and sends the email. 
	 * @param threadId 
	 * @param htmlFile 
	 * @param toEmailAddress
	 * @param prop 
	 * @param log
	 * @return status
	 */
	private String sendNotificationEmail(long threadId, String htmlFile, String toEmailAddress,Properties prop,
			Logger log) {

		String htmlText = "";

		String status = "1";	
		String subject = "An update about your SKY channels";

		log.info("Thread ID : " + threadId + " : Calling readHTMLContent() for reading the HTML file...");

		htmlText = readHTMLContent(threadId,htmlFile,log);

		if (htmlText.equals("FAILED") || htmlText == "")
		{
			log.error("Thread ID : " + threadId + " : Unable to Read the HTML content...");
			status = "2";
		}

		else
		{
			String fromEmailAddress = prop.getProperty("EmailSourceAddress");

			String response = SendEmail(htmlText,subject,fromEmailAddress,toEmailAddress,prop,log);

			log.info("Response : " + response);
			if (response.contains("FAILED") || response.contains("HTTP response code: 500"))
			{
				log.error("Setting the Email Status Failure Code 2: Email Sending Failure from Adapter");
				status = "2";
			}
			else
			{
				log.debug("Email Sent Successfully...");
				status = "1";
			}

		}

		return status;

	}

	/**
	 * This function is to prepare the payload and send the email. 
	 * @param htmlText 
	 * @param toEmailAddress
	 * @param fromEmailAddress
	 * @param subject 
	 * @param prop 
	 * @param Log 
	 */
	private String SendEmail(String htmlText, String subject,
			String fromEmailAddress, String toEmailAddress, Properties prop,
			Logger log) {

		OutputStream ostream = null;
		BufferedReader br = null;
		String respText = "";
		String payload = "";
		HttpURLConnection conn = null;

		try
		{

			payload = createEmailPayload(prop, fromEmailAddress, toEmailAddress, htmlText, subject);

			URL url = new URL(serviceURL);
			conn = (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			log.info("Service URL : " + serviceURL);
			log.info("Service Action : " + serviceAction);
			log.info("Payload : \n" + payload);
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.setRequestProperty("SOAPAction", serviceAction);
			conn.setRequestProperty("Content-Length", (new StringBuilder()).append(payload.length()).append("").toString());
			conn.setRequestMethod("POST");
			ostream = conn.getOutputStream();
			ostream.write(payload.getBytes());
			ostream.flush();
			ostream.close();
			InputStreamReader reader = new InputStreamReader(conn.getInputStream());
			br = new BufferedReader(reader);
			for(String line = br.readLine(); line != null; line = br.readLine())
				respText = (new StringBuilder()).append(respText).append(line).toString();

			br.close();
			respText = respText.replace("\t", "\\t");
			respText = respText.replace("\r", "\\r");
			respText = respText.replace("\n", "\\n");
		} catch(MalformedURLException e) {
			log.error(e.getMessage());
			respText = (new StringBuilder()).append("FAILED").toString();
			if(ostream != null)
				ostream = null;
			if(br != null)
				br = null;
			if(conn != null)
				conn = null;
		} catch(IOException e) {
			log.error(e.getMessage());
			respText = (new StringBuilder()).append("FAILED").toString();
			if(ostream != null)
				ostream = null;
			if(br != null)
				br = null;
			if(conn != null)
				conn = null;
		}
		return respText;

	}


	/**
	 * This function is to prepare the payload. 
	 * @param body 
	 * @param toEmailAddress
	 * @param fromEmailAddress
	 * @param subject 
	 * @param prop 
	 * @return payload
	 */
	private String createEmailPayload(Properties prop,
			String fromEmailAddress, String toEmailAddress, String body,
			String subject) {

		String sourceSystem = prop.getProperty("EmailSourceSystem");
		String destinationSystem = prop.getProperty("EmailDestinationSystem");
		String gatewayUserToken = prop.getProperty("XMLGatewayUsernameToken");
		String gatewayUsername = prop.getProperty("XMLGatewayUsername");
		String gatewayPassword = prop.getProperty("XMLGatewayPassword");

		String payload = Constants.const_payload;
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("New Zealand"));

		//Making changes in the payload

		String [] [] constant_payload_replacement = {
			{"$GATEWAY_USER_TOKEN", gatewayUserToken},
			{"$GATEWAY_USERNAME", gatewayUsername},
			{"$GATEWAY_PASSWORD", gatewayPassword},
			{"$SERVICE_URL", serviceAction},
			{"$FROM_EMAIL_ADDRESS", fromEmailAddress},
			{"$TO_EMAIL_ADDRESS", toEmailAddress},
			{"$SRC_SYSTEM", sourceSystem},
			{"$DATE", df.format(date)},
			{"$DES_SYSTEM", destinationSystem},
			{"$BODY", body},
			{"$SUBJECT", subject}
		};

		//Creating the payload
		for(String[] replacement: constant_payload_replacement) {
			payload = payload.replace(replacement[0],replacement[1]);
		}		

		log.info("Payload from createEmailPayload:\n"+payload);
		return payload;
	}

	/**
	 * Function to update the status Promotion Expiry Notification Object.
	 * 
	 * @param Email_Status -- The status of the email generated
	 * @param threadId
	 * @param ctxp - PortalContext connection
	 * @param vfPromotionExpiryNotifyPoid - one object of /vf_promotion_expiry_notify class
	 * @throws EBufException 
	 */

	private static void update_promotion_expiry_notify(long threadId,
			PortalContext ctxp, Poid vfPromotionExpiryNotifyPoid, String Email_Status) throws EBufException {

		FList update_promotion_expiry_inflist = new FList();
		update_promotion_expiry_inflist.set(FldPoid.getInst(),vfPromotionExpiryNotifyPoid);
		update_promotion_expiry_inflist.set(VfFldNotifyEmail.getInst(), Integer.parseInt(Email_Status));


		//Call Opcode PMC_OP_WRITE_FLDS to update the status
		log.info("Thread ID : " + threadId + " : Update vf_promotion_expiry_notify status Input Flist \n" + update_promotion_expiry_inflist.asString());
		FList update_promotion_expiry_outflist = ctxp.opcode(5, update_promotion_expiry_inflist);
		log.info("Thread ID : " + threadId + " : Update vf_promotion_expiry_notify status Output Flist \n" + update_promotion_expiry_outflist.asString());

	}

	/**
	 * Function to generate HTML file for Promotion Expiry Notification.
	 * 
	 * @param threadFList -- One object of /vf_promotion_expiry_notify class
	 * @param threadId
	 * @throws EBufException
	 * @throws IOException
	 * @return html_filename -- html file name that was created
	 */
	private String create_html_file_for_notification(long threadId,
			FList searchResult) throws IOException, EBufException {

		//Getting Account No

		String account_number = null;
		Random randomGenerator = new Random();
		int random_no_append = 0;

		account_number =  threadFList.get(FldAccountNo.getInst());

		log.info("Thread ID : " + threadId + " : account_number " + account_number );

		FileInputStream fisTargetFile = null;

		//Reading Notification HTML as string and changing desired fields.
		fisTargetFile = new FileInputStream(new File("templates/promo_notify.html"));


		String targetFileStr = org.apache.commons.io.IOUtils.toString(fisTargetFile, "UTF-8");
		targetFileStr = targetFileStr.replace("ACCOUNT_NO",account_number);

		random_no_append = randomGenerator.nextInt(1000000);

		String html_filename = input_folder.concat("/").concat(account_number).concat("_").concat(Integer.toString(random_no_append)).concat(".html");
		PrintWriter out = new PrintWriter(html_filename);
		out.write(targetFileStr);
		out.close();

		return html_filename;
	}


	/**
	 * Function to generate HTML file for Promotion Expiry Notification.
	 * @param threadId
	 * @param loction
	 * @param log
	 * @return htmlText -- return the content present in html file 
	 */

	static String readHTMLContent(long threadId, String location, Logger log)
	{
		String htmlText = "";

		try
		{
			File htmlFile = new File(location);

			htmlText = Files.toString(htmlFile, Charsets.UTF_8);

		}catch (FileNotFoundException e)
		{
			log.error("Thread ID : " + threadId + " : HTML File Not Found...");
			htmlText = "FAILED";
			log.error("Thread ID : " + threadId + " : " + e.getCause());
			log.error("Thread ID : " + threadId + " : " + e.getMessage());
		} catch (IOException e){
			log.error("Thread ID : " + threadId + " : Unable to Read the HTML File...");
			htmlText = "FAILED";
			log.error("Thread ID : " + threadId + " : " + e.getCause());
			log.error("Thread ID : " + threadId + " : " + e.getMessage());
		}

		return htmlText;
	}


}
