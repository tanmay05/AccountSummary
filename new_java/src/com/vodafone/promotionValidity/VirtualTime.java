//============================================================================
//  
// This software is the property of Vodafone Network Pty Limited and/or
// Vodafone New Zealand Limited and is protected by copyright under the laws
// of Australia, New Zealand and other countries. Unathorised reproduction or
// distribution of this software, or any part of it, is prohibited.
//
// MODULE      : vf_promotion_validity_notification
//
// DESCRIPTION : This class has a static method which returns the pin_virtual_time of the server
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


import java.util.Date;
import org.apache.log4j.Logger;
import com.portal.pcm.EBufException;
import com.portal.pcm.FList;
import com.portal.pcm.Poid;
import com.portal.pcm.PortalContext;
import com.portal.pcm.fields.FldPoid;
import com.portal.pcm.fields.FldVirtualT;

public class VirtualTime {
	public static Date getPinVirtualTime(PortalContext ctxp, Logger log) throws EBufException{
		FList virtual_time_flist = new FList();
		Date virtual_time = new Date();

		virtual_time_flist.set(FldPoid.getInst(), new Poid(1L, -1L, "/search"));

		log.info("input Flist to get the pin_virtual_time\n" +virtual_time_flist.asString());
		FList virtual_time_out_flist  = ctxp.opcode(38, virtual_time_flist);
		log.info("Output Flist to get the pin_virtual_time\n" +virtual_time_out_flist.asString());

		//Get the virtual time from out flist as return the same
		virtual_time =  virtual_time_out_flist.get(FldVirtualT.getInst());
		return virtual_time;
	}
}


