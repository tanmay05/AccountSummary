//============================================================================
//
// This software is the property of Vodafone Network Pty Limited and/or
// Vodafone New Zealand Limited and is protected by copyright under the laws
// of Australia, New Zealand and other countries. Unathorised reproduction or
// distribution of this software, or any part of it, is prohibited.
//
// MODULE      : vf_promotion_validity_notification
//
// DESCRIPTION : This class is the constants file to hold all the constant values for application
//
//=============================================================================
// Document History
//
// DATE   /  CHANGE                                           /  Author
//========/===================================================/================
//31.01.18/  MOB-4418 Initial Version                         / Vijayalakshmi
//--------/---------------------------------------------------/----------------
//
//
package com.vodafone.promotionValidity;

public class Constants {
	static String const_payload = "<?xml version='1.0' encoding='UTF-8'?>\n"+
		"<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/'>\n"+
		"   <soapenv:Header xmlns:v1='http://group.vodafone.com/contract/vho/header/v1'>\n"+
		"	  <wsse:Security xmlns:wsse='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd' xmlns:wsu='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd' soapenv:mustUnderstand='1'>\n"+
		"		<wsse:UsernameToken wsu:Id='$GATEWAY_USER_TOKEN'>\n"+
		"			<wsse:Username>$GATEWAY_USERNAME</wsse:Username>\n"+
		"			<wsse:Password Type='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'>$GATEWAY_PASSWORD</wsse:Password>\n"+
		"            <wsse:Nonce EncodingType='http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary'>1SakYRvXbnWwCn7rF8/V6Q==</wsse:Nonce>\n"+
		"            <wsu:Created>$DATE</wsu:Created>\n"+
		"		</wsse:UsernameToken>\n"+
		"	</wsse:Security>\n"+
		"      <v1:Source>\n"+
		"         <v1:System>$SRC_SYSTEM</v1:System>\n"+
		"      </v1:Source>\n"+
		"      <v1:ServiceDocumentation>\n"+
		"         <v1:Implementation>\n"+
		"            <v1:Version>VF-INT-004</v1:Version>\n"+
		"         </v1:Implementation>\n"+
		"      </v1:ServiceDocumentation>\n"+
		"	</soapenv:Header>\n"+
		"   <soapenv:Body>\n"+
			"      <ReminderNotificationRequest xmlns='$SERVICE_URL' xmlns:ns3='http://group.vodafone.com/schema/vbo/technical/communication/v1' xmlns:v12='http://group.vodafone.com/schema/common/v1' xmlns:v13='http://group.vodafone.com/schema/vbo/customer/customer-interaction/v1' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='$SERVICE_URL/Resources/ReminderNotification.xsd'>\n"+
			"         <CreateCommunicationFlag>true</CreateCommunicationFlag>\n"+
			"         <CreateCustomerInteractionFlag>true</CreateCustomerInteractionFlag>\n"+
			"         <CreateCustomerInteractionVBMRequest>\n"+
			"            <DestinationSystem>Siebel</DestinationSystem>\n"+
			"            <CustomerInteractionVBO>\n"+
			"				<v12:IDs>\n"+
			"					<v12:ID  schemeName='SiebelBillingAccountNumber'></v12:ID>\n"+
			"				</v12:IDs>\n"+
			"				<v12:Desc>Promotion Expiry Notification sent</v12:Desc>\n"+
			"				<v12:Type>Promotion Expiry Notification</v12:Type>\n"+
			"				<v12:Categories>\n"+
			"					<v12:Category listName='Category'>System Generated</v12:Category>\n"+
			"					<v12:Category listName='Comment'><![CDATA[$BODY]]></v12:Category>\n"+
			"				</v12:Categories>\n"+
			"				<v13:Parts>\n"+
			"					<v13:Channel actionCode='ADD'>\n"+
			"						<v12:IDs>\n"+
			"							<v12:ID>Web</v12:ID>\n"+
			"						</v12:IDs>\n"+
			"					</v13:Channel>\n"+
			"					<v13:ContactPoints>\n"+
			"						<v13:ContactPoint actionCode='ADD'>\n"+
			"							<v12:Telephone>\n"+
			"								<v12:SubscriberNumber></v12:SubscriberNumber>\n"+
			"							</v12:Telephone>\n"+
			"						</v13:ContactPoint>\n"+
			"					</v13:ContactPoints>\n"+
			"				<v13:Extension/>\n"+
			"				</v13:Parts>\n"+
			"			</CustomerInteractionVBO>\n"+
			"         </CreateCustomerInteractionVBMRequest>\n"+
			"         <CreateCommunicationVBMRequest>\n"+
			"            <DestinationSystem>$DES_SYSTEM</DestinationSystem>\n"+
			"            <CommunicationVBO actionCode='ADD'>\n"+
			"               <Roles xmlns='http://group.vodafone.com/schema/vbo/technical/communication/v1'>\n"+
			"                  <Sender actionCode='ADD'>\n"+
			"                     <Extension>\n"+
			"                        <Email xmlns='http://group.vodafone.com/schema/extension/vbo/technical/communication/v1'>\n"+
			"                           <FullAddress xmlns='http://group.vodafone.com/schema/common/v1'>$FROM_EMAIL_ADDRESS</FullAddress>\n"+
			"                        </Email>\n"+
			"                     </Extension>\n"+
			"                  </Sender>\n"+
			"               </Roles>\n"+
			"               <Parts xmlns='http://group.vodafone.com/schema/vbo/technical/communication/v1'>\n"+
			"                  <ContactPoints>\n"+
			"                     <ContactPoint actionCode='ADD'>\n"+
			"                        <Type xmlns='http://group.vodafone.com/schema/common/v1'>Sent To</Type>\n"+
			"                        <Email>\n"+
			"                           <FullAddress xmlns='http://group.vodafone.com/schema/common/v1'>$TO_EMAIL_ADDRESS</FullAddress>\n"+
			"                        </Email>\n"+
			"                     </ContactPoint>\n"+
			"                  </ContactPoints>\n"+
			"                  <Body>\n"+
			"                     <Text><![CDATA[$BODY]]></Text>\n"+
			"                     <Content actionCode='ADD'>\n"+
			"                        <Desc xmlns='http://group.vodafone.com/schema/common/v1'>$SUBJECT</Desc>\n"+
			"                     </Content>\n"+
			"                  </Body>\n"+
			"               </Parts>\n"+
			"            </CommunicationVBO>\n"+
			"         </CreateCommunicationVBMRequest>\n"+
			"      </ReminderNotificationRequest>\n"+
		    "   </soapenv:Body>\n"+
		    "</soapenv:Envelope>\n";
}
