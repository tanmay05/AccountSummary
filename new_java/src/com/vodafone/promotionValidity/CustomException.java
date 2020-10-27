//============================================================================
//  
// This software is the property of Vodafone Network Pty Limited and/or
// Vodafone New Zealand Limited and is protected by copyright under the laws
// of Australia, New Zealand and other countries. Unathorised reproduction or
// distribution of this software, or any part of it, is prohibited.
//
// MODULE      : vf_promotion_validity_notification
//
// DESCRIPTION : Java file for all the custom  exceptions
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

@SuppressWarnings("serial")
class CustomException extends Exception{
	public  CustomException(String message){
		super(message);
	}
}


@SuppressWarnings("serial")
class InfranetConfigNotFound extends Exception{
	InfranetConfigNotFound(String message){
		super(message);
	}
}

@SuppressWarnings("serial")
class LoginFailureException extends Exception{
	LoginFailureException(String message){
		super(message);
	}
}

@SuppressWarnings("serial")
class InfranetFileNotFound extends Exception{
	InfranetFileNotFound(String message){
		super(message);
	}
}

@SuppressWarnings("serial")
class InputFileNotFound extends Exception{
	InputFileNotFound(String message){
		super(message);
	}
}

@SuppressWarnings("serial")
class InputDirectoryCreationFailed extends Exception{
	InputDirectoryCreationFailed(String message){
		super(message);
	}
}

@SuppressWarnings("serial")
class InfranetMalFormed extends Exception{
	InfranetMalFormed(String message){
		super(message);
	}
}
